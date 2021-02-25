
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
      if(t.isJoiningTable()) {
        b
        .append(t.getParentTables().get(0).getCleanName())
        .append(";\n")
        .append("import ")
        .append(packageName)
        .append(".model.")
        .append(t.getCleanName());
      } else {
        b
        .append(t.getCleanName());
      }
      b
      .append(";\nimport ")
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
      .append("import java.util.stream.Collectors;\n");
      b
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
      b
      .append('\n')
      .append(generateInsert(t))
      .append(generateSelectParentChildren(t))
      .append(generateDelete(t));
      if(!t.isJoiningTable()) {
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
// 	public List<ItemView> selectOfUser(int userId) {
// 		List<Item> dbResult = repo.selectOfUser(userId);
// 		List<ItemView> result = dbResult.
// 				stream()
// 				.map(x -> {
// 					List<MilestoneView> milestoneViews = milestoneService.selectOfItem(x.getItemId());
// 					return ItemMapper.mapItem(x, milestoneViews);
// 				})
// 				.collect(Collectors.toList());
// 		return result;
// 	}
  private String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    for (Table parentTable: t.getParentTables()) {
      if(t.isJoiningTable()) {
        if(t.hasJoiningTable()) {
          b
          .append("\tpublic ")
          .append("List<")
          .append(parentTable.getCleanName())
          .append("View> ")
          .append(String.format("select%ss", t.getCleanName()))
          .append("(")
          .append(ColumnEnums.resolvePrimitiveType(t.getPsudoPrimaryColumn().getDataType()))
          .append(" ")
          .append(t.getPsudoPrimaryColumn().getPascalName())
          .append(") {\n")
          .append("\t\tList<")
          .append(parentTable.getCleanName())
          .append("> dbResult = repo.")
          .append(String.format("select%ss", t.getCleanName()))
          .append("(")
          .append(t.getPsudoPrimaryColumn().getPascalName())
          .append(");\n")
          .append("\t\t")
          .append("List<")
          .append(parentTable.getCleanName())
          .append("View> result = null;\n")
          .append("\t\treturn result;\n")
          .append("\t}\n\n");
        } else {
          b
          .append("\tpublic ")
          .append("List<")
          .append(parentTable.getCleanName())
          .append("View> ")
          .append(String.format("select%s%ss", parentTable.getCleanName(), t.getCleanName()))
          .append("(")
          .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
          .append(" ")
          .append(parentTable.getPrimaryColumn().getPascalName())
          .append(") {\n")
          .append("\t\tList<")
          .append(parentTable.getCleanName())
          .append("> dbResult = repo.")
          .append(String.format("select%s%ss", parentTable.getCleanName(), t.getCleanName()))
          .append("(")
          .append(parentTable.getPrimaryColumn().getPascalName())
          .append(");\n")
          .append("\t\t")
          .append("List<")
          .append(t.getCleanName())
          .append("View> result = null;\n")
          .append("\t\treturn result;\n")
          .append("\t}\n\n");
        }
        break;
      }
      b
      .append("\tpublic ")
      .append("List<")
      .append(t.getCleanName())
      .append("View> selectOf")
      .append(parentTable.getCleanName())
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
      .append(" ")
      .append(parentTable.getPrimaryColumn().getPascalName())
      .append(") {\n")
      .append("\t\tList<")
      .append(t.getCleanName())
      .append("> dbResult = repo.selectOf")
      .append(parentTable.getCleanName())
      .append("(")
      .append(parentTable.getPrimaryColumn().getPascalName())
      .append(");\n")
      .append("\t\t")
      .append("List<")
      .append(t.getCleanName())
      .append("View> result = null;\n")
      .append("\t\treturn result;\n")
      .append("\t}\n\n");
    }
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
      .append("(value);\n")
      .append("\t\t")
      .append(t.getCleanName())
      .append("View result = null;\n")
      .append("\t\treturn result;\n")
      .append("\t}\n");
      if(++colIndex < t.getColumns().size()) {
        b.append("\n");
      }
    }
    b.append("\n");
    return b.toString();
  }
// public ItemView insert(ItemView newItem) { 
// 		Item dbResult = repo.insert(ItemMapper.mapItemView(newItem));
// 		List<MilestoneView> milestones = milestoneService.selectOfItem(dbResult.getItemId());
// 		ItemView result = ItemMapper.mapItem(dbResult, milestones);
// 		return result;
// 	}
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
    .append("));\n")
    .append("\t\t")
    .append(t.getCleanName())
    .append("View result = null;\n")
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
    if(t.isJoiningTable()) {
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




