
import java.util.Map;
import java.util.HashMap;

public class MapperGenerator {
  private Database db;

  public MapperGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateMappers(String packageName) {
    Map<String, String> mappers = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".mapper;\n\n");
      b
      .append("import ")
      .append(packageName)
      .append(".model.")
      .append(t.getCleanName())
      .append(";\n")
      .append("import ")
      .append(packageName)
      .append(".view.")
      .append(t.getCleanName())
      .append("View;\n\n");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append("import ")
        .append(packageName)
        .append(".view.")
        .append(ct.getCleanName())
        .append("View;\n\n");
      }
      b
      .append("import java.util.List;\n\n")
      .append("public class ")
      .append(t.getCleanName())
      .append("Mapper {\n")
      .append(generateEntityMapping(t))
      .append(generateViewMapping(t))
      .append("}\n");
      mappers.put(String.format("%sMapper", t.getCleanName()), b.toString());
    }
    return mappers;
  }

  private String generateEntityMapping(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic static ")
    .append(t.getCleanName())
    .append("View map")
    .append(t.getCleanName())
    .append("(")
    .append(t.getCleanName())
    .append(" ")
    .append(t.getName());
    int tableCount = 0;
    for(Table ct: t.getNonJoiningTables()) {
      if(tableCount++ < t.getNonJoiningTables().size()) {
        b
        .append(", ");
      }
      b
      .append("List<")
      .append(ct.getCleanName())
      .append("View> ")
      .append(ct.getName())
      .append("s");
    }
    b
    .append(") {\n")
    .append("\t\treturn new ")
    .append(t.getCleanName())
    .append("View(");
    int colCount = 0;
    for(Column c: t.getColumns()) {
      b
      .append(t.getName())
      .append(".get")
      .append(c.getCleanName())
      .append("()");
      if(colCount++ < t.getColumns().size() - 1) {
        b.append(", ");
      }
    }
    if(colCount > 0 && t.getNonJoiningTables().size() > 0) {
      b.append(", ");
    }
    colCount = t.getNumJoiningTables();
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(ct.getName())
      .append("s");
      if(colCount++ < t.getNonJoiningTables().size() - 1) {
        b.append(", ");
      }
    }
    b
    .append(");\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateViewMapping(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic static ")
    .append(t.getCleanName())
    .append(" map")
    .append(t.getCleanName())
    .append("View(")
    .append(t.getCleanName())
    .append("View ")
    .append(t.getName())
    .append("View) {\n")
    .append("\t\t")
    .append(t.getCleanName())
    .append(" ")
    .append(t.getName())
    .append(" = new ")
    .append(t.getCleanName())
    .append("();\n");
    for(Column c: t.getColumns()) {
      b
      .append("\t\t")
      .append(t.getName())
      .append(".set")
      .append(c.getCleanName())
      .append("(")
      .append(t.getName())
      .append("View")
      .append(".get")
      .append(c.getCleanName())
      .append("());\n");
    }
    b
    .append("\t\treturn ")
    .append(t.getName())
    .append(";\n")
    .append("\t}\n");
    return b.toString();
  }
}
