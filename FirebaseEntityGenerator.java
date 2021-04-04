
import java.util.Map;
import java.util.HashMap;

public class FirebaseEntityGenerator extends Generator {
  private Database db;

  private String entityTmpl;
  private String entityJoinedTmpl;
  private String getFromMapTmpl;
  private String setToMapTmpl;
  private String fieldTmpl;
  private String getterTmpl;
  private String setterTmpl;

  public FirebaseEntityGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> entities = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      String template = currentLoopedOrJoined ? entityJoinedTmpl : entityTmpl;
      entities.put(t.getPascalName(), template
      .replace("{packagename}", db.getPackageName())
      .replace("{getters}", generateGetters(t))
      .replace("{setters}", generateSetters(t))
      .replace("{serializetomap}", generateSerializeToMap(t))
      .replace("{deserializefrommap}", generateDeserializeFromMap(t))
      .replace("{fields}", generateFields(t))
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarycoljavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarycolcamel}", t.getPrimaryColumn().getCamelName())
      .replace("{secondarycoljavatype}", DataTypeUtil.resolvePrimitiveType(t.getJoinedColumn().getDataType()))
      .replace("{secondarycolcamel}", t.getJoinedColumn().getCamelName()));
    }
    return entities; 
  }

  private String generateGetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      if(colIndex++ != 0) {
      b.append("\n");
      }
      b
      .append(getterTmpl
      .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
      .replace("{columnnamepascal}", c.getPascalName())
      .replace("{columnnamecamel}", c.getCamelName()));
    }
    return b.toString();
  }

  private String generateSetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      if(colIndex++ != 0) {
      b.append("\n");
      }
      b
      .append(setterTmpl
      .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
      .replace("{columnnamepascal}", c.getPascalName())
      .replace("{columnnamecamel}", c.getCamelName()));
    }
    return b.toString();
  }

  private String generateSerializeToMap(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\n\t\t")
      .append(setToMapTmpl
      .replace("{colnamecamel}", c.getCamelName())
      .replace("{insertvalue}", DataTypeUtil.getInsertLine(c)));
    }
    return b.toString();
  }

  private String generateDeserializeFromMap(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getColumns()) {
      b
      .append("\n\t\t")
      .append(getFromMapTmpl
      .replace("{colnamecamel}", c.getCamelName())
      .replace("{extractvalue}", DataTypeUtil.getExtractLine(c)));
    }
    return b.toString();
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getColumns()) {
      b
      .append("\n\t")
      .append(fieldTmpl
      .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
      .replace("{colnamecamel}", c.getCamelName()));
    }
    return b.toString();
  }

  private void loadTemplates() {
    entityTmpl = loadTemplate("../templates/firebaseentity", "entity");
    entityJoinedTmpl = loadTemplate("../templates/firebaseentity", "entityjoined");
    getFromMapTmpl = loadTemplate("../templates/firebaseentity", "getfrommap");
    setToMapTmpl = loadTemplate("../templates/firebaseentity", "settomap");
    fieldTmpl = loadTemplate("../templates/firebaseentity", "field");
    getterTmpl = loadTemplate("../templates", "getter");
    setterTmpl = loadTemplate("../templates", "setter");
  }
}