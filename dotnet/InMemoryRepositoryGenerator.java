
import java.util.*;

public class InMemoryRepositoryGenerator extends Generator {
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
  private String deleteChildrenTmpl;
  private String insertTmpl;
  private String selectAllTmpl;
  private String selectByIdTmpl;
  private String selectByUniqueTmpl;
  private String selectForParentTmpl;
  private String updateTmpl;
  private String joinedClassTmpl;
  private String childListTmpl;
  private String baseRepoTmpl;
  private String dbContextTmpl;
  private String childRepoTmpl;
  private String childRepoArgTmpl;
  private String childRepoAssignTmpl;

  public InMemoryRepositoryGenerator(Database db) {
    super(db, "../templates/dotnet/" + (db.getDatabaseType() == DatabaseType.InMemory ? "inmemrepositories" :
      db.getDatabaseType() == DatabaseType.Sql ? "sqlrepositories" :
      "documentrepositories"));
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    files.put("BaseRepository.cs", baseRepoTmpl.replace("{rootname}", db.getRootName()));
    files.put("DbContext.cs", dbContextTmpl.replace("{rootname}", db.getRootName()));
    for(Table t: db.getTables()) {
      files.put("I" + t.getPascalName() + "Repository.cs", generateInterface(t));
      files.put(t.getPascalName() + "Repository.cs", generateRepository(t));
    }
    return files;
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

  private String generateRepository(Table t) {
    if(t.isLooped() || t.isJoined()) {
      return joinedClassTmpl
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName())
        .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName())
        .replace("{secondarytablepascal}", t.getSecondaryColumn().getForeignTable().getPascalName())
        .replace("{secondarycolumncamel}", t.getSecondaryColumn().getCamelName())
        .replace("{primarytablepascal}", t.getPrimaryColumn().getForeignTable().getPascalName())
        .replace("{primarytablecamel}", t.getPrimaryColumn().getForeignTable().getCamelName())
        .replace("{primarycolumnpascal}", t.getPrimaryColumn().getPascalName())
        .replace("{secondarycolumnpascal}", t.getSecondaryColumn().getPascalName())
        .replace("{secondarytablelower}", t.getSecondaryColumn().getForeignTable().getLowerName())
        .replace("{joinedcolumnpascal}", t.getSecondaryColumn().getForeignTable().getPrimaryColumn().getPascalName());
    } else {
      return classTmpl
        .replace("{selectbyid}", generateSelectById(t))
        .replace("{selectbyunique}", generateSelectByUnique(t))
        .replace("{insert}", generateInsert(t))
        .replace("{selectall}", generateSelectAll(t))
        .replace("{selectforparent}", generateSelectForParent(t))
        .replace("{update}", generateUpdate(t))
        .replace("{delete}", generateDelete(t))
        .replace("{childrepos}", generateChildRepos(t))
        .replace("{childrepoargs}", generateChildRepoArgs(t))
        .replace("{childrepoassigns}", generateChildRepoAssigns(t))
        .replace("{rootname}", db.getRootName())
        .replace("{tablepascal}", t.getPascalName())
        .replace("{tablelower}", t.getLowerName());
    }
  }

  private String generateSelectById(Table t) {
    return selectByIdTmpl
      .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName())
      .replace("{primarycolumnpascal}", t.getPrimaryColumn().getPascalName())
      .replace("{childlists}", generateChildlist(t))
      .replace("{tabs}", "");
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUniqueTmpl
        .replace("{columnpascal}", c.getPascalName())
        .replace("{columncamel}", c.getCamelName())
        .replace("{childlists}", generateChildlist(t))
        .replace("{tabs}", ""));
      if(++i < t.getUniqueColumns().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateChildlist(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      if(ct.isLooped() || ct.isJoined()) {
        continue;
      }
      b
      .append("\n")
      .append(childListTmpl
        .replace("{childtablepascal}", ct.getPascalName())
        .replace("{primarycolumnpascal}", t.getPrimaryColumn().getPascalName())
        .replace("{childtablecamel}", ct.getCamelName()));
    }
    return b.toString();
  } 

  private String generateInsert(Table t) {
    return insertTmpl;
  }

  private String generateSelectAll(Table t) {
    return selectAllTmpl
      .replace("{childlists}", generateChildlist(t))
      .replace("{tabs}", "\t\t");
  }

  private String generateSelectForParent(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      b
      .append(selectForParentTmpl
        .replace("{parentprimarycamel}", pt.getPrimaryColumn().getCamelName())
        .replace("{parentprimarypascal}", pt.getPrimaryColumn().getPascalName())
        .replace("{parentpascal}", pt.getPascalName())
        .replace("{childlists}", generateChildlist(t))
        .replace("{tabs}", "\t\t"));
      if(++i < t.getParentTables().size()) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    return updateTmpl
      .replace("{primarycolumnpascal}", t.getPrimaryColumn().getPascalName());
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      if(ct.isJoined() || ct.isLooped()) {
        continue;
      }
      b
      .append("\n")
      .append(deleteChildrenTmpl
        .replace("{childtablepascal}", ct.getPascalName())
        .replace("{childtablecamel}", ct.getCamelName())
        .replace("{childprimarypascal}", ct.getPrimaryColumn().getPascalName()));
    }
    return deleteTmpl
      .replace("{primarycolumnpascal}", t.getPrimaryColumn().getPascalName())
      .replace("{primarycolumncamel}", t.getPrimaryColumn().getCamelName())
      .replace("{deletechildren}", b.toString());
  }

  private String generateChildRepos(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      b.append(childRepoTmpl
        .replace("{childtablepascal}", ct.getPascalName())
        .replace("{childtablecamel}", ct.getCamelName()))
      .append("\n");
    }
    return b.toString();
  }

  private String generateChildRepoArgs(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      b.append(childRepoArgTmpl
        .replace("{childtablepascal}", ct.getPascalName())
        .replace("{childtablecamel}", ct.getCamelName()));
    }
    return b.toString();
  }

  private String generateChildRepoAssigns(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      b
      .append("\n\t\t\t")
      .append(childRepoAssignTmpl
        .replace("{childtablecamel}", ct.getCamelName()));
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
    classTmpl = loadTemplate("class");
    deleteTmpl = loadTemplate("delete");
    deleteChildrenTmpl = loadTemplate("deletechildren");
    insertTmpl = loadTemplate("insert");
    updateTmpl = loadTemplate("update");
    selectAllTmpl = loadTemplate("selectall");
    selectByIdTmpl = loadTemplate("selectbyid");
    selectByUniqueTmpl = loadTemplate("selectbyunique");
    selectForParentTmpl = loadTemplate("selectforparent");
    joinedInterfaceTmpl = loadTemplate("joinedinterface");
    joinedClassTmpl = loadTemplate("joinedclass");
    childListTmpl = loadTemplate("childlist");
    baseRepoTmpl = loadTemplate("baserepo");
    dbContextTmpl = loadTemplate("dbcontext");
    childRepoTmpl = loadTemplate("childrepo");
    childRepoArgTmpl = loadTemplate("childrepoarg");
    childRepoAssignTmpl = loadTemplate("childrepoassign");
  }
}
 
