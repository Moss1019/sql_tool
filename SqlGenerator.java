
import java.util.List;
import java.util.Optional;

public class SqlGenerator {
    private Database db;

    public SqlGenerator(Database db) {
      this.db = db;
    }

    public String generateDropDBObjects() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        b
        .append("drop table ")
        .append(t.getName())
        .append(";\n");
      }
      return b.toString();
    }

    public String generateCreateTables() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
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
              System.out.println(tableName);
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
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("create procedure sp_selectAll")
        .append(t.getPascalName())
        .append("s () select * from ")
        .append(t.getName())
        .append(";\n");
      }
      return b.toString();
    }

    public String generateSelectByPKProcedures() {
      StringBuilder b = new StringBuilder();
      for(Table t: db.getTables()) {
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_select")
        .append(t.getPascalName())
        .append("s\n")
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
          b
          .append("delimiter //\n")
          .append("create procedure sp_select")
          .append(t.getPascalName())
          .append("sBy")
          .append(col.getPascalName())
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
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_insert")
        .append(t.getPascalName())
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
        if(t.isJoiningTable()) {
          // TODO: work here
          continue;
        } else if (t.hasJoiningTable()) {
          // TODO: work here
        }
        b
        .append("delimiter //\n")
        .append("create procedure sp_delete")
        .append(t.getPascalName())
        .append("\n(in ")
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
} 