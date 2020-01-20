
import java.util.Map;
import java.util.HashMap;

public class RepositoryGenerator {
  private Database db;

  public RepositoryGenerator(Database db) {
    this.db = db;
  }

  public Map<String, String> generateRepositories(String packageName) {
    Map<String, String> repositories = new HashMap<>();
    for(Table t: db.getTables()) {
      StringBuilder b = new StringBuilder();
      b
      .append("package ")
      .append(packageName)
      .append(".repository;\n\n")
      .append("import org.springframework.stereotype.Repository;\n\n")
      .append("import javax.persistence.*;\n\n")
      .append("import java.util.List;\n\n")
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
      .append(";\n\n")
      .append("@Repository\n")
      .append("public class ")
      .append(t.getCleanName())
      .append("Repository {\n")
      .append("\t@PersistenceContext\n\tprivate EntityManager em;\n\n")
      .append(generateInsert(t))
      .append(generateSelectParentChildren(t));
      if(!t.isJoiningTable()) {
        b
        .append(generateDelete(t))
        .append(generateSelectByPK(t))
        .append(generateSelectAll(t))
        .append(generateSelectByUnique(t))
        .append(generateUpdate(t));
      }
      b.append("}\n");
      repositories.put(String.format("%sRepository", t.getCleanName()), b.toString());
    }
    return repositories;
  }

  private String generateSelectByPK(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic ")
    .append(t.getCleanName())
    .append(" selectByPk(")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" value) {\n")
    .append(generateStoredProcedureQuery(String.format("select%s", t.getCleanName())))
    .append(generateSetParameter(t.getPrimaryColumn().getName(), "value"))
    .append("\t\ttry {\n\t")
    .append(generateSelect(t, true))
    .append("\t\t} catch (NoResultException ex) {\n")
    .append("\t\t\treturn null;\n")
    .append("\t\t}\n")
    .append("\t}\n\n");
    return b.toString();
  }

  public String generateSelectParentChildren(Table t) {
    StringBuilder b = new StringBuilder();
    for (Table parentTable: t.getParentTables()) {
      if (t.isJoiningTable()) {
        if(t.hasJoiningTable()) {
          b
          .append("\tpublic List<")
          .append(parentTable.getCleanName())
          .append("> ")
          .append(String.format("select%ss", t.getCleanName()))
          .append("(")
          .append(ColumnEnums.resolvePrimitiveType(t.getPsudoPrimaryColumn().getDataType()))
          .append(" ")
          .append(t.getPsudoPrimaryColumn().getPascalName())
          .append(") {\n")
          .append(generateStoredProcedureQuery(String.format("select%ss", t.getCleanName())))
          .append(generateSetParameter(t.getPsudoPrimaryColumn().getName(), t.getPsudoPrimaryColumn().getPascalName()))
          .append(generateSelect(parentTable, false))
          .append("\t}\n\n");
        } else {
          b
          .append("\tpublic List<")
          .append(parentTable.getCleanName())
          .append("> ")
          .append(String.format("select%s%ss", parentTable.getCleanName(), t.getCleanName()))
          .append("(")
          .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
          .append(" ")
          .append(parentTable.getPrimaryColumn().getPascalName())
          .append(") {\n")
          .append(generateStoredProcedureQuery(String.format("select%s%ss", parentTable.getCleanName(), t.getCleanName())))
          .append(generateSetParameter(parentTable.getPrimaryColumn().getName(), parentTable.getPrimaryColumn().getPascalName()))
          .append(generateSelect(parentTable, false))
          .append("\t}\n\n");
        }
        break;
      } else {
        b
        .append("\tpublic List<")
        .append(t.getCleanName())
        .append("> ")
        .append(String.format("selectOf%s", parentTable.getCleanName()))
        .append("(")
        .append(ColumnEnums.resolvePrimitiveType(parentTable.getPrimaryColumn().getDataType()))
        .append(" ")
        .append(parentTable.getPrimaryColumn().getPascalName())
        .append(") {\n")
        .append(generateStoredProcedureQuery(String.format("select%s%ss", parentTable.getCleanName(), t.getCleanName())))
        .append(generateSetParameter(parentTable.getPrimaryColumn().getName(), parentTable.getPrimaryColumn().getPascalName()))
        .append(generateSelect(t, false))
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
    .append("> selectAll() {\n")
    .append(generateStoredProcedureQuery(String.format("selectAll%ss", t.getCleanName())))
    .append(generateSelect(t, false))
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getUniqueCols()) {
      b
      .append("\tpublic ")
      .append(t.getCleanName())
      .append(" selectBy")
      .append(c.getCleanName())
      .append("(")
      .append(ColumnEnums.resolvePrimitiveType(c.getDataType()))
      .append(" value) {\n")
      .append(generateStoredProcedureQuery(String.format("select%ssBy%s", t.getCleanName(), c.getCleanName())))
      .append(generateSetParameter(c.getName(), "value"))
      .append("\t\ttry {\n\t")
      .append(generateSelect(t, true))
      .append("\t\t} catch (NoResultException ex) {\n")
      .append("\t\t\treturn null;\n")
      .append("\t\t}\n")
      .append("\t}\n");
      if(colIndex++ < t.getUniqueCols().size() - 1) {
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
    .append(" insert(")
    .append(t.getCleanName())
    .append(" value) {\n")
    .append(generateStoredProcedureQuery(String.format("insert%s", t.getCleanName())));
    for(Column c: t.getNonPrimaryCols()) {
      b.append(generateSetParameter(c.getName(), String.format("value.get%s()", c.getCleanName())));
    }
    b
    .append("\t\ttry {\n")
    .append("\t\t\tq.execute();\n")
    .append("\t\t\treturn (")
    .append(t.getCleanName())
    .append(")q.getSingleResult();\n")
    .append("\t\t} catch (Exception ex) {\n")
    .append("\t\t\treturn null;\n")
    .append("\t\t}\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean update(")
    .append(t.getCleanName())
    .append(" value) {\n")
    .append(generateStoredProcedureQuery(String.format("update%s", t.getCleanName())));
    for(Column c: t.getColumns()) {
      b.append(generateSetParameter(c.getName(), String.format("value.get%s()", c.getCleanName())));
    }
    b
    .append("\t\ttry {\n")
    .append("\t\t\tq.execute();\n")
    .append("\t\t\treturn true;\n")
    .append("\t\t} catch (Exception ex) {\n")
    .append("\t\t\treturn false;\n")
    .append("\t\t}\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append("\tpublic boolean delete(")
    .append(ColumnEnums.resolvePrimitiveType(t.getPrimaryColumn().getDataType()))
    .append(" value) {\n")
    .append(generateStoredProcedureQuery(String.format("delete%s", t.getCleanName())))
    .append(generateSetParameter(t.getPrimaryColumn().getName(), "value"))
    .append("\t\ttry {\n")
    .append("\t\t\tq.execute();\n")
    .append("\t\t\treturn true;\n")
    .append("\t\t} catch (Exception ex) {\n")
    .append("\t\t\treturn false;\n")
    .append("\t\t}\n")
    .append("\t}\n\n");
    return b.toString();
  }

  private String generateStoredProcedureQuery(String queryName) {
    return String.format("\t\tStoredProcedureQuery q = em.createNamedStoredProcedureQuery(\"%s\");\n", queryName);
  }

  private String generateSetParameter(String parmName, String argumentName) {
    return String.format("\t\tq.setParameter(\"%s\", %s);\n", parmName, argumentName);
  }

  private String generateSelect(Table t, boolean isSingleResult) {
    return String.format("\t\t%s result = (%s)q.%s();\n\t\t%sreturn result;\n",
      isSingleResult ? t.getCleanName() : String.format("List<%s>", t.getCleanName()),
      isSingleResult ? t.getCleanName() : String.format("List<%s>", t.getCleanName()),
      isSingleResult ? "getSingleResult" : "getResultList",
      isSingleResult ? "\t" : "");
  }
}