
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
      for(Table table: db.getTables()) {
        dbObjects.add("table " + table.getName());
        b
        .append("create table ")
        .append(table.getName())
        .append("(\n");
        int currentColIndex = 0;
        for(Column c: table.getColumns()) {
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
          if(currentColIndex++ < table.getColumns().size() - 1) {
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
      for(Table table: db.getTables()) {
        dbObjects.add("procedure sp_selectAll" + table.getCleanName() + "s");
        procedures.add("sp_selectAll" + table.getCleanName() + "s");
        if(table.isJoiningTable()) {
          // TODO: work here
          // continue;
        } else if (table.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("create procedure sp_selectAll")
        .append(table.getCleanName())
        .append("s () select * from ")
        .append(table.getName())
        .append(";\n");
      }
      return b.toString();
    }

    public String generateSelectParentChildren() {
      StringBuilder b = new StringBuilder();
      for (Table table : db.getTables()) {
        for (Table parentTable: table.getParentTables()) {
          Column primaryColumn = table.getPrimaryColumn();
          b
          .append("delimiter //\n");
          if (table.isJoiningTable()) {
            primaryColumn = table.getPsudoPrimaryColumn();
            if(table.hasJoiningTable()) {
              dbObjects.add("procedure sp_select" + table.getCleanName() + "s");
              procedures.add("sp_select" + table.getCleanName() + "s");
              b
              .append("create procedure sp_select")
              .append(table.getCleanName())
              .append("s(in ")
              .append(primaryColumn.getName())
              .append(" ")
              .append(ColumnEnums.resolveType(primaryColumn.getDataType()))
              .append(")\n")
              .append("begin\n")
              .append("select * from ")
              .append(parentTable.getName())
              .append(" t1 \n")
              .append("where t1.")
              .append(parentTable.getPrimaryColumn().getName())
              .append(" in \n")
              .append("(select ")
              .append(parentTable.getPrimaryColumn().getName())
              .append(" from ")
              .append(table.getName())
              .append(" t2 \n")
              .append("where t2.")
              .append(primaryColumn.getName())
              .append(" = ")
              .append(primaryColumn.getName())
              .append(")")
              .append(";\n")
              .append("end //\n")
              .append("delimiter ;\n");
            } else {
              dbObjects.add("procedure sp_select" + parentTable.getCleanName() + table.getCleanName() + "s");
              procedures.add("sp_select" + parentTable.getCleanName() + table.getCleanName() + "s");
              b
              .append("create procedure sp_select")
              .append(parentTable.getCleanName())
              .append(table.getCleanName())
              .append("s(in ")
              .append(parentTable.getPrimaryColumn().getName())
              .append(" ")
              .append(ColumnEnums.resolveType(parentTable.getPrimaryColumn().getDataType()))
              .append(")\n")
              .append("begin\n")
              .append("select * from ")
              .append(parentTable.getName())
              .append(" t1 \n")
              .append("where t1.")
              .append(parentTable.getPrimaryColumn().getName())
              .append(" in \n")
              .append("(select ")
              .append(primaryColumn.getName())
              .append(" from ")
              .append(table.getName())
              .append(" t2 \n")
              .append("where t2.")
              .append(parentTable.getPrimaryColumn().getName())
              .append(" = ")
              .append(parentTable.getPrimaryColumn().getName())
              .append(")")
              .append(";\n")
              .append("end //\n")
              .append("delimiter ;\n");
            }
            break;
          } else {
            dbObjects.add("procedure sp_select" + parentTable.getCleanName() + table.getCleanName() + "s");
            procedures.add("sp_select" + parentTable.getCleanName() + table.getCleanName() + "s");
            b
            .append("create procedure sp_select")
            .append(parentTable.getCleanName())
            .append(table.getCleanName())
            .append("s(in ")
            .append(parentTable.getPrimaryColumn().getName())
            .append(" ")
            .append(ColumnEnums.resolveType(primaryColumn.getDataType()))
            .append(")\n")
            .append("begin\n")
            .append("select * from ")
            .append(table.getName())
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
      }
      return b.toString();
    }

    public String generateSelectByPKProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table table: db.getTables()) {
        dbObjects.add("procedure sp_select" + table.getCleanName());
        procedures.add("sp_select" + table.getCleanName());
        Column primaryCol = table.getPrimaryColumn();
        if(table.isJoiningTable()) {
          continue;
        } else if (table.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_select")
        .append(table.getCleanName())
        .append("\n")
        .append("(in ")
        .append(primaryCol.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(primaryCol.getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("select * from ")
        .append(table.getName())
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

    public String generateSelectByUniqueColsProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table table: db.getTables()) {
        for(Column col: table.getUniqueCols()) {
          dbObjects.add("procedure sp_select" + table.getCleanName() + "sBy" + col.getCleanName());
          procedures.add("sp_select" + table.getCleanName() + "sBy" + col.getCleanName());
          b
          .append("delimiter //\n")
          .append("create procedure sp_select")
          .append(table.getCleanName())
          .append("sBy")
          .append(col.getCleanName())
          .append("\n(in ")
          .append(col.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(col.getDataType()))
          .append(")\n")
          .append("begin\n")
          .append("select * from ")
          .append(table.getName())
          .append(" t1 where t1.")
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
      for(Table table: db.getTables()) {
        dbObjects.add("procedure sp_insert" + table.getCleanName());
        procedures.add("sp_insert" + table.getCleanName());
        if(table.isJoiningTable()) {
          // TODO: work here
        } else if (table.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_insert")
        .append(table.getCleanName())
        .append("\n(");
        int colIndex = 0;
        for(Column col: table.getNonPrimaryCols()) {
          b
          .append("in ")
          .append(col.getName())
          .append(" ")
          .append(ColumnEnums.resolveType(col.getDataType()));
          if(colIndex++ < table.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b
        .append(")\n")
        .append("begin\n")
        .append("insert into ")
        .append(table.getName())
        .append("(");
        colIndex = 0;
        for(Column col: table.getNonPrimaryCols()) {
          b
          .append(col.getName());
          if(colIndex++ < table.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b.append(") values \n(");
        colIndex = 0;
        for(Column col: table.getNonPrimaryCols()) {
          b
          .append(col.getName());
          if(colIndex++ < table.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b
        .append(");\n")
        .append("select * from ")
        .append(table.getName())
        .append(" t1 where ");
        if(table.isJoiningTable()) {
          colIndex = 0;
          for (Column col: table.getColumns()) {
            b
            .append("t1.")
            .append(col.getName())
            .append(" = ")
            .append(col.getName());
            if (++colIndex < table.getColumns().size()) {
              b.append(" and ");
            }
          }
        } else {
          b
          .append("t1.")
          .append(table.getPrimaryColumn().getName())
          .append(" = last_insert_id()");
        }
        b
        .append(";\nend //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    public String generateDeleteProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table table: db.getTables()) {
        dbObjects.add("procedure sp_delete" + table.getCleanName());
        procedures.add("sp_delete" + table.getCleanName());
        if(table.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (table.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_delete")
        .append(table.getCleanName())
        .append("(in ")
        .append(table.getPrimaryColumn().getName())
        .append(" ")
        .append(ColumnEnums.resolveType(table.getPrimaryColumn().getDataType()))
        .append(")\n")
        .append("begin\n")
        .append("delete from ")
        .append(table.getName())
        .append(" t1 where t1.")
        .append(table.getPrimaryColumn().getName())
        .append(" = ")
        .append(table.getPrimaryColumn().getName())
        .append(";\n")
        .append("end //\n")
        .append("delimiter ;\n");
      }
      return b.toString();
    }

    public String generateUpdateProcedure() {
      StringBuilder b = new StringBuilder();
      for(Table table: db.getTables()) {
        if(table.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (table.hasJoiningTable()) {
          // TODO: work here
        }
        dbObjects.add("procedure sp_update" + table.getCleanName());
        procedures.add("sp_update" + table.getCleanName());
        b
        .append("delimiter //\n")
        .append("create procedure sp_update")
        .append(table.getCleanName())
        .append("(");
        int colIndex = 0;
          for(Column col: table.getColumns()) {
            b
            .append("in ")
            .append(col.getName())
            .append(" ")
            .append(ColumnEnums.resolveType(col.getDataType()));
            if(colIndex++ < table.getColumns().size() - 1) {
              b.append(", ");
            }
          }
        if(table.isJoiningTable()) {
          colIndex = 0;
          b.append(", ");
          for(Column col: table.getColumns()) {
            b
            .append("in old_")
            .append(col.getName())
            .append(" ")
            .append(ColumnEnums.resolveType(col.getDataType()));
            if(colIndex++ < table.getColumns().size() - 1) {
              b.append(", ");
            }
          }
        } 
        b
        .append(")\nbegin\n")
        .append("update ")
        .append(table.getName())
        .append(" \n")
        .append("set ");
        colIndex = 0;
        for(Column col: table.getNonPrimaryCols()) {
          b
          .append(col.getName())
          .append(" = ")
          .append(col.getName());
          if(colIndex++ < table.getNonPrimaryCols().size() - 1) {
            b.append(", ");
          }
        }
        b
        .append("\nwhere ");
        colIndex = 0;
        if(table.isJoiningTable()) {
          for(Column col: table.getNonPrimaryCols()) {
            b
            .append(col.getName())
            .append(" = old_")
            .append(col.getName());
            if(colIndex++ < table.getNonPrimaryCols().size() - 1) {
              b.append(" and ");
            }
          }
        } else {
          b
          .append(table.getPrimaryColumn().getName())
          .append(" = ")
          .append(table.getPrimaryColumn().getName());
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