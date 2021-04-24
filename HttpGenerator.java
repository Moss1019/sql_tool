
import java.util.Map;
import java.util.HashMap;

public class HttpGenerator extends Generator {
  private Database db;
  private String endPoint;

  private String deleteTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String updateTmpl;
  private String selectAllTmpl;
  private String deleteJoinedTmpl;
  private String selectJoinedTmpl;
  private String selectUniqueTmpl;
  private String selectOfParentTmpl;

  public HttpGenerator(Database db, String endPoint) {
    this.db = db;
    this.endPoint = endPoint;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> http = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      b
      .append("import axios from 'axios';\n\n")
      .append("const SERVER_END_POINT = '")
      .append(endPoint)
      .append("';\n\n")
      .append(generateInsert(t))
      .append("\n");
      if(currentLoopedOrJoined) {
      b
      .append(generateDeleteJoined(t))
      .append("\n")
      .append(generateSelectJoined(t))
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
      .append(generateSelectOfParent(t))
      .append("\n")
      .append(generateUpdate(t));
      };
      http.put(t.getLowerName(), b.toString());
    }
    return http;
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarycolnamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{tstype}", DataTypeUtil.resolveTypeScriptType(t.getPrimaryColumn().getDataType()));
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{pk1namecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{pk2namecamel}", t.getJoinedColumn().getCamelName())
      .replace("{pk1tstype}", DataTypeUtil.resolveTypeScriptType(t.getPrimaryColumn().getDataType()))
      .replace("{pk2tstype}", DataTypeUtil.resolveTypeScriptType(t.getJoinedColumn().getDataType()));
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
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{tstype}", DataTypeUtil.resolveTypeScriptType(c.getDataType())));
      if(colIndex++ < t.getUniqueColumns().size() - 1) {
      b.append("\n");
      }
    }
    return b.toString();
  }

  public String generateSelectOfParent(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b
      .append(selectOfParentTmpl
      .replace("{parenttablenamepascal}", pt.getPascalName())
      .replace("{parentcolnamecamel}", pt.getPrimaryColumn().getCamelName())
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{tstype}", DataTypeUtil.resolveTypeScriptType(pt.getPrimaryColumn().getDataType())))
      .append("\n");
    }
    return b.toString();
  }

  public String generateSelectJoined(Table t) {
    StringBuilder b = new StringBuilder();
    int index = t.getIsLooped() ? 0 : 1;
    return selectJoinedTmpl
      .replace("{primarytablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarycolumnnamecamel}", t.getParentTables().get(0).getPrimaryColumn().getCamelName())
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{resulttablenamepascal}", t.getParentTables().get(index).getPascalName());
  }

  public String generateUpdate(Table t) {
    return updateTmpl 
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablenamecamel}", t.getCamelName())
      .replace("{tablenamelower}", t.getLowerName());
  }

  private void loadTemplates() {
    deleteTmpl = loadTemplate("../templates/http", "delete");
    insertTmpl = loadTemplate("../templates/http", "insert");
    selectTmpl = loadTemplate("../templates/http", "select");
    updateTmpl = loadTemplate("../templates/http", "update");
    selectAllTmpl = loadTemplate("../templates/http", "selectall");
    deleteJoinedTmpl = loadTemplate("../templates/http", "deletejoined");
    selectJoinedTmpl = loadTemplate("../templates/http", "selectjoined");
    selectUniqueTmpl = loadTemplate("../templates/http", "selectunique");
    selectOfParentTmpl = loadTemplate("../templates/http", "selectofparent");
  }
}
