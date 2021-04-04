
import java.util.Map;
import java.util.HashMap;

public class FirebaseRepositoryGenerator extends Generator {
  private Database db;

  private String baseClassTmpl;
  private String classTmpl;
  private String deleteTmpl;
  private String deleteJoinedTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String selectAllTmpl;
  private String selectByUniqueTmpl;
  private String selectOfParentTmpl;
  private String selectLoopedTmpl;
  private String updateTmpl;
  private String repoDepTmpl;

  public FirebaseRepositoryGenerator(Database db) {
  this.db = db;
  loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> repos = new HashMap<>();
    repos.put("BaseRepository", baseClassTmpl
      .replace("{packagename}", db.getPackageName()));
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsLooped() || t.getIsJoined();
      repos.put(String.format("%sRepository", t.getPascalName()), 
      classTmpl
        .replace("{repodeps}", generateRepoDeps(t))
        .replace("{packagename}", db.getPackageName())
        .replace("{methods}", generateMethods(t))
        .replace("{imports}", generateImports(t))
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{tablenamecamel}", t.getCamelName()));
    }
    return repos;
  }

  private String generateRepoDeps(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b.append("\n")
      .append(repoDepTmpl
        .replace("{parenttablepascal}", pt.getPascalName())
        .replace("{parenttablecamel}", pt.getCamelName()));
    }
    b.append("\n");
    return b.toString();
  }

  private String generateImports(Table t) {
    return "";
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(generateInsert(t));
    if(currentLoopedOrJoined) {
      b
      .append(generateDeleteJoined(t))
      .append(generateSelectJoined(t));
    } else {
      b
      .append(generateDelete(t))
      .append(generateSelect(t))
      .append(generateSelectAll(t))
      .append(generateByUnique(t))
      .append(generateSelectOfParent(t));
    }
    return b.toString();
  }

  private String generateInsert(Table t) {
    return insertTmpl
      .replace("{setprimarykey}", DataTypeUtil.getObjPrimaryKey(t))
      .replace("{setmapkey}", DataTypeUtil.getMapPrimaryKey(t));
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{secondaryjavatype}", DataTypeUtil.resolvePrimitiveType(t.getJoinedColumn().getDataType()))
      .replace("{secondarycamel}", t.getJoinedColumn().getCamelName())
      .replace("{primaryjavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
      .replace("{javatype}", "UUID");
  }

  private String generateSelectJoined(Table t) {
    int index = t.getIsJoined () ? 1 : 0;
    return selectLoopedTmpl
      .replace("{primarytablepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarycoljavatype}", DataTypeUtil.resolvePrimitiveType(t.getParentTables().get(0).getPrimaryColumn().getDataType()))
      .replace("{primarycolcamel}", t.getParentTables().get(0).getPrimaryColumn().getCamelName());
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{primarycoljavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()));
  }

  private String generateSelect(Table t) {
    return selectTmpl
      .replace("{primarycoljavatype}", DataTypeUtil.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .replace("{primarycolcamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl;
  }

  private String generateByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUniqueTmpl
      .replace("{colnamepascal}", c.getPascalName())
      .replace("{coljavatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType())));
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
      .replace("{parenttablepascal}", pt.getPascalName())
      .replace("{parentcoljavatype}", DataTypeUtil.resolvePrimitiveType(pt.getPrimaryColumn().getDataType()))
      .replace("{parentprimarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(tabIndex++ < t.getParentTables().size() - 1) {
      b.append("\n");
      }
    }
    return b.toString();
  }

  private void loadTemplates() {
    baseClassTmpl = loadTemplate("../templates/firebaserepository", "baseclass");
    classTmpl = loadTemplate("../templates/firebaserepository", "class");
    deleteTmpl = loadTemplate("../templates/firebaserepository", "delete");
    deleteJoinedTmpl = loadTemplate("../templates/firebaserepository", "deletejoined");
    insertTmpl = loadTemplate("../templates/firebaserepository", "insert");
    selectTmpl = loadTemplate("../templates/firebaserepository", "select");
    selectAllTmpl = loadTemplate("../templates/firebaserepository", "selectall");
    selectByUniqueTmpl = loadTemplate("../templates/firebaserepository", "selectbyunique");
    selectOfParentTmpl = loadTemplate("../templates/firebaserepository", "selectofparent");
    selectLoopedTmpl = loadTemplate("../templates/firebaserepository", "selectlooped");
    updateTmpl = loadTemplate("../templates/firebaserepository", "update");
    repoDepTmpl = loadTemplate("../templates/firebaserepository", "repodep");
  }
}
