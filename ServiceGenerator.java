
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ServiceGenerator {
  private Database db;

  private boolean isCurrentJoining;
  private Table joiningPrimary;
  private Table joiningSecondary;

  public ServiceGenerator(Database db) {
    this.db = db;
    isCurrentJoining = false;
    joiningPrimary = null;
    joiningSecondary = null;
  }

  public Map<String, String> generateServices(String packageName) {
    Map<String, String> services = new HashMap<>();
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
      .append(".service;\n\n")
      .append("import org.springframework.stereotype.Service;\n\n")
      .append("import org.springframework.beans.factory.annotation.Autowired;\n\n")
      .append("import ")
      .append(packageName)
      .append(".model.");
      if(isCurrentJoining) {
        b
        .append(t.getParentTables().get(0).getCleanName())
        .append(";\n")
        .append("import ")
        .append(packageName)
        .append(".model.")
        .append(t.getCleanName())
        .append(";\nimport ")
        .append(packageName)
        .append(".mapper.")
        .append(joiningSecondary.getCleanName())
        .append("Mapper;\n");
      } else {
        b
        .append(t.getCleanName())
        .append(";\n");
      }
      b
      .append("import ")
      .append(packageName)
      .append(".view.");
      if(isCurrentJoining) {
        b
        .append(t.getParentTables().get(0).getCleanName())
        .append("View;\n")
        .append("import ")
        .append(packageName)
        .append(".view.")
        .append(t.getCleanName())
        .append("View");
      } else {
        b
        .append(t.getCleanName())
        .append("View");
      }
      b
      .append(";\n")
      .append("import ")
      .append(packageName)
      .append(".repository.")
      .append(t.getCleanName())
      .append("Repository;\n")
      .append("import ")
      .append(packageName)
      .append(".mapper.")
      .append(t.getCleanName())
      .append("Mapper;\n");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append("import ")
        .append(packageName)
        .append(".view.")
        .append(ct.getCleanName())
        .append("View;\n");
      }
      b
      .append("\nimport java.util.List;\n")
      .append("import java.util.stream.Collectors;\n")
      .append("\n@Service\npublic class ")
      .append(t.getCleanName())
      .append("Service {\n")
      .append("\t@Autowired\n")
      .append("\tprivate ")
      .append(t.getCleanName())
      .append("Repository repo;\n");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append("\t@Autowired\n")
        .append("\tprivate ")
        .append(ct.getCleanName())
        .append("Service ")
        .append(ct.getName())
        .append("Service;\n");
      }
      if(isCurrentJoining) {
        b
        .append("\t@Autowired\n")
        .append("\tprivate ")
        .append(joiningSecondary.getCleanName())
        .append("Service ")
        .append(joiningSecondary.getName())
        .append("Service;\n");
      }
      b
      .append('\n')
      .append(generateInsert(t))
      .append(generateSelectParentChildren(t))
      .append(generateDelete(t));
      if(!isCurrentJoining) {
        b
        .append(generateSelectByPK(t))
        .append(generateSelectAll(t))
        .append(generateSelectByUnique(t))
        .append(generateUpdate(t));
      }
      b
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
    .append("View selectByPk(")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" value) {\n")
    .append("\t\t")
    .append(t.getCleanName())
    .append(" dbResult = repo.selectByPk(value);\n");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append("\t\tList<")
      .append(ct.getCleanName())
      .append("View> ") 
      .append(ct.getName())
      .append("s = ")
      .append(ct.getName())
      .append("Service.selectOf")
      .append(t.getCleanName())
      .append("(dbResult.get")
      .append(t.getPrimaryColumn().getCleanName())
      .append("());\n");
    }
    b
    .append("\t\t")
    .append(t.getCleanName())
    .append("View result = ")
    .append(t.getCleanName())
    .append("Mapper.map")
    .append(t.getCleanName())
    .append("(dbResult");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(", ")
      .append(ct.getName())
      .append("s");
    }
    b
    .append(");\n")
    .append("\t\treturn result;\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateStreamResult(Table t, Table parentTable) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t\t\t\tList<")
    .append(t.getCleanName())
    .append("View> ")
    .append(t.getName())
    .append("Views = ")
    .append(t.getName())
    .append("Service.selectOf")
    .append(parentTable.getCleanName())
    .append("(x.get")
    .append(parentTable.getPrimaryColumn().getCleanName())
    .append("());\n");
    return b.toString();
  }

  private String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    if(isCurrentJoining) {
      b
      .append("\tpublic List<")
      .append(joiningSecondary.getCleanName())
      .append("View> select")
      .append(joiningPrimary.getCleanName())
      .append(t.getCleanName())
      .append("s")
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(joiningPrimary.getPrimaryColumn().getDataType()))
      .append(" ")
      .append(joiningPrimary.getPrimaryColumn().getPascalName())
      .append(") {\n")
      .append("\t\tList<")
      .append(joiningSecondary.getCleanName())
      .append("> dbResult = repo.select")
      .append(joiningPrimary.getCleanName())
      .append(t.getCleanName())
      .append("s")
      .append("(")
      .append(joiningPrimary.getPrimaryColumn().getPascalName())
      .append(");\n")
      .append("\t\t")
      .append("List<")
      .append(joiningSecondary.getCleanName())
      .append("View> result = dbResult.stream()\n\t\t\t")
      .append(".map(x -> {\n")
      .append("\t\t\t\treturn ")
      .append(joiningSecondary.getCleanName())
      .append("Mapper.map")
      .append(joiningSecondary.getCleanName())
      .append("(x");
      for(Table ct: joiningSecondary.getNonJoiningTables()) {
        b
        .append(", null");
      }
      b
      .append(");\n")
      .append("\t\t\t})\n\t\t\t")
      .append(".collect(Collectors.toList());\n")
      .append("\t\treturn result;\n")
      .append("\t}\n\n");
    } else {
      for(Table pt: t.getParentTables()) {
        b
        .append("\tpublic ")
        .append("List<")
        .append(t.getCleanName())
        .append("View> selectOf")
        .append(pt.getCleanName())
        .append("(")
        .append(ColumnEnums.resolvePrimitiveType(pt.getPrimaryColumn().getDataType()))
        .append(" ")
        .append(pt.getPrimaryColumn().getPascalName())
        .append(") {\n")
        .append("\t\tList<")
        .append(t.getCleanName())
        .append("> dbResult = repo.selectOf")
        .append(pt.getCleanName())
        .append("(")
        .append(pt.getPrimaryColumn().getPascalName())
        .append(");\n")
        .append("\t\t")
        .append("List<")
        .append(t.getCleanName())
        .append("View> result = dbResult.stream()\n\t\t\t")
        .append(".map(x -> {\n");
        for(Table ct: t.getNonJoiningTables()) {
          b
          .append(generateStreamResult(ct, t));
        }
        b
        .append("\t\t\t\treturn ")
        .append(t.getCleanName())
        .append("Mapper.map")
        .append(t.getCleanName())
        .append("(x");
        for(Table ct: t.getNonJoiningTables()) {
        b
        .append(", ")
        .append(ct.getName())
        .append("Views");
        }
        b
        .append(");\n")
        .append("\t\t\t})\n\t\t\t")
        .append(".collect(Collectors.toList());\n")
        .append("\t\treturn result;\n")
        .append("\t}\n\n");
      }
    }
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic List<")
    .append(t.getCleanName())
    .append("View> selectAll() {\n")
    .append("\t\tList<")
    .append(t.getCleanName())
    .append("> dbResult = repo.selectAll();\n")
    .append("\t\tList<")
    .append(t.getCleanName())
    .append("View> result = ")
    .append("dbResult\n\t\t\t.stream()\n\t\t\t.map(x -> {\n");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(generateStreamResult(ct, t));
    }
    b
    .append("\t\t\t\treturn ")
    .append(t.getCleanName())
    .append("Mapper.map")
    .append(t.getCleanName())
    .append("(x");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(", ")
      .append(ct.getName())
      .append("Views");
    }
    b
    .append(");")
    .append("\n\t\t\t})")
    .append("\n\t\t\t.collect(Collectors.toList());\n")
    .append("\t\treturn result;\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column col: t.getUniqueCols()) {
      b
      .append("\tpublic ")
      .append(t.getCleanName())
      .append("View selectBy")
      .append(col.getCleanName())
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" value) {\n")
      .append("\t\t")
      .append(t.getCleanName())
      .append(" dbResult = repo.selectBy")
      .append(col.getCleanName())
      .append("(value);\n");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append("\t\tList<")
        .append(ct.getCleanName())
        .append("View> ") 
        .append(ct.getName())
        .append("s = ")
        .append(ct.getName())
        .append("Service.selectOf")
        .append(t.getCleanName())
        .append("(dbResult.get")
        .append(t.getPrimaryColumn().getCleanName())
        .append("());\n");
      }
      b
      .append("\t\t")
      .append(t.getCleanName())
      .append("View result = ")
      .append(t.getCleanName())
      .append("Mapper.map")
      .append(t.getCleanName())
      .append("(dbResult");
      for(Table ct: t.getNonJoiningTables()) {
        b
        .append(", ")
        .append(ct.getName())
        .append("s");
      }
      b
      .append(");\n")
      .append("\t\treturn result;\n")
      .append("\t}\n");
      if(++colIndex < t.getColumns().size()) {
        b.append("\n");
      }
    }
    b.append("\n");
    return b.toString();
  }
  
  private String generateInsert(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(t.getCleanName())
    .append("View insert(")
    .append(t.getCleanName())
    .append("View new")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\t")
    .append(t.getCleanName())
    .append(" dbResult = repo.insert(")
    .append(t.getCleanName())
    .append("Mapper.map")
    .append(t.getCleanName())
    .append("View(")
    .append("new")
    .append(t.getCleanName())
    .append("));\n");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append("\t\tList<")
      .append(ct.getCleanName())
      .append("View> ")
      .append(ct.getName())
      .append("Views = ")
      .append(ct.getName())
      .append("Service.selectOf")
      .append(t.getCleanName())
      .append("(dbResult.get")
      .append(t.getPrimaryColumn().getCleanName())
      .append("());\n");
    }
    b
    .append("\t\t")
    .append(t.getCleanName())
    .append("View result = ")
    .append(t.getCleanName())
    .append("Mapper.map")
    .append(t.getCleanName())
    .append("(dbResult");
    for(Table ct: t.getNonJoiningTables()) {
      b
      .append(", ")
      .append(ct.getName())
      .append("Views");
    }
    b
    .append(");\n")
    .append("\t\treturn result;\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean update(")
    .append(t.getCleanName())
    .append("View updated")
    .append(t.getCleanName())
    .append(") { \n")
    .append("\t\tboolean result = repo.update(")
    .append(t.getCleanName())
    .append("Mapper.map")
    .append(t.getCleanName())
    .append("View(")
    .append("updated")
    .append(t.getCleanName())
    .append("));\n")
    .append("\t\treturn result;\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean delete(");
    if(isCurrentJoining) {
      int colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append(ColumnEnums.resolvePrimitiveType(c.getDataType()))
        .append(" ")
        .append(c.getPascalName());
        if(++colIndex < t.getColumns().size()) {
          b.append(" ,");
        }
      }
      b
      .append(") {\n")
      .append("\t\tboolean result = repo.delete(");
      colIndex = 0;
      for(Column c: t.getColumns()) {
        b
        .append(c.getPascalName());
        if(++colIndex < t.getColumns().size()) {
          b.append(" ,");
        }
      }
      b.append(");\n");
    } else {
      b
      .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
      .append(" id) {\n")
      .append("\t\tboolean result = repo.delete(id);\n");
    }
    b
    .append("\t\treturn result;\n")
    .append("\t}\n\n");
    return b.toString();
  }
}




