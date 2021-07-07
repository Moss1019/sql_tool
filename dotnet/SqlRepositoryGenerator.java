
import java.util.*;

public class SqlRepositoryGenerator extends Generator {
  private String interfaceTmpl;
  private String iDeleteTmpl;
  private String iInsertTmpl;
  private String iSelectAllTmpl;
  private String iSelectByIdTmpl;
  private String iSelectByUniqueTmpl;
  private String iSelectForParentTmpl;
  private String iUpdateTmpl;
  private String joinedInterfaceTmpl;
  private String classTmpl;
  private String deleteTmpl;
  private String insertTmpl;
  private String selectAllTmpl;
  private String selectByIdTmpl;
  private String selectByUniqueTmpl;
  private String selectForParentTmpl;
  private String updateTmpl;
  private String includeChildTmpl;
  private String joinedClassTmpl;
  private String contextTmpl;
  private String contextCompositeTmpl;
  private String contextSetTmpl;

  public SqlRepositoryGenerator(Database db) {
    super(db, "../templates/dotnet/entityframework");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    files.put(db.getRootName() + "Context.cs", generateContext());
    for(Table t: db.getTables()) {
      files.put("I" + t.getPascalName() + "Repository.cs", generateInterface(t));
      files.put(t.getPascalName() + "Repository.cs", generateClass(t));
    }
    return files;
  }

  private String generateContext() {
    StringBuilder b = new StringBuilder();
    StringBuilder k = new StringBuilder();
    for(Table t: db.getTables()) {
      b
      .append("\n\n\t\t")
      .append(contextSetTmpl
        .replace("{tablepascal}", t.getPascalName()));
      if(t.isJoined() || t.isLooped()) {
        k
        .append("\n\t\t\t")
        .append(contextCompositeTmpl
          .replace("{tablepascal}", t.getPascalName())
          .replace("{primarycolpascal}", t.getPrimaryColumn().getPascalName())
          .replace("{secondarycolpascal}", t.getSecondaryColumn().getPascalName()));
      }
    }
    return contextTmpl
      .replace("{sets}", b.toString())
      .replace("{composites}", k.toString())
      .replace("{rootname}", db.getRootName());
  }

  private String generateInterface(Table t) {
    if(t.isLooped() || t.isJoined()) {
      return joinedInterfaceTmpl
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName())
        .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName())
        .replace("{secondarycolumncamel}", t.getSecondaryColumn().getOwnerTable().getCamelName())
        .replace("{secondarytablepascal}", t.getSecondaryColumn().getForeignTable().getPascalName());
    } else {
      return interfaceTmpl
        .replace("{selectbyid}", generateInterfaceSelectById(t))
        .replace("{selectbyunique}", generateInterfaceSelectByUnique(t))
        .replace("{insert}", generateInterfaceInsert(t))
        .replace("{selectforparent}", generateInterfaceSelectForParent(t))
        .replace("{selectall}", generateInterfaceSelectAll(t))
        .replace("{update}", generateInterfaceUpdate(t))
        .replace("{delete}", generateInterfaceDelete(t))
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName());
    }
  }

  private String generateInterfaceSelectById(Table t) {
    return iSelectByIdTmpl
      .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateInterfaceSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(iSelectByUniqueTmpl
        .replace("{columnpascal}", c.getPascalName())
        .replace("{columncamel}", c.getCamelName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateInterfaceInsert(Table t) {
    return iInsertTmpl;
  }

  private String generateInterfaceSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(iSelectForParentTmpl
        .replace("{parentpascal}", pt.getPascalName())
        .replace("{parentprimarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateInterfaceSelectAll(Table t) {
    return iSelectAllTmpl;
  }

  private String generateInterfaceUpdate(Table t) {
    return iUpdateTmpl;
  }

  private String generateInterfaceDelete(Table t) {
    return iDeleteTmpl
      .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName());
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
        .replace("{secondarytablepascal}", t.getLoopedJoinedPascal())
        .replace("{primarytablepascal}", t.getPrimaryColumn().getForeignTable().getPascalName());
    } else {
      return classTmpl
        .replace("{delete}", generateDelete(t))
        .replace("{insert}", generateInsert(t))
        .replace("{selectall}", generateSelectAll(t))
        .replace("{selectbyid}", generateSelectById(t))
        .replace("{selectbyunique}", generateSelectByUnique(t))
        .replace("{selectforparent}", generateSelectForParent(t))
        .replace("{update}", generateUpdate(t))
        .replace("{childlists}", generateChildLists(t))
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName());
    }
  }

  private String generateDelete(Table t) {
    return deleteTmpl
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarypascal}", t.getPrimaryColumn().getPascalName());
  }

  private String generateInsert(Table t) {
    return insertTmpl;
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl;
  }

  private String generateSelectById(Table t) {
    return selectByIdTmpl
      .replace("{primarycamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarypascal}", t.getPrimaryColumn().getPascalName());
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUniqueTmpl
        .replace("{columnpascal}", c.getPascalName())
        .replace("{columncamel}", c.getCamelName()));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b.append(selectForParentTmpl
        .replace("{parentpascal}", pt.getPascalName())
        .replace("{primarypascal}", pt.getPrimaryColumn().getPascalName())
        .replace("{primarycamel}", pt.getPrimaryColumn().getCamelName()));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      } 
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    return updateTmpl
      .replace("{primarypascal}", t.getPrimaryColumn().getPascalName());
  }

  private String generateChildLists(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      if(ct.isJoined() || ct.isLooped()) {
        continue;
      }
      b
      .append("\n\t\t\t\t\t")
      .append(includeChildTmpl
        .replace("{childtablepascal}", ct.getPascalName()));
    }
    return b.toString();
  }

  @Override
  protected void loadTemplates() {
    interfaceTmpl = loadTemplate("interface");
    iDeleteTmpl = loadTemplate("idelete");
    iInsertTmpl = loadTemplate("iinsert");
    iSelectAllTmpl = loadTemplate("iselectall");
    iSelectByIdTmpl = loadTemplate("iselectbyid");
    iSelectByUniqueTmpl = loadTemplate("iselectbyunique");
    iSelectForParentTmpl = loadTemplate("iselectforparent");
    iUpdateTmpl = loadTemplate("iupdate");    
    joinedInterfaceTmpl = loadTemplate("joinedinterface");
    classTmpl = loadTemplate("class");
    deleteTmpl = loadTemplate("delete");
    insertTmpl = loadTemplate("insert");
    selectAllTmpl = loadTemplate("selectall");
    selectByIdTmpl = loadTemplate("selectbyid");
    selectByUniqueTmpl = loadTemplate("selectbyunique");
    selectForParentTmpl = loadTemplate("selectforparent");
    updateTmpl = loadTemplate("update");
    includeChildTmpl = loadTemplate("includechild");
    joinedClassTmpl = loadTemplate("joinedclass");
    contextTmpl = loadTemplate("context");
    contextCompositeTmpl = loadTemplate("contextcomposite");
    contextSetTmpl = loadTemplate("contextset");
  }
}
