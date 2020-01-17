
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
      .append("\n")
      .append(generateSelectParentChildren(t))
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
    .append("\t\t")
    .append(t.getCleanName())
    .append(" result = repo.selectByPk(value);\n")
    .append("\t\treturn result;\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    for (Table parentTable: t.getParentTables()) {
      b
      .append("\tpublic ")
      .append("List<")
      .append(t.getCleanName())
      .append("> selectOf")
      .append(parentTable.getCleanName())
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
      .append(" ")
      .append(parentTable.getPrimaryColumn().getPascalName())
      .append(") {\n")
      .append("\t\tList<")
      .append(t.getCleanName())
      .append("> result = repo.selectOf")
      .append(parentTable.getCleanName())
      .append("(")
      .append(parentTable.getPrimaryColumn().getPascalName())
      .append(");\n")
      .append("\t\treturn result;\n")
      .append("\t}\n\n");
    }
   
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic List<")
    .append(t.getCleanName())
    .append("> selectAll() {\n")
    .append("\t\tList<")
    .append(t.getCleanName())
    .append("> result = repo.selectAll();\n")
    .append("\t\treturn result;\n")
    .append("\t}");
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column col: t.getUniqueCols()) {
      b
      .append("\tpublic ")
      .append(t.getCleanName())
      .append(" selectBy")
      .append(col.getCleanName())
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" value) {\n")
      .append("\t\t")
      .append(t.getCleanName())
      .append(" result = repo.selectBy")
      .append(col.getCleanName())
      .append("(value);\n")
      .append("\t\treturn result;\n")
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
    .append("\tpublic boolean insert(")
    .append(t.getCleanName())
    .append(" new")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = repo.insert(new")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\treturn result;\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean update(")
    .append(t.getCleanName())
    .append(" updated")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = repo.update(updated")
    .append(t.getCleanName())
    .append(");\n")
    .append("\t\treturn result;\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean delete(")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" id) {\n")
    .append("\t\tboolean result = repo.delete(id);\n")
    .append("\t\treturn result;\n")
    .append("\t}\n");
    return b.toString();
  }


}