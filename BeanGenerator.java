
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanGenerator {
    private Table table;
    private String packageName; 
    private List<Column> primaryCols;
    private List<Column> nonPrimaryCols;

    public BeanGenerator(Table table, String packageName) {
        this.table = table;
        this.packageName = packageName;
        primaryCols = table.getColumns().stream().filter(c -> c.getIsPrimary()).collect(Collectors.toList());
        nonPrimaryCols = table.getColumns().stream().filter(c -> !c.getIsPrimary()).collect(Collectors.toList());
    }

    public String generateController() {
        StringBuilder b = new StringBuilder();
        b.append("package ")
        .append(packageName)
        .append(".controller;\n\n")
        .append("import org.springframework.beans.factory.annotation.Autowired;\n")
        .append("import org.springframework.http.ResponseEntity;\n")
        .append("import org.springframework.web.bind.annotation.*;\n\n")
        .append("import java.util.logging.Logger;\n")
        .append("import java.util.logging.Level;\n")
        .append("import java.util.List;\n\n")
        .append("import ")
        .append(packageName)
        .append(".service.")
        .append(table.getName())
        .append("Service;\n\n")
        .append("@RestController\n")
        .append("@RequestMapping(value = \"api/")
        .append(table.getName().toLowerCase())
        .append("s\")\npublic class ")
        .append(table.getName())
        .append("Controller {\n")
        .append("\tprivate static final Logger L = Logger.getLogger(")
        .append(table.getName())
        .append("Controller.class.toString());\n\n")
        .append("\t@Autowired\n\tprivate ")
        .append(table.getName())
        .append("Service service;\n\n")
        .append("}\n");
        return b.toString();
    }

    private String generateQueryCode(String queryName, List<Column> parameterColumns) {
        StringBuilder b = new StringBuilder();
        b.append("\t\tStoredProcedureQuery query = em.createNamedStoredProcedureQuery(\"")
        .append(queryName)
        .append(table.getName())
        .append("\");\n");
        if(parameterColumns != null) {
            for(Column col: parameterColumns) {
                b.append("\t\tquery.setParameter(\"")
                .append(col.getName())
                .append("\", p.get")
                .append(col.getFieldNameCaps())
                .append("());\n");
            }
        }
        return b.toString();
    }

    private String generateCallSelect() {
        StringBuilder b = new StringBuilder();
        b.append("\t")
        .append("public ")
        .append(table.getName())
        .append(" select")
        .append(table.getName())
        .append("(")
        .append(ColumnEnums.resolveWrapperType(primaryCols.get(0).getType()))
        .append(" ")
        .append(primaryCols.get(0).getFieldName())
        .append(") {\n")
        .append("\t\tStoredProcedureQuery query = em.createNamedStoredProcedureQuery(\"select")
        .append(table.getName())
        .append("\");\n\t\tquery.setParameter(\"")
        .append(primaryCols.get(0).getName())
        .append("\",")
        .append(primaryCols.get(0).getFieldName())
        .append(");\n\t\tObject result = query.getSingleResult();\n")
        .append("\t\treturn (")
        .append(table.getName())
        .append(")result;\n")
        .append("\t}\n");
        return b.toString();
    }

    private String generateCallSelectAll() {
        StringBuilder b = new StringBuilder();
        b.append("\tpublic List<")
        .append(table.getName())
        .append("> select")
        .append(table.getName())
        .append("s(");
        Optional<Column> foreignKeyCol = table.getColumns().stream()
            .filter(col -> col.getReferencedTable() != null)
            .findFirst();
        if(foreignKeyCol.isPresent()) {
            b.append(ColumnEnums.resolveWrapperType(foreignKeyCol.get().getType()))
            .append(" ")
            .append(foreignKeyCol.get().getFieldName());
        }
        b.append(") {\n")
        .append("\t\tStoredProcedureQuery query = em.createNamedStoredProcedureQuery(\"select")
        .append(table.getName())
        .append("s\");\n");
        if(foreignKeyCol.isPresent()) {
            b.append("\t\tquery.setParameter(\"")
            .append(foreignKeyCol.get().getName())
            .append("\", ")
            .append(foreignKeyCol.get().getFieldName())
            .append(");\n");
        }
        b.append("\t\tList<")
        .append(table.getName())
        .append("> result = query.getResultList();\n")
        .append("\t\treturn (List<")
        .append(table.getName())
        .append(">)result;\n\t}\n");
        return b.toString();
    }

    private String generateCallInsert() {
        StringBuilder b = new StringBuilder();
        b.append("\tpublic boolean insert")
        .append(table.getName())
        .append("(")
        .append(table.getName())
        .append(" p")
        .append(") {\n")
        .append(generateQueryCode("insert", nonPrimaryCols))
        .append("\t\treturn query.execute();\n\t}\n");
        return b.toString();
    }

    private String generateCallUpdate() {
        StringBuilder b = new StringBuilder();
        b.append("\tpublic ")
        .append(table.getName())
        .append(" update")
        .append(table.getName())
        .append("(")
        .append(table.getName())
        .append(" p")
        .append(") {\n")
        .append(generateQueryCode("update", table.getColumns()))
        .append("\t\tint affectedRows = query.executeUpdate();\n")
        .append("\t\tif(affectedRows > 0) {\n")
        .append("\t\t\treturn p;\n")
        .append("\t\t}\n")
        .append("\t\treturn null;\n\t}\n");
        return b.toString();
    }

    private String generateCallDelete() {
        StringBuilder b = new StringBuilder();
        b.append("\tpublic ")
        .append(table.getName())
        .append(" delete")
        .append(table.getName())
        .append("(")
        .append(table.getName())
        .append(" p")
        .append(") {\n")
        .append(generateQueryCode("delete", primaryCols))
        .append("\t\tint affectedRows = query.executeUpdate();\n")
        .append("\t\tif(affectedRows > 0) {\n")
        .append("\t\t\treturn p;\n")
        .append("\t\t}\n")
        .append("\t\treturn null;\n\t}\n");
        return b.toString();
    }

    public String generateRepo() {
        StringBuilder b = new StringBuilder();
        b.append("package ")
        .append(packageName)
        .append(".repository;\n\nimport org.springframework.stereotype.Repository;\n\n")
        .append("import javax.persistence.*;\n\n")
        .append("import java.util.List;\n\n")
        .append("import ")
        .append(packageName)
        .append(".model.")
        .append(table.getName())
        .append(";\n\n@Repository\npublic class ")
        .append(table.getName())
        .append("Repository {\n\t@PersistenceContext\n\tprivate EntityManager em;\n\n")
        .append(generateCallSelect())
        .append("\n")
        .append(generateCallSelectAll())
        .append("\n")
        .append(generateCallInsert())
        .append("\n")
        .append(generateCallUpdate())
        .append("\n")
        .append(generateCallDelete())
        .append("}");
        return b.toString();
    }

    public String generateService() {
        StringBuilder b = new StringBuilder();
        b.append("package ")
        .append(packageName)
        .append(".service;\n\nimport org.springframework.stereotype.Service;\n")
        .append("import org.springframework.beans.factory.annotation.Autowired;\n\n")
        .append("import java.util.logging.Logger;\n")
        .append("import java.util.List;\n\n")
        .append("import ")
        .append(packageName)
        .append(".model.")
        .append(table.getName())
        .append(";\n\n@Service\npublic class ")
        .append(table.getName())
        .append("Service {\n")
        .append("\tprivate static final Logger L = Logger.getLogger(")
        .append(table.getName())
        .append("Service")
        .append(".class.toString());\n\n")
        .append("\t@Autowired\n\tprivate ")
        .append(table.getName())
        .append("Repository repo;\n")
        .append("}");
        return b.toString();
    }

    private String generateNamedStoredProcedureQueryParameter(Column col) {
        StringBuilder b = new StringBuilder();
        b.append("\t\t@StoredProcedureParameter(\n")
        .append("\t\t\tmode = ParameterMode.IN,\n\t\t\ttype = ")
        .append(ColumnEnums.resolveWrapperType(col.getType()))
        .append(".class")
        .append(",\n\t\t\tname = \"")
        .append(col.getName())
        .append("\")");
        return b.toString();
    }

    private String generateParameterDefinition(List<Column> columns) {
        if(columns == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        b.append(",\n\tparameters = {\n");
        int i = 0;
        for(Column col: columns) {
            b.append(generateNamedStoredProcedureQueryParameter(col));
            if(++i < columns.size()) {
                b.append(",\n");
            }
        }
        b.append("\n\t}\n");
        return b.toString();
    }

    private String generateNamedStoredProcedureQuery(String procName, List<Column> columns, String resultClass) {
        StringBuilder b = new StringBuilder();
        b.append("@NamedStoredProcedureQuery(\n\tname = \"")
        .append(procName)
        .append("\",\n")
        .append("\tprocedureName = \"sp_")
        .append(procName.toLowerCase())
        .append("\"");
        if(resultClass != null) {
            b.append(",\n\tresultClasses = ")
            .append(resultClass)
            .append(".class");
        }
        b.append(generateParameterDefinition(columns))
        .append(")\n");
        return b.toString();
    }

    private String generateFields(List<Column> columns) {
        StringBuilder b = new StringBuilder();
        for (Column col: columns) {
            if(col.getIsPrimary()) {
                b.append("\t@Id\n");
            }
            b.append("\t@Column(name = \"")
            .append(col.getName())
            .append("\")\n\tprivate ")
            .append(ColumnEnums.resolvePrimitiveType(col.getType()))
            .append(" ")
            .append(col.getFieldName())
            .append(";\n");
        }
        b.append("\n");
        return b.toString();
    }

    private String generateAccessorsMutators(List<Column> columns) {
        StringBuilder b = new StringBuilder();
        for(Column col: columns) {
            b.append("\tpublic void set")
            .append(col.getFieldNameCaps())
            .append("(")
            .append(ColumnEnums.resolvePrimitiveType(col.getType()))
            .append(" ")
            .append(col.getFieldName())
            .append(") {\n")
            .append("\t\tthis.")
            .append(col.getFieldName())
            .append(" = ")
            .append(col.getFieldName())
            .append(";\n\t}\n\n")
            .append("\tpublic ")
            .append(ColumnEnums.resolvePrimitiveType(col.getType()))
            .append(" get")
            .append(col.getFieldNameCaps())
            .append("() {\n")
            .append("\t\treturn ")
            .append(col.getFieldName())
            .append(";\n\t}\n\n");
        }
        return b.toString();
    }

    public String generateEntity() {
        StringBuilder b = new StringBuilder();
        b.append("package ")
        .append(packageName)
        .append(".model;\n\nimport javax.persistence.*;\n\n")
        .append("@Entity\n")
        .append("@Table(name = \"")
        .append(table.getName())
        .append("\")\n")
        .append(generateNamedStoredProcedureQuery(String.format("select%ss", table.getName()), null, table.getName()))
        .append(generateNamedStoredProcedureQuery(String.format("select%s", table.getName()), primaryCols, table.getName()))
        .append(generateNamedStoredProcedureQuery(String.format("insert%s", table.getName()), nonPrimaryCols, null))
        .append(generateNamedStoredProcedureQuery(String.format("update%s", table.getName()), table.getColumns(), null))
        .append(generateNamedStoredProcedureQuery(String.format("delete%s", table.getName()), primaryCols, null))
        .append("public class ")
        .append(table.getName())
        .append(" {\n")
        .append(generateFields(table.getColumns()))
        .append(generateAccessorsMutators(table.getColumns()))
        .append("}\n");
        return b.toString();
    }
}