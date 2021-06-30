
import java.util.*;

public class ConfigGenerator extends Generator {
  private String inMemDiConfig;
  private String addScopedTmpl;

  public ConfigGenerator(Database db) {
    super(db, "../templates/dotnet/config");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    StringBuilder bs = new StringBuilder();
    StringBuilder br = new StringBuilder();
    for(Table t: db.getTables()) {
      bs
        .append("\n\t\t\t\t\t\t")
        .append(addScopedTmpl
          .replace("{tablepascal}", t.getPascalName())
          .replace("{type}", "Service"));
      br
        .append("\n\t\t\t\t\t\t")
        .append(addScopedTmpl
          .replace("{tablepascal}", t.getPascalName())
          .replace("{type}", "Repository"));
    }
    files.put("DIConfiguration.cs", inMemDiConfig
      .replace("{services}", bs.toString())
      .replace("{repositories}", br.toString())
      .replace("{rootname}", db.getRootName()));
    return files;
  }

  @Override
  protected void loadTemplates() {
    inMemDiConfig = loadTemplate("inmemdiconfig");
    addScopedTmpl = loadTemplate("addscoped");
  }
}
