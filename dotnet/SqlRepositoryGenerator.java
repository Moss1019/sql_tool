
import java.util.*;

public class SqlRepositoryGenerator extends Generator {
  private String interfaceTmpl;
  private String joinedInterfaceTmpl;
  private String classTmpl;
  private String joinedClassTmpl;
  
  public SqlRepositoryGenerator(Database db) {
    super(db, "../templates/dotnet/entityframework");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    for(Table t: db.getTables()) {
      files.put("I" + t.getPascalName() + "Repository.cs", generateInterface(t));
      files.put(t.getPascalName() + "Repository.cs", generateClass(t));
    }
    return files;
  }

  private String generateInterface(Table t) {
    if(t.isLooped() || t.isJoined()) {
      return joinedInterfaceTmpl;
    } else {
      return interfaceTmpl;
    }
  }

  private String generateClass(Table t) {
    if(t.isLooped() || t.isJoined()) {
      return joinedClassTmpl
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName())
        .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
        .replace("{secondarycamel}", t.getSecondaryColumn().getCamelName())
        .replace("{primarypascal}", t.getPrimaryColumn().getPascalName())
        .replace("{secondarypascal}", t.getSecondaryColumn().getPascalName())
        .replace("{secondarytablepascal}", t.getLoopedJoinedPascal());
    } else {
      return classTmpl;
    }
  }

  @Override
  protected void loadTemplates() {
    interfaceTmpl = loadTemplate("interface");
    joinedInterfaceTmpl = loadTemplate("joinedinterface");
    classTmpl = loadTemplate("class");
    joinedClassTmpl = loadTemplate("joinedclass");
  }
}
