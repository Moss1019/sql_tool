
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ModelGenerator {
  private Database db;

  public ModelGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateModels(String packageName) {
    Map<String, String> models = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".model;\n\n")
      .append("import javax.persistence.*;\n\n")
      .append("import java.util.Date;\n\n")
      .append("import java.io.Serializable;\n\n")
      .append("@Entity\n");
      if(t.isJoiningTable()) {
        b
        .append("@IdClass(")
        .append(t.getCleanName())
        .append(".")
        .append(t.getCleanName())
        .append("PK.class)\n");
      }
      b
      .append("@Table(name = \"")
      .append(t.getName())
      .append("\")\n");
      if(!t.isJoiningTable()) {
        b
        .append(generateSelectAllProcedures(t))
        .append(generateSelectByPKProcedures(t))
        .append(generateSelectByUniqueColsProcedures(t))
        .append(generateDeleteProcedures(t))
        .append(generateUpdateProcedure(t));
      }
      b
      .append(generateSelectParentChildren(t))
      .append(generateInsertProcedures(t))
      .append(generateClassDef(t));
      models.put(t.getCleanName(), b.toString());
    }
    return models;
  }

  public String generateClassDef(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("public class ")
    .append(t.getCleanName())
    .append(" {\n")
    .append(generateFields(t.getColumns()))
    .append("\n")
    .append(generateGettersSetters(t.getColumns()));
    if(t.isJoiningTable()) {
      b
      .append(generatePKClass(t));
    }
    b
    .append("}\n");
    return b.toString();
  }

  private String generateSelectAllProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    List<Column> cols = null;
    b.append(generateNamedStoredProcedureQuery(String.format("selectAll%ss", t.getCleanName()), t.getCleanName(), cols));
    return b.toString();
  }

  private String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    for (Table parentTable : t.getParentTables()) {
      if(t.isJoiningTable()) {
        if(t.hasJoiningTable()) {
          b.append(generateNamedStoredProcedureQuery(String.format("select%ss", t.getCleanName()), 
          parentTable.getCleanName(), 
          t.getPsudoPrimaryColumn()));
          break;
        } else {
          b.append(generateNamedStoredProcedureQuery(String.format("select%s%ss", parentTable.getCleanName(), 
            t.getCleanName()), 
            parentTable.getCleanName(), 
            parentTable.getPrimaryColumn()));
        }
      } else {
        b.append(generateNamedStoredProcedureQuery(String.format("select%s%ss", parentTable.getCleanName(), 
          t.getCleanName()), 
          t.getCleanName(), 
          parentTable.getPrimaryColumn()));
      }
    }
    return b.toString();
  }

  private String generateSelectByPKProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(generateNamedStoredProcedureQuery(String.format("select%s", t.getCleanName()), t.getCleanName(), t.getPrimaryColumn()));
    return b.toString();
  }

  private String generateSelectByUniqueColsProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    List<Column> uniqueCols = t.getUniqueCols();
    for(Column col: uniqueCols) {
      b.append(generateNamedStoredProcedureQuery(String.format("select%ssBy%s", t.getCleanName(), col.getCleanName()), t.getCleanName(), col));
    }
    return b.toString();
  }

  private String generateInsertProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(generateNamedStoredProcedureQuery(String.format("insert%s", t.getCleanName()), null, t.getNonPrimaryCols()));
    return b.toString();
  }

  private String generateDeleteProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(generateNamedStoredProcedureQuery(String.format("delete%s", t.getCleanName()), null, t.getPrimaryColumn()));
    return b.toString();
  }

  private String generateUpdateProcedure(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(generateNamedStoredProcedureQuery(String.format("update%s", t.getCleanName()), null, t.getColumns()));
    return b.toString();
  }

  private String generatePKClass(Table t) {
    String className = String.format("%sPK", t.getCleanName());
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic class ")
    .append(className)
    .append(" implements Serializable {\n");
    for(Column col: t.getColumns()) {
      b
      .append("\t\tprotected ")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" ")
      .append(col.getPascalName())
      .append(";\n");
    }
    b
    .append("\n")
    .append("\t\tpublic ")
    .append(className)
    .append("() {\n")
    .append("\t\t}\n\n")
    .append("\t\tpublic ")
    .append(className)
    .append("(");
    int colIndex = 0;
    for(Column col: t.getColumns()) {
      b
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" ")
      .append(col.getPascalName());
      if(++colIndex < t.getColumns().size()) {
        b.append(", ");
      }
    }
    b
    .append(") {\n");
    for(Column col: t.getColumns()) {
      b
      .append("\t\t\tthis.")
      .append(col.getPascalName())
      .append(" = ")
      .append(col.getPascalName())
      .append(";\n");
    }
    b
    .append("\t\t}\n")
    .append("\t}\n");
    return b.toString();
  }

  private String generateParameter(Column col) {
    StringBuilder b = new StringBuilder();
    b
    .append("\t@StoredProcedureParameter(\n")
    .append("\t\tmode = ParameterMode.IN,\n\t\ttype = ")
    .append(ColumnEnums.resolveWrapperType(col.getDataType()))
    .append(".class,\n\t\tname = \"")
    .append(col.getName())
    .append("\")");
    return b.toString();
  }
  
  private String generateNamedStoredProcedureQuery(String queryName, String type, Column col) {
    List<Column> cols = new ArrayList<>();
    cols.add(col);
    return generateNamedStoredProcedureQuery(queryName, type, cols);
  }

  private String generateNamedStoredProcedureQuery(String queryName, String type, List<Column> parms) {
    StringBuilder b = new StringBuilder();
    b
    .append("@NamedStoredProcedureQuery(\n")
    .append("\tname = \"")
    .append(queryName)
    .append("\",\n")
    .append("\tprocedureName = \"sp_")
    .append(queryName)
    .append("\"");
    if(type != null) {
      b
      .append(",\n\tresultClasses = ")
      .append(type)
      .append(".class");
    }
    if (parms != null) {
      b
      .append(",\n\tparameters = {\n");
      int colIndex = 0;
      for(Column col: parms) {
        b.append(generateParameter(col));
        if(colIndex++ < parms.size() - 1) {
          b.append(",");
        }
        b.append("\n");
      }
      b.append("\t}\n");
    }
    b.append(")\n");
    return b.toString();
  }

  private String generateFields(List<Column> cols) {
    StringBuilder b = new StringBuilder();
    for(Column col: cols) {
      if(col.isPrimary() || col.getPsudoName() != null) {
        b.append("\t@Id\n");
      }
      b
      .append("\t@Column(name = \"")
      .append(col.getName())
      .append("\")\n")
      .append("\tprivate ")
      .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
      .append(" ")
      .append(col.getPascalName())
      .append(";\n");
    }
    return b.toString();
  }

  private String generateGettersSetters(List<Column> cols) {
    StringBuilder b = new StringBuilder();
    for(Column col: cols) {
      b
      .append(generateGetter(col))
      .append("\n")
      .append(generateSetter(col))
      .append("\n");
    }
    return b.toString();
  }

  private String generateGetter(Column col) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
    .append(" get")
    .append(col.getCleanName())
    .append("() {\n")
    .append("\t\treturn ")
    .append(col.getPascalName())
    .append(";\n\t}\n");
    return b.toString();
  }

  private String generateSetter(Column col) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic void set")
    .append(col.getCleanName())
    .append("(")
    .append(ColumnEnums.resolvePrimitiveType(col.getDataType()))
    .append(" ")
    .append(col.getPascalName())
    .append(") {\n")
    .append("\t\tthis.")
    .append(col.getPascalName())
    .append(" = ")
    .append(col.getPascalName())
    .append(";\n\t}\n");
    return b.toString();
  }
}
