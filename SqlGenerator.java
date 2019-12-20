
import java.util.List;
import java.util.Optional;

public class SqlGenerator {
    private Database db;

    public SqlGenerator(Database db) {
      this.db = db;
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

    public String generateSelectAllProcedures() {
      return "";
    }
} 