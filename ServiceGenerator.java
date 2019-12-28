
import java.util.Map;
import java.util.HashMap;

public class ServiceGenerator {
  private Database db;

  public ServiceGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateServices(String packageName) {
    Map<String, String> services = new HashMap<>();
    for(Table t: db.getTables()) {
      if(t.isJoiningTable()) {
        continue;
      }
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".service;\n\n")
      .append("import org.springframework.stereotype.Service;\n\n")
      .append("import org.springframework.beans.factory.annotation.Autowired;\n\n")
      .append("import java.util.List;\n\n")
      .append("import ")
      .append(packageName)
      .append(".model.")
      .append(t.getCleanName())
      .append(";\n")
      .append("import ")
      .append(packageName)
      .append(".repository.")
      .append(t.getCleanName())
      .append("Repository;\n\n")
      .append("@Service\npublic class ")
      .append(t.getCleanName())
      .append("Service {\n")
      .append("\t@Autowired\n")
      .append("\tprivate ")
      .append(t.getCleanName())
      .append("Repository repo;\n\n")
      .append(generateSelectByPK(t))
      .append(generateSelectAll(t))
      .append(generateSelectByUnique(t))
      .append(generateInsert(t))
      .append(generateDelete(t))
      .append("}\n");
      services.put(String.format("%sService", t.getCleanName()), b.toString());
    }
    return services;
  }

  private String generateSelectByPK(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(t.getCleanName())
    .append(" selectByPk(")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" value) {\n")
    .append(generateReturn(t, "selectByPk", true))
    .append("\t}\n");
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

  private String generateReturn(Table t, String methodName, boolean isSingleResult) {
    return String.format("\t\t%s result = repo.%s();\n\n\t\treturn result;\n",
      isSingleResult ? t.getCleanName() : String.format("List<%s>", t.getCleanName()),
      methodName);
  }
}