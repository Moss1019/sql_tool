
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
      .append("import org.springframework.web.bind.annotation.RequestMethod;\n\n")
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
      .append("@Controller\npublic class ")
      .append(t.getCleanName())
      .append("Controller {\n")
      .append("\t@Autowired\n\tprivate ")
      .append(t.getCleanName())
      .append("Service service;\n\n")
      .append(generateSelectByPK(t))
      .append(generateSelectAll(t))
      .append(generateSelectByUnique(t))
      .append(generateInsert(t))
      .append(generateDelete(t))
      .append("}\n");
      controllers.put(String.format("%sController", t.getCleanName()), b.toString());
    }
    return controllers;
  }

  private String generateSelectByPK(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    
    return b.toString();
  }
}