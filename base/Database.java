
import java.util.*;

public class Database {
  private List<Table> tables;

  public Database(List<Table> tables) {
    this.tables = tables;
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

  public List<Table> getTables() {
    return tables;
  }
}