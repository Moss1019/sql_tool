
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class ServiceGenerator extends Generator {
  private Database db;

  private String depRepoTmpl;
  private String depServiceTmpl;
  private String classTmpl;
  private String deleteTmpl;
  private String deleteJoinedTmpl;
  private String insertTmpl;
  private String selectTmpl;
  private String selectAllTmpl;
  private String selectByUniqueTmpl;
  private String selectOfTmpl;
  private String selectParentChildren;
  private String updateTmpl;
  private String viewListTmpl;
  private String importTmpl;

  public ServiceGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> services = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      services.put(t.getPascalName() + "Service", classTmpl
        .replace("{imports}", generateImports(t))
        .replace("{packagename}", db.getPackageName())
        .replace("{methods}", generateMethods(t))
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{repodep}", depRepoTmpl.replace("{tablenamepascal}", t.getPascalName()))
        .replace("{servicedeps}", generateServiceDeps(t)));
    }
    return services;
  }

  private String generateServiceDeps(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      if(ct.getIsLooped() || ct.getIsJoined()) {
        continue;
      }
      b.append(depServiceTmpl
          .replace("{tablenamepascal}", ct.getPascalName())
          .replace("{tablenamecamel}", ct.getCamelName()));
    }
    return b.toString();
  }

  private String generateImports(Table t) {
    StringBuilder b = new StringBuilder();
    List<Table> tables = currentLoopedOrJoined ? t.getParentTables() : t.getChildTables();
    for(Table st: tables) {
      b
      .append(importTmpl
        .replace("{subpackage}", "entity")
        .replace("{classname}", st.getPascalName()))
      .append(importTmpl
        .replace("{subpackage}", "view")
        .replace("{classname}", st.getPascalName() + "View"))
      .append(importTmpl
        .replace("{subpackage}", "repository")
        .replace("{classname}", st.getPascalName() + "Repository"))
      .append(importTmpl
        .replace("{subpackage}", "mapper")
        .replace("{classname}", st.getPascalName() + "Mapper"));
    }
    return b.toString();
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(generateInsertSelect(t, insertTmpl, 2))
    .append("\n");
    if(currentLoopedOrJoined) {
      System.out.println(t.getPascalName());
      b
      .append(generateDeleteJoined(t))
      .append("\n")
      .append(generateSelectParentChildren(t));
    } else {
      b
      .append(generateDelete(t))
      .append("\n")
      .append(generateInsertSelect(t, selectTmpl, 2))
      .append("\n")
      .append(generateInsertSelect(t, selectAllTmpl, 4))
      .append("\n")
      .append(generateSelectByUnique(t))
      .append("\n")
      .append(generateSelectOf(t))
      .append("\n")
      .append(generateUpdate(t));
    }
    return b.toString();
  }

  private String generateDelete(Table t) {
    return deleteTmpl;
  }

  private String generateDeleteJoined(Table t) {
    return deleteJoinedTmpl
      .replace("{pk1namecamel}", t.getPrimaryColumn().getCamelName())
      .replace("{pk2namecamel}", t.getJoinedColumn().getCamelName());
  }

  private String generateInsertSelect(Table t, String template, int tabs) {
    StringBuilder viewLists = new StringBuilder();
    StringBuilder childTables = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      if(ct.getIsJoined()) {
        continue;
      }
      viewLists.append("\n");
      for(int i = 0; i < tabs; ++i) {
        viewLists.append("\t");
      }
      viewLists
      .append(viewListTmpl
        .replace("{tablenamepascal}", ct.getPascalName())
        .replace("{tablenamecamel}", ct.getCamelName())
        .replace("{parenttablenamepascal}", t.getPascalName()));
      childTables.append(", ").append(ct.getCamelName()).append("s");
    }
    return template
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{viewlists}", viewLists.toString())
      .replace("{childtables}", childTables.toString());
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column uc: t.getUniqueColumns()) {
      StringBuilder viewLists = new StringBuilder();
      StringBuilder childTables = new StringBuilder();
      for(Table ct: t.getChildTables()) {
        if(ct.getIsJoined() || ct.getIsLooped()) {
          continue;
        }
        viewLists
        .append("\n\t\t")
        .append(viewListTmpl
          .replace("{tablenamepascal}", ct.getPascalName())
          .replace("{tablenamecamel}", ct.getCamelName())
          .replace("{parenttablenamepascal}", t.getPascalName()));
        childTables.append(", ").append(ct.getCamelName()).append("s");
      }
      b.append(selectByUniqueTmpl
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(uc.getDataType()))
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{columnnamepascal}", uc.getPascalName())
        .replace("{viewlists}", viewLists.toString())
        .replace("{childtables}", childTables.toString()));
      if(colIndex++ < t.getUniqueColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateSelectOf(Table t) {
    StringBuilder b = new StringBuilder();
    int tableIndex = 0;
    for(Table pt: t.getParentTables()) {
      b.append(generateInsertSelect(t, selectOfTmpl, 4)
        .replace("{joinednamepascal}", pt.getPascalName())
        .replace("{joinednamecamel}", pt.getCamelName()));
      if(tableIndex++ < t.getParentTables().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateSelectParentChildren(Table t) {
    int index = t.getIsLooped() ? 0 : 1;
    return selectParentChildren
      .replace("{resulttablenamepascal}", t.getParentTables().get(index).getPascalName())
      .replace("{tablenamespascal}", t.getPascalName())
      .replace("{pk1namecamel}", t.getPrimaryColumn().getCamelName());
  }

  private String generateUpdate(Table t) {
    return updateTmpl
      .replace("{tablenamepascal}", t.getPascalName());
  }

  private void loadTemplates() {
      depRepoTmpl = loadTemplate("../templates/service", "deprepo");
      depServiceTmpl = loadTemplate("../templates/service", "depservice");
      classTmpl = loadTemplate("../templates/service", "class");
      deleteTmpl = loadTemplate("../templates/service", "delete");
      deleteJoinedTmpl = loadTemplate("../templates/service", "deletejoined");
      insertTmpl = loadTemplate("../templates/service", "insert");
      selectTmpl = loadTemplate("../templates/service", "select");
      selectAllTmpl = loadTemplate("../templates/service", "selectall");
      selectByUniqueTmpl = loadTemplate("../templates/service", "selectbyunique");
      selectOfTmpl = loadTemplate("../templates/service", "selectof");
      selectParentChildren = loadTemplate("../templates/service", "selectparentchildren");
      updateTmpl = loadTemplate("../templates/service", "update");
      viewListTmpl = loadTemplate("../templates/service", "viewlist");
      importTmpl = loadTemplate("../templates/service", "import");
  }
}

