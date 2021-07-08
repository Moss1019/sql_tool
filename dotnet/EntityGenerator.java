
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
      .replace("{childlist}", generateChildLists(t))
      .replace("{tableattrib}", getTableAttibute(t));
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\t\t")
      .append(fieldTmpl
        .replace("{columnattrib}", getColumnAttribute(c))
        .replace("{datatype}", DataTypeUtil.resolvePrimitive(c))
        .replace("{columnpascal}", c.getPascalName())
        .replace("{datadefault}", DataTypeUtil.resolveDefault(c)));
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
    b.append("\n\n");
    int i = 0;
    for(Table pt: t.getParentTables()) {
      String parentHandle = pt.getPascalName();
      if(t.isLooped() || t.isJoined()) {
        if(i > 0) {
          parentHandle = t.getLoopedJoinedPascal();
        }
      }
      b
      .append("\t\t")
      .append(virtualParentTmpl
        .replace("{parenthandlepascal}", parentHandle)
        .replace("{tablepascal}", pt.getPascalName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  } 

  private String generateChildLists(Table t) {
    StringBuilder b = new StringBuilder();
    b.append("\n\n");
    int i = 0;
    for(Table ct: t.getChildTables()) {
      if(ct.isJoined() || ct.isLooped()) {
        continue;
      }
      b
      .append("\t\t")
      .append(childListTmpl
        .replace("{tablepascal}", ct.getPascalName()));
      if(++i < t.getChildTables().size() - 1) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String getTableAttibute(Table t) {
    if(db.getDatabaseType() == DatabaseType.Sql) {
      return String.format("\n\t[Table(\"%s\")]", t.getPascalName());
    }
    return "";
  }

  private String getColumnAttribute(Column c) {
    if(c.isPrimary()) {
      return "[Key]\n\t\t";
    }
    return "";
  }

  @Override
  protected void loadTemplates() {
    classTmpl = loadTemplate("class");
    fieldTmpl = loadTemplate("field");
    childListTmpl = loadTemplate("childlist");
    virtualParentTmpl = loadTemplate("virtualparent");
  }
}
