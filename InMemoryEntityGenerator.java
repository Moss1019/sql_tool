
import java.util.Map;
import java.util.HashMap;

public class InMemoryEntityGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String classJoinedTmpl;
  private String fieldTmpl;
  private String getterTmpl;
  private String setterTmpl;

  public InMemoryEntityGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> entities = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      String template = currentLoopedOrJoined ? classJoinedTmpl : classTmpl;
      entities.put(t.getPascalName(), template
        .replace("{packagename}", db.getPackageName())
        .replace("{fields}", generateFields(t))
        .replace("{getters}", generateGetters(t))
        .replace("{setters}", generateSetters(t))
        .replace("{key1namecamel}", t.getPrimaryColumn().getCamelName())
        .replace("{key2namecamel}", t.getJoinedColumn().getCamelName())
        .replace("{tablenamepascal}", t.getPascalName()));
    }
    return entities;
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getColumns()) {
      b
      .append(fieldTmpl
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()));
    }
    return b.toString();
  }

  private String generateGetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append(getterTmpl
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName())
        .replace("{columnnamepascal}", c.getPascalName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateSetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append(setterTmpl
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName())
        .replace("{columnnamepascal}", c.getPascalName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/inmemoryentity", "class");
    classJoinedTmpl = loadTemplate("../templates/inmemoryentity", "classjoined");
    fieldTmpl = loadTemplate("../templates/inmemoryentity", "field");
    getterTmpl = loadTemplate("../templates", "getter");
    setterTmpl = loadTemplate("../templates", "setter");
  }
}