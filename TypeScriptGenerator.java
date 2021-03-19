
import java.util.Map;
import java.util.HashMap;

public class TypeScriptGenerator extends Generator {
  private Database db;

  private String interfaceTmpl;
  private String propertyTmpl;
  private String importTmpl;
  private String exportTmpl;

  public TypeScriptGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> types = new HashMap<String, String>();
    StringBuilder b = new StringBuilder();
    for(Table t: db.getTables()) {
      types.put(t.getPascalName(), generateType(t));
      b
      .append(exportTmpl
        .replace("{tablenamepascal}", t.getPascalName()))
      .append("\n");
    }
    types.put("index", b.toString());
    return types;
  }

  private String generateType(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\t")
      .append(propertyTmpl
        .replace("{colnamecamel}", c.getCamelName())
        .replace("{typescripttype}", DataTypeUtil.resolveTypeScriptType(c.getDataType())));
      if(colIndex++ < t.getColumns().size() - 1) {
        b
        .append("\n");
      }
    }
    int tabIndex = 0;
    for(Table ch: t.getNonJoinedTables()) {
      b
      .append("\n\t")
      .append(propertyTmpl
        .replace("{colnamecamel}", ch.getCamelName() + "s")
        .replace("{typescripttype}", ch.getPascalName() + "[]"));
    }
    return interfaceTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{properties}", b.toString())
      .replace("{imports}", generateImports(t));
  }

  private String generateImports(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ch: t.getNonJoinedTables()) {
      b
      .append(importTmpl.replace("{tablenamepascal}", ch.getPascalName()));
    }
    if(t.getNonJoinedTables().size() > 0) {
      b.append("\n\n");
    }
    return b.toString();
  }

  private void loadTemplates() {
    interfaceTmpl = loadTemplate("../templates/typescript", "interface");
    propertyTmpl = loadTemplate("../templates/typescript", "property");
    importTmpl = loadTemplate("../templates/typescript", "import");
    exportTmpl = loadTemplate("../templates/typescript", "export");
  }
}
