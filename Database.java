
import java.util.List;

public class Database {
  private String user;
  private List<Table> tables;

  public Database(String user, List<Table> tables) {
    this.user = user;
    this.tables = tables;
  }

  public String getUser() {
    return user;
  }

  public List<Table> getTables() {
    return tables;
  }
}