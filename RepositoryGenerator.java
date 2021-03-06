
import java.util.Map;
import java.util.HashMap;

public class RepositoryGenerator extends Generator {
  private Database db;

  public RepositoryGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generate() {
    Map<String, String> repositories = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      StringBuilder b = new StringBuilder();

      repositories.put(String.format("%sRepository", t.getPascalName()), b.toString());
    }
    return repositories;
  }
}