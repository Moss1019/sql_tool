
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ViewGenerator {
  private Database db;

  public ViewGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateViews(String packageName) {
    Map<String, String> views = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".view;\n\n");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append("import ")
        .append(packageName)
        .append(".view.")
        .append(ct.getCleanName())
        .append("View;\n");
      }
      b
      .append("\nimport java.util.List;\n\n")
      .append("public class ")
      .append(t.getCleanName())
      .append("View {\n");
      b
      .append(generateFields(t))
      .append(generateConstructor(t))
      .append(generateGettersSetters(t));
      b.append("}\n");
      views.put(t.getCleanName() + "View", b.toString());
    }
    return views;
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getColumns()) {
      b
      .append("\tprivate ")
      .append(ColumnEnums.resolvePrimitiveType(c.getDataType()))
      .append(" ")
      .append(c.getPascalName())
      .append(";\n\n");
    }
    System.out.println("Child tables");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append('\t')
      .append("private List<")
      .append(ct.getCleanName())
      .append("View")
      .append("> ")
      .append(ct.getName())
      .append("s;\n\n");
    }
    return b.toString();
  }

  public String generateConstructor(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(t.getPascalName())
    .append("View(");
    int colCount = 0;
    for(Column c: t.getColumns()) {
      b
      .append(ColumnEnums.resolvePrimitiveType(c.getDataType()))
      .append(" ")
      .append(c.getPascalName());
      if(colCount++ < t.getColumns().size() - 1) {
        b.append(", ");
      }
    }
    if(colCount > 0 && t.getChildTables().size() > 0) {
      b.append(", ");
    }
    colCount = t.getNumJoiningTables();
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append("List<")
      .append(ct.getCleanName())
      .append("View")
      .append("> ")
      .append(ct.getName())
      .append("s");
      if(colCount++ < t.getChildTables().size() - 1) {
        b.append(", ");
      }
    }
    b.append(") {\n");
    for(Column c: t.getColumns()) {
      b
      .append("\t\tthis.")
      .append(c.getPascalName())
      .append(" = ")
      .append(c.getPascalName())
      .append(";\n");
    }
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append("\t\tthis.")
      .append(ct.getName())
      .append("s = ")
      .append(ct.getName())
      .append("s;\n");
    }
    b.append("\t}\n\n");
    return b.toString();
  }

  private String generateGettersSetters(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column col: t.getColumns()) {
      b
      .append(generateGetter(col))
      .append("\n")
      .append(generateSetter(col))
      .append("\n");
    }
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(generateGetter(ct))
      .append("\n")
      .append(generateSetter(ct))
      .append("\n");
    }
    return b.toString();
  }

  public String generateGetter(Column col) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
    .append(" get")
    .append(col.getCleanName())
    .append("() {\n")
    .append("\t\treturn ")
    .append(col.getPascalName())
    .append(";\n\t}\n");
    return b.toString();
  }

  public String generateSetter(Column col) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic void set")
    .append(col.getCleanName())
    .append("(")
    .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
    .append(" ")
    .append(col.getPascalName())
    .append(") {\n")
    .append("\t\tthis.")
    .append(col.getPascalName())
    .append(" = ")
    .append(col.getPascalName())
    .append(";\n\t}\n");
    return b.toString();
  }

  private String generateGetter(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic List<")
    .append(t.getCleanName())
    .append("View> get")
    .append(t.getCleanName())
    .append("s() {\n")
    .append("\t\treturn ")
    .append(t.getName())
    .append("s;\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateSetter(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic void set")
    .append(t.getCleanName())
    .append("s(List<")
    .append(t.getCleanName())
    .append("View> ")
    .append(t.getName())
    .append("s) {\n")
    .append("\t\tthis.")
    .append(t.getName())
    .append("s = ")
    .append(t.getName())
    .append("s;\n\t}\n");
    return b.toString();
  }
}