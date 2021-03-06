
import java.util.List;

public class Database {
  private String user;
  private String packageName;
  private List<Table> tables;

  public Database(String user, String packageName, List<Table> tables) {
    this.user = user;
    this.packageName = packageName;
    this.tables = tables;
  }

  public String getUser() {
    return user;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<Table> getTables() {
    return tables;
  }
}