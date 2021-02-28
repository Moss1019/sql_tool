
import java.util.Map;
import java.util.HashMap;

public class ControllerGenerator {
  private Database db;

  private boolean isCurrentJoining;
  private Table joiningPrimary;
  private Table joiningSecondary;


  public ControllerGenerator(Database db) {
    this.db = db;
    isCurrentJoining = false;
    joiningPrimary = null;
    joiningSecondary = null;
  }

  public Map<String, String> generateControllers(String packageName) {
    Map<String, String> controllers = new HashMap<>();
    for(Table t: db.getTables()) {
      isCurrentJoining = t.isJoiningTable();
      if(isCurrentJoining) {
        joiningPrimary = Table.getJoiningPrimary(t);
        joiningSecondary = Table.getJoiningSecondary(t);
      }
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".controller;\n\n")
      .append("import org.springframework.http.ResponseEntity;\n\n")
      .append("import org.springframework.stereotype.Controller;\n\n")
      .append("import org.springframework.beans.factory.annotation.Autowired;\n\n")
      .append("import org.springframework.web.bind.annotation.RequestMapping;\n")
      .append("import org.springframework.web.bind.annotation.RequestMethod;\n")
      .append("import org.springframework.web.bind.annotation.PathVariable;\n")
      .append("import org.springframework.web.bind.annotation.RequestBody;\n\n")
      .append("import java.util.List;\n\n")
      .append("import ")
      .append(packageName)
      .append(".view.");
      if(t.isJoiningTable()) {
        b
        .append(t.getParentTables().get(0).getCleanName())
        .append("View;\n")
        .append("import ")
        .append(packageName)
        .append(".view.")
        .append(t.getCleanName())
        .append("View;\n");
      } else {
        b
        .append(t.getCleanName())
        .append("View;\n");
      }
      b
      .append("import ")
      .append(packageName)
      .append(".service.")
      .append(t.getCleanName())
      .append("Service;\n\n")
      .append("@RequestMapping(value = \"/api/")
      .append(t.getLowerCasedName())
      .append("s\")\n")
      .append("@Controller\npublic class ")
      .append(t.getCleanName())
      .append("Controller {\n")
      .append("\t@Autowired\n\tprivate ")
      .append(t.getCleanName())
      .append("Service service;\n\n");
      if(!t.isJoiningTable()) {
        b
        .append(generateSelectAll(t))
        .append(generateSelectByPK(t))
        .append(generateSelectByUnique(t))
        .append(generateUpdate(t));
      }
      b
      .append(generateDelete(t))
      .append(generateInsert(t))
      .append(generateSelectParentChildren(t))
      .append("}\n");
      controllers.put(String.format("%sController", t.getCleanName()), b.toString());
    }
    return controllers;
  }

  private String generateSelectByPK(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"{value}\", method = RequestMethod.GET)\n")
    .append("\tpublic ")
    .append("ResponseEntity<?>")
    .append(" getByPk(@PathVariable ")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" value) {\n")
    .append("\t\t")
    .append(t.getCleanName())
    .append("View result = service.selectByPk(value);\n")
    .append("\t\tif (result == null) {\n")
    .append("\t\t\treturn ResponseEntity.status(404).body(\"Could not find ")
    .append(t.getCleanName())
    .append("\");\n")
    .append("\t\t}\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"\", method = RequestMethod.GET)\n")
    .append("\tpublic ")
    .append("ResponseEntity<?>")
    .append(" selectAll() {\n")
    .append("\t\tList<")
    .append(t.getCleanName())
    .append("View> result = service.selectAll();\n")
    .append("\t\tif (result.size() == 0) {\n")
    .append("\t\t\treturn ResponseEntity.status(404).body(\"No results\");\n")
    .append("\t\t}\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    if(isCurrentJoining) {
      b
      .append("\t@RequestMapping(value = \"{")
      .append(joiningPrimary.getPrimaryColumn().getPascalName())
      .append("}\"")
      .append(", method = RequestMethod.GET)\n\t")
      .append("public ResponseEntity<?> get")
      .append(joiningPrimary.getCleanName())
      .append(t.getCleanName())
      .append("s(@PathVariable ")
      .append(ColumnEnums.resolvePrimitiveType(joiningPrimary.getPrimaryColumn().getDataType()))
      .append(" ")
      .append(joiningPrimary.getPrimaryColumn().getPascalName())
      .append(") { \n")
      .append("\t\tList<")
      .append(joiningSecondary.getCleanName())
      .append("View> result = service.select")
      .append(joiningPrimary.getCleanName())
      .append(t.getCleanName())
      .append("s(")
      .append(joiningPrimary.getPrimaryColumn().getPascalName())
      .append(");\n")
      .append("\t\tif (result.size() == 0) {\n")
      .append("\t\t\treturn ResponseEntity.status(404).body(\"No results\");\n\t\t}\n")
      .append("\t\treturn ResponseEntity.ok(result);\n")
      .append("\t}\n\n");
    } else {
      for (Table parentTable: t.getParentTables()) {
        b
        .append("\t@RequestMapping(value = \"for")
        .append(parentTable.getPascalName())
        .append("/{")
        .append(parentTable.getPrimaryColumn().getPascalName())
        .append("}\", method = RequestMethod.GET)\n")
        .append("\tpublic ResponseEntity<?> getOf")
        .append(parentTable.getCleanName())
        .append("(@PathVariable ")
        .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
        .append(" ")
        .append(parentTable.getPrimaryColumn().getPascalName())
        .append(") { \n")
        .append("\t\tList<")
        .append(t.getCleanName())
        .append("View> result = service.selectOf")
        .append(parentTable.getCleanName())
        .append("(")
        .append(parentTable.getPrimaryColumn().getPascalName())
        .append(");\n")
        .append("\t\tif (result.size() == 0) {\n")
        .append("\t\t\treturn ResponseEntity.status(404).body(\"No results\");\n\t\t}\n")
        .append("\t\treturn ResponseEntity.ok(result);\n")
        .append("\t}\n\n");
      }
    }
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column col: t.getUniqueCols()) {
      b
      .append("\t@RequestMapping(value = \"by")
      .append(col.getCleanName())
      .append("/{")
      .append(col.getPascalName())
      .append("}\", method = RequestMethod.GET)\n")
      .append("\tpublic ")
      .append("ResponseEntity<?>")
      .append(" selectBy")
      .append(col.getCleanName())
      .append("(@PathVariable ")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" ")
      .append(col.getPascalName())
      .append(") {\n")
      .append("\t\t")
      .append(t.getCleanName())
      .append("View result = service.selectBy")
      .append(col.getCleanName())
      .append("(")
      .append(col.getPascalName())
      .append(");\n")
      .append("\t\tif (result == null) {\n")
      .append("\t\t\treturn ResponseEntity.status(404).body(\"Could not find")
      .append(t.getCleanName())
      .append("\");\n")
      .append("\t\t}\n")
      .append("\t\treturn ResponseEntity.ok(result);\n")
      .append("\t}\n\n");
      if(++colIndex < t.getColumns().size()) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"\", method = RequestMethod.POST)\n")
    .append("\tpublic ResponseEntity<?> insert(@RequestBody ")
    .append(t.getCleanName())
    .append("View new")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\t")
    .append(t.getCleanName())
    .append("View result = service.insert(new")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\tif (result == null) {\n")
    .append("\t\t\treturn ResponseEntity.status(400).body(\"Could not create new ")
    .append(t.getCleanName())
    .append("\");\n")
    .append("\t\t}\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"\", method = RequestMethod.PUT)\n")
    .append("\tpublic ResponseEntity<?> update(@RequestBody ")
    .append(t.getCleanName())
    .append("View updated")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = service.update(updated")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\tif (!result) {\n")
    .append("\t\t\treturn ResponseEntity.status(400).body(\"Could not update ")
    .append(t.getCleanName())
    .append("\");\n")
    .append("\t\t}\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    if(t.isJoiningTable()) {
      b
      .append("\t@RequestMapping(value = \"");
      int colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append("{")
        .append(c.getPascalName())
        .append("}");
        if(++colIndex < t.getColumns().size()) {
          b
          .append("/");
        }
      }
      b
      .append("\", method = RequestMethod.DELETE)\n")
      .append("\tpublic ResponseEntity<?> delete(");
      colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append("@PathVariable ")
        .append(ColumnEnums.resolvePrimitiveType(c.getDataType()))
        .append(" ")
        .append(c.getPascalName());
        if(++colIndex < t.getColumns().size()) {
          b.append(", ");
        }
      }
      b
      .append(") {\n")
      .append("\t\tboolean result = service.delete(");
      colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append(c.getPascalName());
        if(++colIndex < t.getColumns().size()) {
          b.append(", ");
        }
      }
      b.append(");\n");
    } else {
      b
      .append("\t@RequestMapping(value = \"{id}\", method = RequestMethod.DELETE)\n")
      .append("\tpublic ResponseEntity<?> delete(@PathVariable ")
      .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .append(" id) {\n")
      .append("\t\tboolean result = service.delete(id);\n");
    }
    b
    .append("\t\tif (!result) {\n")
    .append("\t\t\treturn ResponseEntity.status(400).body(\"Could not delete ")
    .append(t.getCleanName())
    .append("\");\n")
    .append("\t\t}\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n\n");
    return b.toString();
  }
}