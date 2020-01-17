
import java.util.List;
import java.util.ArrayList;

public class SqlGenerator {
    private Database db = null;
    private List<String> dbObjects = null;
    private List<String> procedures = null;

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

    public String generateCreateTables() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        dbObjects.add("table " + t.getName());
        b
        .append("create table ")
        .append(t.getName())
        .append("(\n");
        int currentColIndex = 0;
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
              String tableName = c.getName().substring(0, c.getName().indexOf("_"));
              b
              .append(tableName)
              .append("(")
              .append(c.getName())
              .append(")");
            }
          }
          if(currentColIndex++ < t.getColumns().size() - 1) {
            b.append(",");
          }
          b.append("\n");
        }
        b.append(");\n");
      }
      return b.toString();
    }

    public String generateSelectAllProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        dbObjects.add("procedure sp_selectAll" + t.getCleanName() + "s");
        procedures.add("sp_selectAll" + t.getCleanName() + "s");
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("create procedure sp_selectAll")
        .append(t.getCleanName())
        .append("s () select * from ")
        .append(t.getName())
        .append(";\n");
      }
      return b.toString();
    }

    public String generateSelectParentChildren() {
      StringBuilder b = new StringBuilder();
      for (Table t : db.getTables()) {
        for (Table parentTable: t.getParentTables()) {
          dbObjects.add("procedure sp_select" + parentTable.getCleanName() + t.getCleanName() + "s");
          procedures.add("sp_select" + parentTable.getCleanName() + t.getCleanName() + "s");
          b
          .append("delimiter //\n")
          .append("create procedure sp_select")
          .append(parentTable.getCleanName())
          .append(t.getCleanName())
          .append("s(in ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" ")
          .append(ColumnEnums.resolveType(t.getPrimaryColumn().getDataType()))
          .append(")\n")
          .append("begin\n")
          .append("select * from ")
          .append(t.getName())
          .append("\n")
          .append("where ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" in \n")
          .append("(select ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" from ")
          .append(parentTable.getName())
          .append(" t \n")
          .append("where t.")
          .append(parentTable.getPrimaryColumn().getName())
          .append(" = ")
          .append(parentTable.getPrimaryColumn().getName())
          .append(");\n")
          .append("end //\n")
          .append("delimiter ;\n");
        }
      }
      return b.toString();
    }

    public String generateSelectByPKProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        dbObjects.add("procedure sp_select" + t.getCleanName());
        procedures.add("sp_select" + t.getCleanName());
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_select")
        .append(t.getCleanName())
        .append("\n")
        .append("(in ")
        .append(t.getPrimaryColumn().getName())
        .append(" ")
        .append(ColumnEnums.resolveType(t.getPrimaryColumn().getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("select * from ")
        .append(t.getName())
        .append(" t where t.")
        .append(t.getPrimaryColumn().getName())
        .append(" = ")
        .append(t.getPrimaryColumn().getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    public String generateSelectByUniqueColsProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
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
          .append(" t where t.")
          .append(col.getName())
          .append(" = ")
          .append(col.getName())
          .append(";\n")
          .append("end //\n")
          .append("delimiter ;\n");
        }
      }
      return b.toString();
    }

    public String generateInsertProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        dbObjects.add("procedure sp_insert" + t.getCleanName());
        procedures.add("sp_insert" + t.getCleanName());
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_insert")
        .append(t.getCleanName())
        .append("\n(");
        int colIndex = 0;
        for(Column col: t.getNonPrimaryCols()) {
          b
          .append("in ")
          .append(col.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(col.getDataType()));
          if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
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
          if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b.append(") values \n(");
        colIndex = 0;
        for(Column col: t.getNonPrimaryCols()) {
          b
          .append(col.getName());
          if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b
        .append(");\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    public String generateDeleteProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        dbObjects.add("procedure sp_delete" + t.getCleanName());
        procedures.add("sp_delete" + t.getCleanName());
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
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
        .append(" t where t.")
        .append(t.getPrimaryColumn().getName())
        .append(" = ")
        .append(t.getPrimaryColumn().getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    public String generateUpdateProcedure() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        dbObjects.add("procedure sp_update" + t.getCleanName());
        procedures.add("sp_update" + t.getCleanName());
        b
        .append("delimiter //\n")
        .append("create procedure sp_update")
        .append(t.getCleanName())
        .append("(");
        int colIndex = 0;
          for(Column col: t.getNonPrimaryCols()) {
            b
            .append("in ")
            .append(col.getName())
            .append(" ")
            .append(ColumnEnums.resolveType(col.getDataType()));
            if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
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
            if(colIndex++ < t.getColumns().size() - 1) {
              b.append(", ");
            }
          }
        } 
        b
        .append(")\nbegin\n")
        .append("update ")
        .append(t.getName())
        .append(" \n")
        .append("set ");
        colIndex = 0;
        for(Column col: t.getNonPrimaryCols()) {
          b
          .append(col.getName())
          .append(" = ")
          .append(col.getName());
          if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b
        .append("\nwhere ");
        colIndex = 0;
        if(t.isJoiningTable()) {
          for(Column col: t.getNonPrimaryCols()) {
            b
            .append(col.getName())
            .append(" = old_")
            .append(col.getName());
            if(colIndex++ < t.getNonPrimaryCols().size() - 1) {
              b.append(" and ");
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
      }
      return b.toString();
    }

    public String generateGrants() {
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