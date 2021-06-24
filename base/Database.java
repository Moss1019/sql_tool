
import java.util.*;

public class Database {
  private String rootName;
  private DatabaseType type;
  private List<Table> tables;

  public Database(String rootName, String databaseTypeStr, List<Table> tables) {
    this.rootName = rootName;
    this.tables = tables;
    int databaseType = Integer.parseInt(databaseTypeStr);
    switch(databaseType) {
      case 0:
        type = DatabaseType.InMemory;
        break;
      case 1:
        type = DatabaseType.Sql;
        break;
      case 2:
        type = DatabaseType.Document;
        break;
    }
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for(Table table: tables) {
      b.append("\n");
      b.append(table.toString());
      b.append("\n---------------------------------");
    }
    return b.toString();
  }

  public String getRootName() {
    return rootName;
  }

  public DatabaseType getDatabaseType() {
    return type;
  }

  public List<Table> getTables() {
    return tables;
  }
}