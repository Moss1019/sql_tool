

import java.util.List;

public class SqlGenerator {


    /*
    create table users(
        id INTEGER PRIMARY KEY AUTO_INCREMENT,
        first_name CHAR(32),
        last_name CHAR(32),
        is_active BIT
    );
    */
    public String generateCreateTable(Table table) {
        List<Column> cols = table.getColumns();
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("create table ").append(table.getName()).append("(\n");
        int i = 0;
        for(Column col: cols) {
            statementBuilder.append(col.getName()).append(" ");
            statementBuilder.append(ColumnEnums.resolveType(col.getType())).append(" ");
            for(ColumnEnums.Option option: col.getOptions()) {
                statementBuilder.append(ColumnEnums.resolveOption(option)).append(" ");
            }
            if (++i < cols.size()) {
                statementBuilder.append(",\n");
            } else {
                statementBuilder.append("\n");
            }
        }
        statementBuilder.append(");");
        return statementBuilder.toString();
    }

    public String generateSelectAllProc(Table table) {

        return "";
    }

    public String generateSelectByPKProc(Table table) {

        return ""; 
    }

    public String generateCreateProc(Table table) {

        return "";
    }

    public String generateUpdateProc(Table table) {
        
        return "";
    }

    public String generateDeleteProc(Table table) {

        return "";
    }
} 