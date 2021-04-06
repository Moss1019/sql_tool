
import java.util.Map;
import java.util.HashMap;

public class InMemoryRepositoryGenerator extends Generator {
  private Database db;

  private String additionalCollectionTmpl;
  private String baseClassTmpl;
  private String classTmpl;
  private String deleteTmpl;
  private String deleteJoinedTmpl;
  private String insertTmpl;
  private String selectAllTmpl;
  private String selectByPkTmpl;
  private String selectOfParentTmpl;
  private String selectJoinedTmpl;
  private String selectByUniqueTmpl;
  private String updateTmpl;

  public InMemoryRepositoryGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> repos = new HashMap<>();
    repos.put("BaseRepository", baseClassTmpl
      .replace("{packagename}", db.getPackageName()));
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      repos.put(t.getPascalName() + "Repository", classTmpl
      .replace("{methods}", generateMethods(t))
      .replace("{tablenamelower}", t.getLowerName())
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{imports}", generateImports(t))
      .replace("{packagename}", db.getPackageName())
      .replace("{additionalcollections}", generateAdditionalCollections(t)));
    }
    return repos;
  }

  private String generateImports(Table t) {
    if(!currentLoopedOrJoined) {
      return "\n";
    }
    StringBuilder b = new StringBuilder();
    b.append("\nimport {packagename}.util.JoinedRepoObj;\n");
    for(Table pt: t.getParentTables()) {
      b
      .append("import {packagename}.entity.")
      .append(pt.getPascalName())
      .append(";\n");
    }
    return b.toString();
  }

  private String generateAdditionalCollections(Table t) {
    int index = t.getIsJoined() ? 1 : 0;
    return !currentLoopedOrJoined ? "" :
      additionalCollectionTmpl
      .replace("{primarytablenamelower}", t.getParentTables().get(0).getLowerName())
      .replace("{secondarytablenamelower}", t.getParentTables().get(index).getLowerName());
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(generateInsert(t));
    if(currentLoopedOrJoined) {
      b
      .append(generateDeleteJoined(t))
      .append(generateSelectJoined(t));
    } else {
      b
      .append(generateDelete(t))
      .append(generateSelectAll(t))
      .append(generateSelectByPk(t))
      .append(generateOfParent(t))
      .append(generateSelectUnique(t))
      .append(generateUpdate(t));
    }
    return b.toString();
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{key1javatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{key1namecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{key2javatype}", DataTypeUtil.resolvePrimitiveType(t.getJoinedColumn().getDataType()))
      .replace("{key2namecamel}", t.getJoinedColumn().getCamelName());
  }

  private String generateSelectJoined(Table t) {
  int index = t.getIsJoined() ? 1 : 0;
    return selectJoinedTmpl
      .replace("{secondarytablenamepascal}", t.getParentTables().get(index).getPascalName())
      .replace("{primarytablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarkeyjavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarytablenamecamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{keyjavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()));
  }

  private String generateInsert(Table t) {
    return insertTmpl
      .replace("{setprimarykey}", DataTypeUtil.getObjPrimaryKey(t))
      .replace("{setmapkey}", DataTypeUtil.getMapPrimaryKey(t))
      .replace("{tablenamecamel}", t.getCamelName());
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl;
  }

  private String generateSelectByPk(Table t) {
    return selectByPkTmpl
      .replace("{keyjavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{keynamecamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateOfParent(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b
      .append(selectOfParentTmpl 
        .replace("{parenttablenamepascal}", pt.getPascalName())
        .replace("{parentkeyjavatype}", DataTypeUtil.resolvePrimitiveType(pt.getPrimaryColumn().getDataType()))
        .replace("{parentkeynamecamel}", pt.getPrimaryColumn().getCamelName())
        .replace("{parentkeynamepascal}", pt.getPrimaryColumn().getPascalName()));
    }
    return b.toString();
  }

  private String generateSelectUnique(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getUniqueColumns()) {
      b.append(selectByUniqueTmpl
        .replace("{colnamepascal}", c.getPascalName())
        .replace("{coljavatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType())));
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    return updateTmpl
      .replace("{keyjavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{tablenamecamel}", t.getCamelName());
  }

  private void loadTemplates() {
    additionalCollectionTmpl = loadTemplate("../templates/inmemoryrepository", "additionalcollectionnames");
    baseClassTmpl = loadTemplate("../templates/inmemoryrepository", "baseclass");
    classTmpl = loadTemplate("../templates/inmemoryrepository", "class");
    deleteTmpl = loadTemplate("../templates/inmemoryrepository", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/inmemoryrepository", "deletejoined");
    insertTmpl = loadTemplate("../templates/inmemoryrepository", "insert");
    selectAllTmpl = loadTemplate("../templates/inmemoryrepository", "selectall");
    selectByPkTmpl = loadTemplate("../templates/inmemoryrepository", "selectbypk");
    selectOfParentTmpl = loadTemplate("../templates/inmemoryrepository", "selectofparent");
    selectJoinedTmpl = loadTemplate("../templates/inmemoryrepository", "selectjoined");
    selectByUniqueTmpl = loadTemplate("../templates/inmemoryrepository", "selectbyunique");
    updateTmpl = loadTemplate("../templates/inmemoryrepository", "update");
  }
}