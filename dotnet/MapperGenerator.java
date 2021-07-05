
import java.util.*;

public class MapperGenerator extends Generator {
  private String classTmpl;
  private String viewAssignTmpl;
  private String entityAssignTmpl;
  private String viewListAssignTmpl;
  private String entityListAssignTmpl;

  public MapperGenerator(Database db) {
    super(db, "../templates/dotnet/mappers");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    for(Table t: db.getTables()) {
      files.put(t.getPascalName() + "Mapper.cs", generateMapper(t));
    }
    return files;
  }

  private String generateMapper(Table t) {
    return classTmpl
      .replace("{rootname}", db.getRootName())
      .replace("{tablepascal}", t.getPascalName())
      .replace("{viewassigns}", generateViewAssigns(t))
      .replace("{entityassigns}", generateEntityAssigns(t));
  }

  private String generateViewAssigns(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\t\t\t\t")
      .append(viewAssignTmpl
        .replace("{columnpascal}", c.getPascalName()));
      if(++i < t.getColumns().size()) {
        b.append(",\n");
      }
    }
    for(Table ct: t.getChildTables()) {
      if(ct.isJoined() || ct.isLooped()) {
        continue;
      }
      b
      .append(",\n")
      .append("\t\t\t\t")
      .append(viewListAssignTmpl
        .replace("{columnpascal}", ct.getPascalName() + "s"));
    }
    return b.toString();
  }

  private String generateEntityAssigns(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\t\t\t\t")
      .append(entityAssignTmpl
        .replace("{columnpascal}", c.getPascalName()));
      if(++i < t.getColumns().size()) {
        b.append(",\n");
      }
    }
    for(Table ct: t.getChildTables()) {
      if(ct.isJoined() || ct.isLooped()) {
        continue;
      }
      b
      .append(",\n")
      .append("\t\t\t\t")
      .append(entityListAssignTmpl
        .replace("{columnpascal}", ct.getPascalName() + "s"));
    }
    return b.toString();
  }

  @Override
  protected void loadTemplates() {
    classTmpl = loadTemplate("class");
    viewAssignTmpl = loadTemplate("viewassign");
    entityAssignTmpl = loadTemplate("entityassign");
    viewListAssignTmpl = loadTemplate("viewlistassign");
    entityListAssignTmpl = loadTemplate("entitylistassign");
  }
}
