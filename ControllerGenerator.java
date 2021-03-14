

import java.util.Map;
import java.util.HashMap;

public class ControllerGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String deleteTmpl;
  private String deleteJoinedTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String selectAllTmpl;
  private String selectOfParentTmpl;
  private String selectJoinedTmpl;
  private String selectUniqueTmpl;
  private String updateTmpl;

  public ControllerGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> controllers = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      controllers.put(t.getPascalName() + "Controller", classTmpl 
        .replace("{packagename}", db.getPackageName())
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{tablenamelower}", t.getLowerName())
        .replace("{methods}", generateMethods(t))
        .replace("{imports}", generateImports(t)));
    }
    return controllers;
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(generateInsert(t))
    .append("\n");
    if(currentLoopedOrJoined) {
      b
      .append(generateDeleteJoined(t))
      .append("\n")
      .append(generateSelectJoined(t));
    } else {
      b
      .append(generateSelectAll(t))
      .append("\n")
      .append(generateSelect(t))
      .append("\n")
      .append(generateSelectUnique(t))
      .append("\n")
      .append(generateSelectOfParent(t))
      .append("\n")
      .append(generateUpdate(t))
      .append("\n")
      .append(generateDelete(t));
    }
    return b.toString();
  }

  private String generateImports(Table t) {
    StringBuilder b = new StringBuilder();
    if(!currentLoopedOrJoined) {
      return "";
    }
    for(Table pt: t.getParentTables()) {
      b
      .append("\n")
      .append("import ")
      .append(db.getPackageName())
      .append(".view.")
      .append(pt.getPascalName())
      .append("View;\n");
    }
    return b.toString();
  }

  private String generateDelete(Table t) {
    return deleteTmpl.replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl;
  }

  private String generateInsert(Table t) {
    return insertTmpl.replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateSelect(Table t) {
    return selectTmpl.replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl
      .replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateSelectOfParent(Table t) {
    StringBuilder b = new StringBuilder();
    int tabIndex = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(selectOfParentTmpl
        .replace("{parenttablenamepascal}", pt.getPascalName())
        .replace("{primarycolumnnamecamel}", pt.getPrimaryColumn().getLowerName())
        .replace("{tablenamepascal}", t.getPascalName()));
    }
    return b.toString();
  }

  private String generateSelectJoined(Table t) {
    int index = t.getIsLooped() ? 0 : 1;
    return selectJoinedTmpl
      .replace("{primarytablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarycolumnnamelower}", t.getParentTables().get(0).getPrimaryColumn().getLowerName())
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{resulttablenamepascal}", t.getParentTables().get(index).getPascalName());
  }

  private String generateSelectUnique(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectUniqueTmpl
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{columnnamecamel}", c.getCamelName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType())));
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    return updateTmpl.replace("{tablenamepascal}", t.getPascalName());
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/controller", "class");
    deleteTmpl = loadTemplate("../templates/controller", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/controller", "deletejoined");
    insertTmpl = loadTemplate("../templates/controller", "insert");
    selectTmpl = loadTemplate("../templates/controller", "select");
    selectAllTmpl = loadTemplate("../templates/controller", "selectall");
    selectOfParentTmpl = loadTemplate("../templates/controller", "selectofparent");
    selectJoinedTmpl = loadTemplate("../templates/controller", "selectjoined");
    selectUniqueTmpl = loadTemplate("../templates/controller", "selectunique");
    updateTmpl = loadTemplate("../templates/controller", "update");
  }
}