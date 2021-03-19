
import java.util.Map;
import java.util.HashMap;

public class HttpGenerator extends Generator {
  private Database db;

  private String deleteTmpl;
  private String deleteJoinedTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String selectAllTmpl;
  private String selectJoinedTmpl;
  private String selectUniqueTmpl;
  private String updateTmpl;

  public HttpGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> http = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      b
      .append("import axios from 'axios';\n\n")
      .append("const SERVER_END_POINT = 'http://localhost:8080';\n\n")
      .append(generateInsert(t))
      .append("\n");
      if(currentLoopedOrJoined) {
        b
        .append(generateDeleteJoined(t))
        .append("\n");
      } else {
        b
        .append(generateDelete(t))
        .append("\n")
        .append(generateSelect(t))
        .append("\n")
        .append(generateSelectAll(t))
        .append("\n")
        .append(generateSelectUnqiue(t))
        .append("\n")
        .append(generateUpdate(t));
      };
      http.put(t.getPascalName(), b.toString());
    }
    return http;
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarycolnamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{tablenamelower}", t.getLowerName());
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{pk1namecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{pk2namecamel}", t.getJoinedColumn().getCamelName());
  }

  private String generateInsert(Table t) {
    return insertTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablenamecamel}", t.getCamelName())
      .replace("{tablenamelower}", t.getLowerName());
  }
  
  public String generateSelect(Table t) {
    return selectTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarycolnamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{tablenamelower}", t.getLowerName());
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl  
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablenamelower}", t.getLowerName());
  } 

  public String generateSelectUnqiue(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectUniqueTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{colnamepascal}", c.getPascalName())
        .replace("{colnamecamel}", c.getCamelName())
        .replace("{tablenamelower}", t.getLowerName()));
      if(colIndex++ < t.getUniqueColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  public String generateUpdate(Table t) {
    return updateTmpl 
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablenamecamel}", t.getCamelName())
      .replace("{tablenamelower}", t.getLowerName());
  }

  private void loadTemplates() {
    deleteTmpl = loadTemplate("../templates/http", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/http", "deletejoined");
    insertTmpl = loadTemplate("../templates/http", "insert");
    selectTmpl = loadTemplate("../templates/http", "select");
    selectAllTmpl = loadTemplate("../templates/http", "selectall");
    selectJoinedTmpl = loadTemplate("../templates/http", "selectjoined");
    selectUniqueTmpl = loadTemplate("../templates/http", "selectunique");
    updateTmpl = loadTemplate("../templates/http", "update");
  }
}
