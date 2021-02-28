
import java.util.List;
import java.util.ArrayList;

public class SqlGenerator {
    private Database db = null;
    private List<String> dbObjects = null;
    private List<String> procedures = null;

    private boolean currentIsJoining = false;
    private Table joiningPrimary = null;
    private Table joiningSecondary = null;

    public SqlGenerator(Database db) {
      this.db = db;
      dbObjects = new ArrayList<>();
      procedures = new ArrayList<>();
    }

    public String generateDropDBObjects() {
      StringBuilder b = new StringBuilder();
      for(String obj: dbObjects) {
        b
        .append("drop ")
        .append(obj)
        .append(";\n");
      }
      return b.toString();
    }

    public String generateSql() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        currentIsJoining = t.isJoiningTable();
        if(currentIsJoining) {
          joiningPrimary = Table.getJoiningPrimary(t);
          joiningSecondary = Table.getJoiningSecondary(t);
        }
        b
        .append(generateCreateTables(t))
        .append("\n")
        .append(generateSelectAllProcedures(t))
        .append("\n")
        .append(generateSelectByPKProcedures(t))
        .append("\n")
        .append(generateSelectByUniqueColsProcedures(t))
        .append("\n")
        .append(generateInsertProcedures(t))
        .append("\n")
        .append(generateDeleteProcedures(t))
        .append("\n")
        .append(generateUpdateProcedure(t))
        .append("\n")
        .append(generateSelectParentChildren(t))
        .append("\n");
      }
      b.append(generateGrants());
      return b.toString();
    }

    private String generateCreateTables(Table t) {
      StringBuilder b = new StringBuilder();
      dbObjects.add("table " + t.getName());
      b
      .append("create table ")
      .append(t.getName())
      .append("(\n");
      int colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append("\t")
        .append(c.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(c.getDataType()));
        if(c.getOptions().size() > 0){
          b.append(" ");
        }
        for(ColumnEnums.Option o: c.getOptions()) {
          b
          .append(ColumnEnums.resolveOption(o))
          .append(" ");
          if(o == ColumnEnums.Option.foreignKey) {
            String tName = c.getName().substring(0, c.getName().indexOf("_"));
            b
            .append(tName)
            .append("(")
            .append(c.getName())
            .append(")");
          }
        }
        if(++colIndex < t.getColumns().size()) {
          b.append(",");
        }
        b.append("\n");
      }
      b.append(");\n");
      return b.toString();
    }

    private String generateSelectAllProcedures(Table t) {
      StringBuilder b = new StringBuilder();
      StringBuilder procNameBuilder = new StringBuilder();
      procNameBuilder.append("sp_selectAll");
      if(t.isJoiningTable()) {
        if(t.hasJoiningTable()) {
          procNameBuilder.append(t.getCleanName());
        } else {
          procNameBuilder
          .append(t.getParentTables().get(0).getCleanName())
          .append(t.getCleanName());
        }
      } else {
        procNameBuilder.append(t.getCleanName());
      }
      dbObjects.add("procedure " + procNameBuilder.toString() + "s");
      procedures.add(procNameBuilder.toString() + "s");
      b
      .append("create procedure ")
      .append(procNameBuilder.toString())
      .append("s () select * from ")
      .append(t.getName())
      .append(";\n");
      return b.toString();
    }

    private String generateSelectParentChildren(Table t) {
      StringBuilder b = new StringBuilder();
      if(t.isJoiningTable()) {

      } else {
        for (Table parentTable: t.getParentTables()) {
          Column primaryColumn = t.getPrimaryColumn();
          b
          .append("delimiter //\n");
          dbObjects.add("procedure sp_select" + parentTable.getCleanName() + t.getCleanName() + "s");
          procedures.add("sp_select" + parentTable.getCleanName() + t.getCleanName() + "s");
          b
          .append("create procedure sp_select")
          .append(parentTable.getCleanName())
          .append(t.getCleanName())
          .append("s(in ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" ")
          .append(ColumnEnums.resolveType(primaryColumn.getDataType()))
          .append(")\n")
          .append("begin\n")
          .append("select * from ")
          .append(t.getName())
          .append(" t1 \n")
          .append("where t1.")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" = ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(";\n")
          .append("end //\n")
          .append("delimiter ;\n");
        }
      }
      return b.toString();
    }

// delimiter //
// create procedure sp_selectItemCollaborators(int item_id integer)
// begin
// select * from user t1
// where t1.user_id in
// (select user_id from collaborator t2
// where t2.item_id = item_id);
// end //
// delimiter ;

    private String generateSelectByPKProcedures(Table t) {
      StringBuilder b = new StringBuilder();
      if(currentIsJoining) {
        String procName = "sp_select" + joiningPrimary.getCleanName() + t.getCleanName() + "s";
        dbObjects.add("procedure " + procName);
        procedures.add(procName);
        b
        .append("delimiter //\ncreate procedure ")
        .append(procName)
        .append("\n(in ")
        .append(joiningPrimary.getPrimaryColumn().getName())
        .append(" ")
        .append(ColumnEnums.resolveType(joiningPrimary.getPrimaryColumn().getDataType()))
        .append(")\nbegin\nselect * from ")
        .append(joiningSecondary.getName())
        .append(" t1\nwhere t1.")
        .append(joiningSecondary.getPrimaryColumn().getName())
        .append(" in\n(select ")
        .append(joiningSecondary.getPrimaryColumn().getName())
        .append(" from ")
        .append(t.getName())
        .append(" t2\nwhere t2.")
        .append(joiningPrimary.getPrimaryColumn().getName())
        .append(" = ")
        .append(joiningPrimary.getPrimaryColumn().getName())
        .append(");\nend //\ndelimiter ;\n");
      } else {
        dbObjects.add("procedure sp_select" + t.getCleanName());
        procedures.add("sp_select" + t.getCleanName());
        Column primaryCol = t.getPrimaryColumn();
        b
        .append("delimiter //\ncreate procedure sp_select")
        .append(t.getCleanName())
        .append("\n")
        .append("(in ")
        .append(primaryCol.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(primaryCol.getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("select * from ")
        .append(t.getName())
        .append(" t1 where t1.")
        .append(primaryCol.getName())
        .append(" = ")
        .append(primaryCol.getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    private String generateSelectByUniqueColsProcedures(Table t) {
      StringBuilder b = new StringBuilder();
      for(Column col: t.getUniqueCols()) {
        dbObjects.add("procedure sp_select" + t.getCleanName() + "sBy" + col.getCleanName());
        procedures.add("sp_select" + t.getCleanName() + "sBy" + col.getCleanName());
        b
        .append("delimiter //\n")
        .append("create procedure sp_select")
        .append(t.getCleanName())
        .append("sBy")
        .append(col.getCleanName())
        .append("\n(in ")
        .append(col.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(col.getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("select * from ")
        .append(t.getName())
        .append(" t1 where t1.")
        .append(col.getName())
        .append(" = ")
        .append(col.getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    private String generateInsertProcedures(Table t) {
      StringBuilder b = new StringBuilder();
      StringBuilder procNameBuilder = new StringBuilder();
      procNameBuilder.append("sp_insert");
      if(t.isJoiningTable()) {
        if(t.hasJoiningTable()) {
          procNameBuilder
          .append(t.getCleanName());
        } else {
          procNameBuilder
          .append(t.getParentTables().get(0).getCleanName())
          .append(t.getCleanName());
        }
      } else {
        procNameBuilder
        .append(t.getCleanName());
      }
      dbObjects.add("procedure " + procNameBuilder.toString());
      procedures.add(procNameBuilder.toString());
      b
      .append("delimiter //\n")
      .append("create procedure ")
      .append(procNameBuilder.toString())
      .append("\n(");
      int colIndex = 0;
      for(Column col: t.getNonPrimaryCols()) {
        b
        .append("in ")
        .append(col.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(col.getDataType()));
        if(++colIndex < t.getNonPrimaryCols().size()) {
          b.append(", ");
        }
      }
      b
      .append(")\n")
      .append("begin\n")
      .append("insert into ")
      .append(t.getName())
      .append("(");
      colIndex = 0;
      for(Column col: t.getNonPrimaryCols()) {
        b
        .append(col.getName());
        if(++colIndex < t.getNonPrimaryCols().size()) {
          b.append(", ");
        }
      }
      b.append(") values \n(");
      colIndex = 0;
      for(Column col: t.getNonPrimaryCols()) {
        b
        .append(col.getName());
        if(++colIndex < t.getNonPrimaryCols().size()) {
          b.append(", ");
        }
      }
      b
      .append(");\n")
      .append("select * from ")
      .append(t.getName())
      .append(" t1 where ");
      if(t.isJoiningTable()) {
        colIndex = 0;
        for (Column col: t.getColumns()) {
          b
          .append("t1.")
          .append(col.getName())
          .append(" = ")
          .append(col.getName());
          if (++colIndex < t.getColumns().size()) {
            b.append(" and ");
          }
        }
      } else {
        b
        .append("t1.")
        .append(t.getPrimaryColumn().getName())
        .append(" = last_insert_id()");
      }
      b
      .append(";\nend //\n")
      .append("delimiter ;\n");
      return b.toString();
    }

    private String generateDeleteProcedures(Table t) {
      StringBuilder b = new StringBuilder();
      if(t.isJoiningTable()) {
        StringBuilder procNameBuilder = new StringBuilder();
        if(t.hasJoiningTable()) {
          procNameBuilder
          .append(t.getCleanName());
        } else {
          procNameBuilder
          .append(t.getParentTables().get(0).getCleanName())
          .append(t.getCleanName());
        }
        dbObjects.add("procedure sp_delete" + procNameBuilder.toString());
        procedures.add("sp_delete" + procNameBuilder.toString());
        b
        .append("delimiter //\n")
        .append("create procedure sp_delete")
        .append(procNameBuilder.toString())
        .append("(");
        int colIndex = 0;
        for(Column c: t.getColumns()) {
          b
          .append("in ")
          .append(c.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(c.getDataType()));
          if(++colIndex < t.getColumns().size()) {
            b.append(", ");
          }
        }
        b
        .append(")\n")
        .append("begin\n")
        .append("delete from ")
        .append(t.getName())
        .append(" t1 where ");
        colIndex = 0;
        for(Column c: t.getColumns()) {
          b
          .append("t1.")
          .append(c.getName())
          .append(" = ")
          .append(c.getName());
          if(++colIndex < t.getColumns().size()) {
            b.append(" and ");
          }
        }
        b
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      } else {
        dbObjects.add("procedure sp_delete" + t.getCleanName());
        procedures.add("sp_delete" + t.getCleanName());
        b
        .append("delimiter //\n")
        .append("create procedure sp_delete")
        .append(t.getCleanName())
        .append("(in ")
        .append(t.getPrimaryColumn().getName())
        .append(" ")
        .append(ColumnEnums.resolveType(t.getPrimaryColumn().getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("delete from ")
        .append(t.getName())
        .append(" t1 where t1.")
        .append(t.getPrimaryColumn().getName())
        .append(" = ")
        .append(t.getPrimaryColumn().getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    private String generateUpdateProcedure(Table t) {
      StringBuilder b = new StringBuilder();
      dbObjects.add("procedure sp_update" + t.getCleanName());
      procedures.add("sp_update" + t.getCleanName());
      b
      .append("delimiter //\n")
      .append("create procedure sp_update")
      .append(t.getCleanName())
      .append("(");
      int colIndex = 0;
        for(Column col: t.getColumns()) {
          b
          .append("in ")
          .append(col.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(col.getDataType()));
          if(++colIndex < t.getColumns().size()) {
            b.append(", ");
          }
        }
      if(t.isJoiningTable()) {
        colIndex = 0;
        b.append(", ");
        for(Column col: t.getColumns()) {
          b
          .append("in old_")
          .append(col.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(col.getDataType()));
          if(++colIndex < t.getColumns().size()) {
            b.append(", ");
          }
        }
      } 
      b
      .append(")\nbegin\n")
      .append("update ")
      .append(t.getName())
      .append(" t1 \n")
      .append("set ");
      colIndex = 0;
      for(Column col: t.getNonPrimaryCols()) {
        b
        .append("t1.")
        .append(col.getName())
        .append(" = ")
        .append(col.getName());
        if(++colIndex < t.getNonPrimaryCols().size() - 1) {
          b.append(", ");
        }
      }
      b
      .append("\nwhere t1.");
      colIndex = 0;
      if(t.isJoiningTable()) {
        for(Column col: t.getNonPrimaryCols()) {
          b
          .append(col.getName())
          .append(" = old_")
          .append(col.getName());
          if(++colIndex < t.getNonPrimaryCols().size()) {
            b.append(" and t1.");
          }
        }
      } else {
        b
        .append(t.getPrimaryColumn().getName())
        .append(" = ")
        .append(t.getPrimaryColumn().getName());
      }
      b
      .append(";\nend //\n")
      .append("delimiter ;\n");
      return b.toString();
    }

    private String generateGrants() {
      StringBuilder b = new StringBuilder();
      for(String procName: procedures) {
        b
        .append("grant execute on procedure ")
        .append(procName)
        .append(" to ")
        .append(db.getUser())
        .append(";\n");
      }
      return b.toString();
    }
} 