
import java.util.*;

public class ServiceGenerator extends Generator {
  private String interfaceTmpl;
  private String iDeleteTmpl;
  private String iInsertTmpl;
  private String iSelectTmpl;
  private String iSelectAllTmpl;
  private String iSelectByUniqueTmpl;
  private String iSelectForParentTmpl;
  private String iUpdateTmpl;
  private String classTmpl;
  private String deleteTmpl;
  private String insertTmpl;
  private String selectByIdTmpl;
  private String selectAllTmpl;
  private String selectByUniqueTmpl;
  private String selectForParentTmpl;
  private String updateTmpl;
  private String joinedClassTmpl;
  private String joinedInterfaceTmpl;

  public ServiceGenerator(Database db) {
    super(db, "../templates/dotnet/services");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    for(Table t: db.getTables()) {
      files.put("I" + t.getPascalName() + "Service.cs", generateInterface(t));
    }
    for(Table t: db.getTables()) {
      files.put(t.getPascalName() + "Service.cs", generateSevice(t));
    }
    return files;
  }

  private String generateInterface(Table t) {
    if(t.isLooped() || t.isJoined()) {
      return joinedInterfaceTmpl
        .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
        .replace("{secondarycamel}", t.getSecondaryColumn().getCamelName())
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName());
    } else {
      return interfaceTmpl
        .replace("{selectbyid}", iSelectTmpl)
        .replace("{insert}", iInsertTmpl)
        .replace("{selectall}", iSelectAllTmpl)
        .replace("{delete}", iDeleteTmpl)
        .replace("{update}", iUpdateTmpl)
        .replace("{selectforparent}", generateInterfaceSelectForParent(t))
        .replace("{selectbyunique}", generateInterfaceSelectByUnique(t))
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName());
    }
  }

  private String generateInterfaceSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append("\t\t\t\t")
      .append(iSelectForParentTmpl
        .replace("{primarypascal}", pt.getPascalName())
        .replace("{primarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateInterfaceSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append("\t\t\t\t")
      .append(iSelectByUniqueTmpl
        .replace("{columncamel}", c.getCamelName())
        .replace("{columnpascal}", c.getPascalName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateSevice(Table t) {
    if(t.isJoined() || t.isLooped()) {
      return joinedClassTmpl
        .replace("{secondarytablepascal}", t.getSecondaryColumn().getForeignTable().getPascalName())
        .replace("{secondarytablecamel}", t.getSecondaryColumn().getForeignTable().getCamelName())
        .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName())
        .replace("{secondarycolumncamel}", t.getSecondaryColumn().getCamelName())
        .replace("{tablepascal}", t.getPascalName())
        .replace("{rootname}", db.getRootName());
    } else {
      return classTmpl
        .replace("{delete}", deleteTmpl
          .replace("{primarycamel}", t.getPrimaryColumn().getCamelName()))
        .replace("{insert}", insertTmpl)
        .replace("{selectall}", selectAllTmpl)
        .replace("{selectbyid}", selectByIdTmpl)
        .replace("{update}", updateTmpl)
        .replace("{selectbyunique}", generateSelectByUnique(t))
        .replace("{selectforparent}", generateSelectForParent(t))
        .replace("{tablepascal}", t.getPascalName())
        .replace("{rootname}", db.getRootName());
    }
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append("\t\t\t\t")
      .append(selectForParentTmpl
        .replace("{parentpascal}", pt.getPascalName())
        .replace("{parentprimarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append("\t\t\t\t")
      .append(selectByUniqueTmpl
        .replace("{columnpascal}", c.getPascalName())
        .replace("{columncamel}", c.getCamelName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  @Override
  protected void loadTemplates() {
    interfaceTmpl = loadTemplate("interface");
    iDeleteTmpl = loadTemplate("idelete");
    iInsertTmpl = loadTemplate("iinsert");
    iSelectTmpl = loadTemplate("iselectbyid");
    iSelectAllTmpl = loadTemplate("iselectall");
    iSelectByUniqueTmpl = loadTemplate("iselectbyunique");
    iSelectForParentTmpl = loadTemplate("iselectforparent");
    iUpdateTmpl = loadTemplate("iupdate");
    classTmpl = loadTemplate("class");
    deleteTmpl = loadTemplate("delete");
    insertTmpl = loadTemplate("insert");
    selectByIdTmpl = loadTemplate("selectbyid");
    selectAllTmpl = loadTemplate("selectall");
    selectByUniqueTmpl = loadTemplate("selectbyunique");
    selectForParentTmpl = loadTemplate(("selectforparent"));
    updateTmpl = loadTemplate("update");  
    joinedClassTmpl = loadTemplate("joinedclass");
    joinedInterfaceTmpl = loadTemplate("joinedinterface");  
  }
}
