
import java.util.Map;
import java.util.HashMap;

public class ConfigGenerator extends Generator {
  private Database db;

  private String swaggerTmpl;

  public ConfigGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> configs = new HashMap<>();
    configs.put("SwaggerConfig", swaggerTmpl
      .replace("{packagename}", db.getPackageName()));
    return configs;
  }

  private void loadTemplates() {
    swaggerTmpl = loadTemplate("../templates/config", "swaggerconfig");
  }
}
