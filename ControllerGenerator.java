
import java.util.Map;
import java.util.HashMap;

public class ControllerGenerator {
  private Database db;

  public ControllerGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateControllers(String packageName) {
    Map<String, String> controllers = new HashMap<>();
    for(Table t: db.getTables()) {
      if(t.isJoiningTable()) {
        continue;
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
      .append(".model.")
      .append(t.getCleanName())
      .append(";\n")
      .append("import ")
      .append(packageName)
      .append(".service.")
      .append(t.getCleanName())
      .append("Service;\n\n")
      .append("@RequestMapping(value = \"/api/")
      .append(t.getLowerCasedName())
      .append("s/\")\n")
      .append("@Controller\npublic class ")
      .append(t.getCleanName())
      .append("Controller {\n")
      .append("\t@Autowired\n\tprivate ")
      .append(t.getCleanName())
      .append("Service service;\n\n")
      .append(generateSelectByPK(t))
      .append("\n")
      .append(generateSelectAll(t))
      .append("\n")
      .append(generateSelectByUnique(t))
      .append("\n")
      .append(generateInsert(t))
      .append("\n")
      .append(generateUpdate(t))
      .append("\n")
      .append(generateDelete(t))
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
    .append(" result = service.selectByPk(value);\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n");
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
    .append("> result = service.selectAll();\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column col: t.getUniqueCols()) {
      b
      .append("\t@RequestMapping(value = \"{")
      .append(col.getName())
      .append("}\", method = RequestMethod.GET)\n")
      .append("\tpublic ")
      .append("ResponseEntity<?>")
      .append(" selectBy")
      .append(col.getCleanName())
      .append("(@PathVariable ")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" ")
      .append(col.getName())
      .append(") {\n")
      .append("\t\t")
      .append(t.getCleanName())
      .append(" result = service.selectBy")
      .append(col.getCleanName())
      .append("(")
      .append(col.getName())
      .append(");\n")
      .append("\t\treturn ResponseEntity.ok(result);\n")
      .append("\t}\n");
      if(colIndex++ < t.getUniqueCols().size() - 1) {
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
    .append(" new")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = service.insert(new")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"\", method = RequestMethod.PUT)\n")
    .append("\tpublic ResponseEntity<?> update(@RequestBody ")
    .append(t.getCleanName())
    .append(" updated")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = service.update(updated")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@RequestMapping(value = \"{id}\", method = RequestMethod.DELETE)\n")
    .append("\tpublic ResponseEntity<?> delete(@PathVariable ")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" id) {\n")
    .append("\t\tboolean result = service.delete(id);\n")
    .append("\t\treturn ResponseEntity.ok(result);\n")
    .append("\t}\n");
    return b.toString();
  }
}