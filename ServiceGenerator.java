
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
  private String viewListMapTmpl;
  private String viewListUniqueTmpl;
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
        .replace("{subpackage}", "view")
        .replace("{classname}", st.getPascalName() + "View"));
    }
    return b.toString();
  }

  private String generateMethods(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(generateInsert(t))
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
      .append(generateSelectByPk(t))
      .append("\n")
      .append(generateSelectAll(t))
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

  private String generateInsert(Table t) {
    return insertTmpl
      .replace("{tablenamepascal}", t.getPascalName());
  }

  private String generateSelectByPk(Table t) {
    StringBuilder viewLists = new StringBuilder();
    StringBuilder childTables = new StringBuilder();
    for(Table ct: t.getChildTables()) {
      viewLists 
        .append("\n\t\t")
        .append(viewListTmpl
          .replace("{childtablenamepascal}", ct.getPascalName())
          .replace("{childtablenamecamel}", ct.getCamelName())
          .replace("{primarycolumnnamepascal}", t.getPrimaryColumn().getPascalName()));
      childTables.append(", ").append(ct.getCamelName()).append("Views");
    }
    return selectTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{viewlists}", viewLists)
      .replace("{childtables}", childTables.toString());
  }

  private String generateSelectAll(Table t) {
    StringBuilder viewLists = new StringBuilder();
    StringBuilder childTables = new StringBuilder();
    int tableIndex = 0;
    for(Table ct: t.getChildTables()) {
      viewLists
        .append("\n\t\t\t\t")
        .append(viewListMapTmpl
          .replace("{childtablenamecamel}", ct.getCamelName())
          .replace("{childtablenamepascal}", ct.getPascalName())
          .replace("{tablenamepascal}", t.getPascalName())
          .replace("{primarycolumnnamepascal}", t.getPrimaryColumn().getPascalName()));
      childTables.append(", ").append(ct.getCamelName()).append("Views");
    }
    return selectAllTmpl
      .replace("{viewlists}", viewLists.toString())
      .replace("{childtables}", childTables.toString());
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getUniqueColumns()) {
      StringBuilder viewLists = new StringBuilder();
      StringBuilder childTables = new StringBuilder();
      for(Table ct: t.getChildTables()) {
        viewLists
          .append("\n\t\t")
          .append(viewListUniqueTmpl
            .replace("{childtablenamepascal}", ct.getPascalName())
            .replace("{childtablenamecamel}", ct.getCamelName())
            .replace("{primarycolumnnamepascal}", t.getPrimaryColumn().getPascalName()));
        childTables.append(", ").append(ct.getCamelName()).append("Views");
      }
      b.append(selectByUniqueTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
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
    for(Table pt: t.getParentTables()) {
      StringBuilder viewLists = new StringBuilder();
      StringBuilder childTables = new StringBuilder();
      for(Table ct: t.getChildTables()) {
        viewLists
        .append("\n\t\t")
        .append(viewListUniqueTmpl
          .replace("{childtablenamepascal}", ct.getPascalName())
          .replace("{childtablenamecamel}", ct.getCamelName())
          .replace("{primarycolumnnamepascal}", t.getPrimaryColumn().getPascalName()));
        childTables.append(", ").append(ct.getCamelName()).append("Views");
      }
      b.append(selectOfTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{joinednamepascal}", pt.getPascalName())
        .replace("{primarycolumnnamecamel}", pt.getPrimaryColumn().getCamelName())
        .replace("{joinednamepascal}", pt.getPascalName())
        .replace("{viewlists}", viewLists.toString())
        .replace("{childtables}", childTables.toString()));
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
      viewListUniqueTmpl = loadTemplate("../templates/service", "viewlistunique");
      viewListMapTmpl = loadTemplate("../templates/service", "viewlistmap");
      importTmpl = loadTemplate("../templates/service", "import");
  }
}

