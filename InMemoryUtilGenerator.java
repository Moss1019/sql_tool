
import java.util.Map;
import java.util.HashMap;

public class InMemoryUtilGenerator extends Generator {
  private Database db;

  private String repoObjTmpl;
  private String repoObjJoinedTmpl;

  public InMemoryUtilGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> utils = new HashMap<>();
    utils.put("RepoObj", repoObjTmpl
      .replace("{packagename}", db.getPackageName()));
    utils.put("JoinedRepoObj", repoObjJoinedTmpl
      .replace("{packagename}", db.getPackageName()));
    return utils;
  }

  public void loadTemplates() {
    repoObjTmpl = loadTemplate("../templates/inmemoryutil", "repoobj");
    repoObjJoinedTmpl = loadTemplate("../templates/inmemoryutil", "repoobjjoined");
  }
}
