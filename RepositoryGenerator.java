
import java.util.Map;
import java.util.HashMap;

public class RepositoryGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String setParamTmpl;
  private String argumentTmpl;
  private String selectOneTmpl;
  private String selectListTmpl;
  private String insertTmpl;
  private String updateTmpl;
  private String deleteTmpl;
  private String deleteJoinedTmpl;

  public RepositoryGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> repositories = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      StringBuilder b = new StringBuilder();
      b
      .append(classTmpl
        .replace("{modelimports}", generateImports(t))
        .replace("{methods}", generateMethods(t))
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{packagename}", db.getPackageName()));
      repositories.put(String.format("%sRepository", t.getPascalName()), b.toString());
    }
    return repositories;
  }

  private String generateImports(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b
      .append("import ")
      .append(db.getPackageName())
      .append(".entity.")
      .append(pt.getPascalName())
      .append(";\n");
    }
    return b.toString();
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(generateSelectOne(t))
    .append(generateSelectList(t))
    .append(generateInsert(t))
    .append(generateUpdate(t));
    if(currentLoopedOrJoined) {
      b.append(generateDeleteJoined(t));
    } else {
      b.append(generateDelete(t));
    }
    return b.toString();
  }

  private String generateSelectOne(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectOneTmpl
      .replace("{methodname}", "select")
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{methodsuffix}", t.getPascalName())
      .replace("{javatype}", "int")
      .replace("{columnname}", t.getPrimaryColumn().getName()))
    .append("\n");
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectOneTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{methodsuffix}", "By" + c.getPascalName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnname}", c.getName()))
      .append("\n");
    }
    return b.toString();
  }
  
  private String generateSelectList(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectListTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{methodsuffix}", "All" + t.getPascalName() + "s")
      .replace("{arguments}", "")
      .replace("{setparams}", ""));
    for(Table pt: t.getParentTables()) {
      b
      .append("\n\n")
      .append(selectListTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{methodsuffix}", "Of" + pt.getPascalName())
        .replace("{arguments}", argumentTmpl
          .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(pt.getPrimaryColumn().getDataType()))
          .replace("{columnnamecamel}", pt.getPrimaryColumn().getCamelName()))  
        .replace("{setparams}", "\n\t\t" + setParamTmpl
          .replace("{columnname}", pt.getPrimaryColumn().getName())
          .replace("{columnnamecamel}", pt.getPrimaryColumn().getCamelName())));
    }
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder b = new StringBuilder();
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getNonPrimaryColumns()) {
      parameters.append(setParamTmpl
        .replace("{columnname}", c.getName())
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getNonPrimaryColumns().size() - 1) {
        parameters.append("\n\t\t");
      }
    }
    b
    .append("\n\n")
    .append(insertTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{parameters}", parameters.toString()));
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      parameters.append(setParamTmpl
        .replace("{columnname}", c.getName())
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        parameters.append("\n\t\t");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append("\n")
    .append(updateTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{parameters}", parameters.toString()));
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(deleteTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private String generateDeleteJoined(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(deleteTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{key1name}", t.getPrimaryColumn().getName())
      .replace("{key2name}", t.getPrimaryColumn().getName())
      .replace("{key1namecamel}", t.getPrimaryColumn().getName())
      .replace("{key2namecamel}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/repository", "class");
    argumentTmpl = loadTemplate("../templates/repository", "argument");
    setParamTmpl = loadTemplate("../templates/repository", "setparameter");
    selectListTmpl = loadTemplate("../templates/repository", "selectlist");
    selectOneTmpl = loadTemplate("../templates/repository", "selectone");
    insertTmpl = loadTemplate("../templates/repository", "insert");
    updateTmpl = loadTemplate("../templates/repository", "update");
    deleteTmpl = loadTemplate("../templates/repository", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/repository", "deletejoined");
  }
}