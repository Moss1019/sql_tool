
import java.util.Map;
import java.util.HashMap;

public class ViewGenerator extends Generator {
  private Database db;

  private String fieldTmpl;
  private String fieldListTmpl;
  private String assignmentTmpl;
  private String ctorTmpl;
  private String getterTmpl;
  private String classTmpl;
  private String classJoinedTmpl;

  public ViewGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> views = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      StringBuilder b = new StringBuilder();
      String cls = (currentLoopedOrJoined ? classJoinedTmpl : classTmpl)
        .replace("{packagename}", db.getPackageName())
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{fields}", generateFields(t))
        .replace("{childtablelists}", generateChildTableLists(t))
        .replace("{ctor}", generateCtor(t))
        .replace("{getters}", generateGetters(t));
      b.append(cls);
      views.put(t.getPascalName() + "View", b.toString());
    }
    return views;
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\tprivate ")
      .append(fieldTmpl
        .replace("{javattype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()))
      .append(";");
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateChildTableLists(Table t) {
    StringBuilder b = new StringBuilder();
    b.append("\n");
    int tableIndex = 0;
    for(Table ct: t.getNonJoinedTables()) {
      b
      .append("\tprivate ")
      .append(fieldListTmpl
        .replace("{tablenamepascal}", ct.getPascalName() + "View")
        .replace("{tablenamecamel}", ct.getCamelName()))
      .append(";");
      if(tableIndex++ < t.getNonJoinedTables().size() - 1) {
        b.append("\n\n");
      }
    }
    return b.toString();
  }

  private String generateCtor(Table t) {
    StringBuilder assignments = new StringBuilder();
    StringBuilder fieldParams = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      fieldParams.append(fieldTmpl
        .replace("{javattype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()));
      assignments
      .append("\t\t")
      .append(assignmentTmpl
        .replace("{columnnamecamel}", c.getCamelName()))
      .append(";");
      if(colIndex++ < t.getColumns().size() - 1) {
        fieldParams.append(", ");
        assignments.append("\n");
      } else if(colIndex == t.getColumns().size() && t.getNonJoinedTables().size() > 0) {
        fieldParams.append(", ");
        assignments.append("\n");
      } 
    }
    StringBuilder childTableListParams = new StringBuilder();
    int tableIndex = 0;
    for(Table ct: t.getNonJoinedTables()) {
        childTableListParams.append(fieldListTmpl
          .replace("{tablenamepascal}", ct.getPascalName() + "View")
          .replace("{tablenamecamel}", ct.getCamelName()));
        assignments
        .append("\t\t")
        .append(assignmentTmpl
          .replace("{columnnamecamel}", ct.getCamelName() + "s"))
        .append(";");
        if(tableIndex++ < t.getNonJoinedTables().size() - 1) {
          childTableListParams.append(", ");
          assignments.append("\n");
        }
    }
    StringBuilder b = new StringBuilder();
    b
    .append(ctorTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{fieldparams}", fieldParams.toString())
      .replace("{childtablelistsparams}", childTableListParams.toString())
      .replace("{assignments}", assignments.toString()));
    return b.toString();
  }

  private String generateGetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b.append(getterTmpl
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      } else if(colIndex == t.getColumns().size() && t.getNonJoinedTables().size() > 0) {
        b.append("\n");
      }
    }
    int tableIndex = 0;
    for(Table ct: t.getNonJoinedTables()) {
      b.append(getterTmpl
        .replace("{javatype}", "List<" + ct.getPascalName() + "View>")
        .replace("{columnnamepascal}", ct.getPascalName() + "s")
        .replace("{columnnamecamel}", ct.getCamelName() + "s"));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private void loadTemplates() {
    fieldTmpl = loadTemplate("../templates/view", "field");
    fieldListTmpl = loadTemplate("../templates/view", "fieldlist");
    assignmentTmpl = loadTemplate("../templates/view", "assignment");
    ctorTmpl = loadTemplate("../templates/view", "ctor");
    getterTmpl = loadTemplate("../templates", "getter");
    classTmpl = loadTemplate("../templates/view", "class");
    classJoinedTmpl = loadTemplate("../templates/view", "classjoined");
  }
}