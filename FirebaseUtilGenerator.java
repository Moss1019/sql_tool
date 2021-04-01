
import java.util.Map;
import java.util.HashMap;

public class FirebaseUtilGenerator extends Generator{
  private Database db;

  private String firebaseObjTmpl;
  private String joinedMappableTmpl;
  private String mappableTmpl;

  public FirebaseUtilGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> utils = new HashMap<>();
    utils.put("FirestoreObj", firebaseObjTmpl
      .replace("{packagename}", db.getPackageName()));
    utils.put("JoinedMappable", joinedMappableTmpl
      .replace("{packagename}", db.getPackageName()));
    utils.put("Mappable", mappableTmpl
      .replace("{packagename}", db.getPackageName()));
    return utils;
  }

  private void loadTemplates() {
    firebaseObjTmpl = loadTemplate("../templates/firebaseutil", "firestoreobj");
    joinedMappableTmpl = loadTemplate("../templates/firebaseutil", "joinedmappable");
    mappableTmpl = loadTemplate("../templates/firebaseutil", "mappable");
  }
}