
import java.util.List;

public class Database {
  private List<Table> tables;

  public Database(List<Table> tables) {
    this.tables = tables;
  }

  public List<Table> getTables() {
    return tables;
  }
}