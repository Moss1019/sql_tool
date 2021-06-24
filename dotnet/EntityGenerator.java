
import java.util.*;


public class EntityGenerator extends Generator {
  private String classTmpl;
  private String fieldTmpl;
  private String childListTmpl;
  private String virtualParentTmpl;

  public EntityGenerator(Database db) {
    super(db, "../templates/dotnet/entities");
  }

  public Map<String, String> generate() {
    Map<String, String> entities = new HashMap<>();
    for(Table t: db.getTables()) {
      entities.put(t.getPascalName() + ".cs", generateEntity(t));
    }
    return entities;
  }

  public String generateEntity(Table t) {
    return classTmpl
      .replace("{rootname}", db.getRootName())
      .replace("{tablepascal}", t.getPascalName())
      .replace("{fields}", generateFields(t))
      .replace("{parent}", generateVirtualParents(t))
      .replace("{childlist}", generateChildLists(t));
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\t\t\t\t")
      .append(fieldTmpl
        .replace("{datatype}", DataTypeUtil.resolvePrimitive(c))
        .replace("{columnpascal}", c.getPascalName()));
      if(++i < t.getColumns().size() || t.hasLists()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  public String generateVirtualParents(Table t) {
    if(db.getDatabaseType() != DatabaseType.Sql) {
      return "";
    }
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append("\t\t\t\t")
      .append(virtualParentTmpl
        .replace("{tablepascal}", pt.getPascalName()))
      .append("\n");
      if(++i < t.getParentTables().size()) {
        b.append("\n");
      }
    }
    return b.toString();
  } 

  public String generateChildLists(Table t) {
    if(db.getDatabaseType() != DatabaseType.Sql) {
      return "";
    }
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table ct: t.getChildTables()) {
      b
      .append("\t\t\t\t")
      .append(childListTmpl
        .replace("{tablepascal}", ct.getPascalName()))
      .append("\n");
      if(++i < t.getChildTables().size()) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  @Override
  public void loadTemplates() {
    classTmpl = loadTemplate("class");
    fieldTmpl = loadTemplate("field");
    childListTmpl = loadTemplate("childlist");
    virtualParentTmpl = loadTemplate("virtualparent");
  }
}
