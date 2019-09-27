
import java.util.List;
import java.util.stream.Collectors;

public class BeanGenerator {
    private Table table;
    private List<Column> primaryCols;
    private List<Column> nonPrimaryCols;

    public BeanGenerator(Table table) {
        this.table = table;
        primaryCols = table.getColumns().stream().filter(c -> c.getIsPrimary()).collect(Collectors.toList());
        nonPrimaryCols = table.getColumns().stream().filter(c -> !c.getIsPrimary()).collect(Collectors.toList());
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
        .append(ColumnEnums.resolveJavaType(primaryCols.get(0).getType()))
        .append(" ")
        .append(primaryCols.get(0).getFieldName())
        .append(") {\n")
        .append(generateQueryCode("select", primaryCols))
        .append("\t\tObject result = query.getSingleResult();\n")
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
        .append("s() {\n")
        .append("\t\tStoreProcedureQuery query = em.createNamedStoredProcedureQuery(\"select")
        .append(table.getName())
        .append("s\");\n")
        .append("\t\tList<")
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
        .append("\t\treturn query.execute();\n")
        .append("\t}\n");
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
        b.append("@Repository\npublic class ")
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
        b.append("@Service\npublic class ")
        .append(table.getName())
        .append("Service {\n")
        .append("\tprivate static final Logger L = Logger.getLogger(")
        .append(table.getName())
        .append("Service")
        .append(".class.toString());\n\n")
        .append("\t@Autowired\n")
        .append("\tprivate ")
        .append(table.getName())
        .append("Repository repo;")
        .append("\n}");
        return b.toString();
    }

    private String generateNamedStoredProcedureQueryParameter(Column col) {
        StringBuilder b = new StringBuilder();
        b.append("\t\t@StoredProcedureParameter(\n")
        .append("\t\t\tmode = ParameterMode.IN,\n\t\t\ttype = ")
        .append(ColumnEnums.resolveJavaType(col.getType()))
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
        b.append(generateParameterDefinition(columns));
        
        b.append(")\n");
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
            .append(ColumnEnums.resolveJavaType(col.getType()))
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
            .append(ColumnEnums.resolveJavaType(col.getType()))
            .append(" ")
            .append(col.getFieldName())
            .append(") {\n")
            .append("\t\tthis.")
            .append(col.getFieldName())
            .append(" = ")
            .append(col.getFieldName())
            .append(";\n\t}\n\n");
            b.append("\tpublic ")
            .append(ColumnEnums.resolveJavaType(col.getType()))
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
        b.append("import javax.persistence.*;\n\n")
        .append("@Entity\n")
        .append("@Table(name = \"")
        .append(table.getName())
        .append("\")\n");
        b.append(generateNamedStoredProcedureQuery("selectUsers", null, table.getName()))
        .append(generateNamedStoredProcedureQuery("selectUser", primaryCols, table.getName()))
        .append(generateNamedStoredProcedureQuery("insertUser", nonPrimaryCols, null))
        .append(generateNamedStoredProcedureQuery("updateUser", table.getColumns(), null))
        .append(generateNamedStoredProcedureQuery("deleteUser", primaryCols, null))
        .append("public class ")
        .append(table.getName())
        .append(" {\n");
        b.append(generateFields(table.getColumns()))
        .append(generateAccessorsMutators(table.getColumns()))
        .append("}\n");
        return b.toString();
    }
}