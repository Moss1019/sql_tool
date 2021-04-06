
import java.util.Map;
import java.util.HashMap;

public class RepositoryGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String setParamTmpl;
  private String setParamColTmpl;
  private String argumentTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String selectAllTmpl;
  private String selectByUnqiueTmpl;
  private String selectOfParentTmpl;
  private String selectJoinedTmpl;
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
      repositories.put(String.format("%sRepository", t.getPascalName()), classTmpl
      .replace("{modelimports}", generateImports(t))
      .replace("{methods}", generateMethods(t))
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{packagename}", db.getPackageName()));
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
    .append(generateInsert(t))
    .append("\n");
    if(currentLoopedOrJoined) {
      b
      .append(generateSelectJoined(t))
      .append(generateDeleteJoined(t));
    } else {
      b
      .append(generateSelect(t))
      .append(generateSelectAll(t))
      .append(generateSelectByUnique(t))
      .append(generateSelectOfParent(t))
      .append(generateDelete(t))
      .append(generateUpdate(t));
    }
    return b.toString();
  }

  private String generateSelectJoined(Table t) {
    int index = t.getIsJoined() ? 1 : 0;
    return selectJoinedTmpl
      .replace("{secondarytablenamepascal}", t.getParentTables().get(index).getPascalName())
      .replace("{primarytablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarykeyjavatype}", DataTypeUtil.resolvePrimitiveType(t.getParentTables().get(0).getPrimaryColumn().getDataType()))
      .replace("{primarykeynamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName())
      .replace("{primarykeysetparam}", DataTypeUtil.resolveSetParam(t.getPrimaryColumn().getCamelName(), 
        t.getPrimaryColumn(), false));
  }

  private String generateSelect(Table t) {
    return selectTmpl
      .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarycolnamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primaryname}", t.getPrimaryColumn().getName())
      .replace("{primarycolsetvalue}", DataTypeUtil.resolveSetParam(t.getPrimaryColumn().getCamelName(), t.getPrimaryColumn(), false));
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl
      .replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUnqiueTmpl
        .replace("{colnamepascal}", c.getPascalName())
        .replace("{coljavatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{colnamecamel}", c.getCamelName())
        .replace("{colsetparam}", DataTypeUtil.resolveSetParam(c.getCamelName(), c, false)));
      if(colIndex++ < t.getUniqueColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateSelectOfParent(Table t) {
    StringBuilder b = new StringBuilder();
    int tabIndex = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(selectOfParentTmpl
        .replace("{parenttablenamepascal}", pt.getPascalName())
        .replace("{primarycoljavatype}", DataTypeUtil.resolvePrimitiveType(pt.getPrimaryColumn().getDataType()))
        .replace("{primarycolnamecamel}", pt.getPrimaryColumn().getCamelName())
        .replace("{primarycolname}", pt.getPrimaryColumn().getName())
        .replace("{primarycolsetvalue}", DataTypeUtil.resolveSetParam(pt.getPrimaryColumn().getCamelName(), pt.getPrimaryColumn(), false)));
      if(tabIndex++ < t.getParentTables().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      if(c.getIsAutoIncrement()) {
        ++colIndex;
        continue;
      }
      parameters.append(setParamTmpl
        .replace("{columnname}", c.getName())
        .replace("{columnsetparam}", DataTypeUtil.resolveSetParam(c.getPascalName(), c, true)));
      if(colIndex++ < t.getColumns().size() - 1) {
        parameters.append("\n\t\t");
      }
    }
    return insertTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{parameters}", parameters.toString())
      .replace("{setprimarykey}", DataTypeUtil.getObjPrimaryKey(t))
      .replace("{tablenamecamel}", t.getCamelName());
  }

  private String generateUpdate(Table t) {
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      parameters.append(setParamTmpl
        .replace("{columnname}", c.getName())
        .replace("{columnsetparam}", DataTypeUtil.resolveSetParam(c.getPascalName(), c, true)));
      if(colIndex++ < t.getColumns().size() - 1) {
        parameters.append("\n\t\t");
      }
    }
    return updateTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{parameters}", parameters.toString())
      .replace("{tablenamecamel}", t.getCamelName());
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarykeynamecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName())
      .replace("{primarykeysetparam}", DataTypeUtil.resolveSetParam(t.getPrimaryColumn().getCamelName(), t.getPrimaryColumn(), false));
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{key1name}", t.getPrimaryColumn().getName())
      .replace("{key2name}", t.getJoinedColumn().getName())
      .replace("{key1javatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{key2javatype}", DataTypeUtil.resolvePrimitiveType(t.getJoinedColumn().getDataType()))
      .replace("{key1namecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{key2namecamel}", t.getJoinedColumn().getCamelName())
      .replace("{key1setparam}", DataTypeUtil.resolveSetParam(t.getPrimaryColumn().getCamelName(), t.getPrimaryColumn(), false))
      .replace("{key2setparam}", DataTypeUtil.resolveSetParam(t.getJoinedColumn().getCamelName(), t.getJoinedColumn(), false));
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/repository", "class");
    argumentTmpl = loadTemplate("../templates/repository", "argument");
    setParamTmpl = loadTemplate("../templates/repository", "setparameter");
    setParamColTmpl = loadTemplate("../templates/repository", "setparametercol");
    insertTmpl = loadTemplate("../templates/repository", "insert");
    insertTmpl = loadTemplate("../templates/repository", "insert");
    selectTmpl = loadTemplate("../templates/repository", "select");
    selectAllTmpl = loadTemplate("../templates/repository", "selectall");
    selectByUnqiueTmpl = loadTemplate("../templates/repository", "selectbyunique");
    selectOfParentTmpl = loadTemplate("../templates/repository", "selectofparent");
    selectJoinedTmpl = loadTemplate("../templates/repository", "selectjoined");
    updateTmpl = loadTemplate("../templates/repository", "update");
    deleteTmpl = loadTemplate("../templates/repository", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/repository", "deletejoined");
  }
}