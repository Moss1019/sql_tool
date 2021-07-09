
import java.util.*;

public class ControllerGenerator extends Generator {
  private String classTmpl;
  private String insertTmpl;
  private String selectAllTmpl;
  private String selectByIdTmpl;
  private String selectForParentTmpl;
  private String selectByUniqueTmpl;
  private String updateTmpl;
  private String deleteTmpl;

  public ControllerGenerator(Database db) {
    super(db, "../templates/java/controllers");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    for(Table t: db.getTables()) {
      files.put(t.getPascalName() + "Controller.java", generateController(t));
    }
    return files;
  }

  private String generateController(Table t) {
    return classTmpl
      .replace("{insert}", generateInsert(t))
      .replace("{selectall}", generateSelectAll(t))
      .replace("{selectbyid}", generateSelectById(t))
      .replace("{selectforparent}", generateSelectForParent(t))
      .replace("{selectbyunique}", generateSelectByUnique(t))
      .replace("{update}", generateUpdate(t))
      .replace("{delete}", generateDelete(t))
      .replace("{tablepascal}", t.getPascalName())
      .replace("{tablelower}", t.getLowerName())
      .replace("{rootname}", db.getRootName());
  }

  private String generateInsert(Table t) {
    return insertTmpl;
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl;
  }

  private String generateSelectById(Table t) {
    return selectByIdTmpl;
  }

  private String generateSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(selectForParentTmpl
        .replace("{parentpascal}", pt.getPascalName())
        .replace("{parentprimarylower}", pt.getPrimaryColumn().getLowerName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUniqueTmpl
        .replace("{columnpascal}", c.getPascalName())
        .replace("{pascallower}", c.getLowerName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    return updateTmpl;
  }

  private String generateDelete(Table t) {
    return deleteTmpl;
  }

  @Override
  protected void loadTemplates() {
    classTmpl = loadTemplate("class");
    insertTmpl = loadTemplate("insert");
    selectAllTmpl = loadTemplate("selectall");
    selectByIdTmpl = loadTemplate("selectbyid");
    selectForParentTmpl = loadTemplate("selectforparent");
    selectByUniqueTmpl = loadTemplate("selectbyunique");
    updateTmpl = loadTemplate("update");
    deleteTmpl = loadTemplate("delete");
  }
}