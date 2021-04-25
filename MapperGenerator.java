
import java.util.Map;
import java.util.HashMap;

public class MapperGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String mapEntityTmpl;
  private String mapViewTmpl;
  private String colGetterTmpl;
  private String colSetterTmpl;

  public MapperGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> mappers = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      mappers.put(t.getPascalName() + "Mapper", classTmpl
      .replace("{childviewimports}", generateViewImports(t))
      .replace("{mapentity}", generateMapEntity(t))
      .replace("{mapentityempties}", t.getNonJoinedTables().size() > 0 ? generateEmptyMapEntity(t) : "")
      .replace("{mapview}", generateMapView(t))
      .replace("{tablenamecamel}", t.getCamelName())
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{packagename}", db.getPackageName()));
    }
    return mappers;
  }

  private String generateViewImports(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table ct: t.getNonJoinedTables()) {
      b
      .append("import ")
      .append(db.getPackageName())
      .append(".view.")
      .append(ct.getPascalName())
      .append("View;\n")
      .append("import ")
      .append(db.getPackageName())
      .append(".entity.")
      .append(ct.getPascalName())
      .append(";\n");
    }
    return b.toString();
  }

  private String generateMapEntity(Table t) {
    StringBuilder childListsParams = new StringBuilder();
    StringBuilder childListsArgs = new StringBuilder();
    for(Table ct: t.getNonJoinedTables()) {
      childListsParams.append(", List<").append(ct.getPascalName()).append("View> ").append(ct.getCamelName()).append("s");
      childListsArgs.append(", ").append(ct.getCamelName()).append("s");
    }
    StringBuilder colGetters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      colGetters.append(colGetterTmpl.replace("{columnnamepascal}", c.getPascalName()));
      if(colIndex++ < t.getColumns().size() - 1) {
      colGetters.append(", ");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append(mapEntityTmpl
      .replace("{childlistsparams}", childListsParams.toString())
      .replace("{childlistargs}", childListsArgs.toString())
      .replace("{colgetters}", colGetters.toString()))
    .append("\n");
    return b.toString();
  }

  private String generateEmptyMapEntity(Table t) {
    StringBuilder childListsArgs = new StringBuilder();
    for(Table ct: t.getNonJoinedTables()) {
      childListsArgs.append(", new ArrayList<>()");
    }
    StringBuilder colGetters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      colGetters.append(colGetterTmpl.replace("{columnnamepascal}", c.getPascalName()));
      if(colIndex++ < t.getColumns().size() - 1) {
      colGetters.append(", ");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append("\n")
    .append(mapEntityTmpl
      .replace("{childlistsparams}", "")
      .replace("{childlistargs}", childListsArgs.toString())
      .replace("{colgetters}", colGetters.toString()))
    .append("\n");
    return b.toString();
  }

  private String generateMapView(Table t) {
    StringBuilder colSetters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      colSetters.append("\t\t").append(colSetterTmpl.replace("{columnnamepascal}", c.getPascalName()));
      if(colIndex++ < t.getColumns().size() - 1) {
      colSetters.append("\n");
      }
    }
    return mapViewTmpl
      .replace("{colsetters}", colSetters.toString());
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/mapper", "class");
    mapEntityTmpl = loadTemplate("../templates/mapper", "mapentity");
    mapViewTmpl = loadTemplate("../templates/mapper", "mapview");
    colGetterTmpl = loadTemplate("../templates/mapper", "colgetter");
    colSetterTmpl = loadTemplate("../templates/mapper", "colsetter");
  }
}
