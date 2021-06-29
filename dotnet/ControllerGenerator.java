
import java.util.*;

public class ControllerGenerator extends Generator {
  private String classTmpl;
  private String deleteOneTmpl;
  private String getAllTmpl;
  private String getByIdTmpl;
  private String getByUniqueTmpl;
  private String getForParentTmpl;
  private String joiningClassTmpl;
  private String joiningDeleteTmpl;
  private String joiningGetForParentTmpl;
  private String postTmpl;
  private String putTmpl;

  public ControllerGenerator(Database db) {
    super(db, "../templates/dotnet/controllers");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    for(Table t: db.getTables()) {
      files.put(t.getPascalName() + "Controller.cs", generateController(t));
    }
    return files;
  }

  private String generateController(Table t) {
    if(t.isJoined() || t.isLooped()) {
      return joiningClassTmpl
        .replace("{rootname}", db.getRootName())
        .replace("{getforparent}", generateJoiningGetForParent(t))
        .replace("{post}", generatePost(t))
        .replace("{delete}", generateJoiningDelete(t))
        .replace("{tablelower}", t.getLowerName())
        .replace("{tablepascal}", t.getPascalName());
    } else {
      return classTmpl
        .replace("{rootname}", db.getRootName())
        .replace("{getbyid}", generateGetById(t))
        .replace("{getforparent}", generateGetForParent(t))
        .replace("{getall}", generateGetAll(t))
        .replace("{getbyunique}", generateGetByUnqiue(t))
        .replace("{post}", generatePost(t))
        .replace("{delete}", genrateDelete(t))
        .replace("{put}", generatePut(t))
        .replace("{tablepascal}", t.getPascalName())
        .replace("{tablelower}", t.getLowerName());
    }
  }

  private String generateJoiningGetForParent(Table t) {
    return joiningGetForParentTmpl
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarytablepascal}", t.getPrimaryColumn().getForeignTable().getPascalName());
  }

  private String generateJoiningDelete(Table t) {
    return joiningDeleteTmpl
      .replace("{primarylower}", t.getPrimaryColumn().getLowerName())
      .replace("{secondarylower}", t.getSecondaryColumn().getLowerName())
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
      .replace("{secondarycamel}", t.getSecondaryColumn().getLowerName());
  }

  private String generateGetById(Table t) {
    return getByIdTmpl
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateGetForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(getForParentTmpl
        .replace("{primarytablelower}", pt.getLowerName())
        .replace("{parenttablepascal}", pt.getPascalName())
        .replace("{primarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateGetAll(Table t) {
    return getAllTmpl;
  }

  private String generateGetByUnqiue(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(getByUniqueTmpl
        .replace("{columnlower}", c.getLowerName())
        .replace("{columnpascal}", c.getPascalName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generatePost(Table t) {
    return postTmpl;
  }

  private String genrateDelete(Table t) {
    return deleteOneTmpl
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generatePut(Table t) {
    return putTmpl;
  }

  @Override
  protected void loadTemplates() {
    classTmpl = loadTemplate("class");
    deleteOneTmpl = loadTemplate("deleteone");
    getAllTmpl = loadTemplate("getall");
    getByIdTmpl = loadTemplate("getbyid");
    getByUniqueTmpl = loadTemplate("getbyunique");
    getForParentTmpl = loadTemplate("getforparent");
    joiningClassTmpl = loadTemplate("joiningclass");
    joiningDeleteTmpl = loadTemplate("joiningdelete");
    joiningGetForParentTmpl = loadTemplate("joininggetforparent");
    postTmpl = loadTemplate("post");
    putTmpl = loadTemplate("put");
  }
}