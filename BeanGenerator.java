
import java.util.List;
import java.util.stream.Collectors;

public class BeanGenerator {

    public String generateRepo(Table table) {
        StringBuilder b = new StringBuilder();
        b.append("@Repository\npublic class ")
        .append(table.getName())
        .append("Repository {\n")
        
        .append("}");
        return b.toString();
    }

    public String generateService(Table table) {
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
            .append(col.getName())
            .append(";\n");
        }
        b.append("\n");
        return b.toString();
    }

    private String generateAccessorsMutators(List<Column> columns) {
        StringBuilder b = new StringBuilder();
        for(Column col: columns) {
            b.append("\tpublic void set")
            .append(col.getName())
            .append("(")
            .append(ColumnEnums.resolveJavaType(col.getType()))
            .append(" ")
            .append(col.getName().toLowerCase())
            .append(") {\n")
            .append("\t\tthis.")
            .append(col.getName())
            .append(" = ")
            .append(col.getName())
            .append(";\n\t}\n\n");
            b.append("\tpublic ")
            .append(ColumnEnums.resolveJavaType(col.getType()))
            .append(" get")
            .append(col.getName())
            .append("() {\n")
            .append("\t\treturn ")
            .append(col.getName())
            .append(";\n\t}\n\n");
        }
        return b.toString();
    }

    public String generateEntity(Table table) {
        StringBuilder b = new StringBuilder();
        b.append("import javax.persistence.*;\n\n")
        .append("@Entity\n")
        .append("@Table(name = \"")
        .append(table.getName())
        .append("\")\n");
        List<Column> primaryCol = table.getColumns().stream().filter(c -> c.getIsPrimary()).collect(Collectors.toList());
        List<Column> otherCols = table.getColumns().stream().filter(c -> !c.getIsPrimary()).collect(Collectors.toList());
        b.append(generateNamedStoredProcedureQuery("selectUsers", null, table.getName()))
        .append(generateNamedStoredProcedureQuery("selectUser", primaryCol, table.getName()))
        .append(generateNamedStoredProcedureQuery("insertUser", otherCols, null))
        .append(generateNamedStoredProcedureQuery("updateUser", table.getColumns(), null))
        .append(generateNamedStoredProcedureQuery("deleteUser", primaryCol, null))
        .append("public class ")
        .append(table.getName())
        .append(" {\n");
        b.append(generateFields(table.getColumns()))
        .append(generateAccessorsMutators(table.getColumns()))
        .append("}\n");
        return b.toString();
    }
}