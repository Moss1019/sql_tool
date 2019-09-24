
import java.util.List;

public class SqlGenerator {
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
        String procName = String.format("sp_select%ss", table.getName().toLowerCase());
        return String.format("create procedure %s () select * from %s;", procName, table.getName());
    }

    public String generateSelectByPKProc(Table table) {
        Column primaryCol = table.getPrimaryColumn();
        if(primaryCol == null) {
            return "<no primary key specified for " + table.getName() + ">";
        }
        String procName = String.format("sp_select%s", table.getName().toLowerCase());
        StringBuilder procBuilder = new StringBuilder();
        procBuilder
        .append("delimiter //\n")
        .append("create procedure ")
        .append(procName)
        .append("\n")
        .append("(in ")
        .append(primaryCol.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(primaryCol.getType()))
        .append(")\n")
        .append("begin\n")
        .append("select * from ")
        .append(table.getName())
        .append(" t1 where t1.")
        .append(primaryCol.getName())
        .append(" = ")
        .append(primaryCol.getName())
        .append(";\nend\n\\\\\ndelimiter ;");
        return procBuilder.toString(); 
    }

    public String generateInsertProc(Table table) {
        StringBuilder procBuilder = new StringBuilder();
        String procName = String.format("sp_insert%s", table.getName().toLowerCase());
        procBuilder
        .append("delimiter //\n")
        .append("create procedure ")
        .append(procName)
        .append("\n(");
        int i = 0;
        List<Column> cols = table.getColumns();
        for(Column col: cols) {
            procBuilder
            .append("in ")
            .append(col.getName())
            .append(" ")
            .append(ColumnEnums.resolveType(col.getType()));
            if(++i < cols.size()) {
                procBuilder.append(", ");
            }
        }
        procBuilder
        .append(")\n")
        .append("begin\n")
        .append("insert into ")
        .append(table.getName())
        .append("(");
        i = 0;
        for(Column col: cols) {
            procBuilder
            .append(col.getName());
            if(++i < cols.size()) {
                procBuilder.append(", ");
            }
        }
        procBuilder
        .append(")\nvalues(\n");
        i = 0;
        for(Column col: cols) {
            procBuilder
            .append(col.getName());
            if(++i < cols.size()) {
                procBuilder.append(", ");
            }
        }
        procBuilder
        .append(")\nend \n\\\\\n")
        .append("delimiter ;");
        return procBuilder.toString();
    }

    public String generateUpdateProc(Table table) {
        Column primaryCol = table.getPrimaryColumn();
        if(primaryCol == null) {
            return "<no primary key specified for " + table.getName() + ">";
        }
        StringBuilder procBuilder = new StringBuilder();
        String procName = String.format("sp_update%s", table.getName().toLowerCase());
        procBuilder
        .append("delimiter //\ncreate procedure ")
        .append(procName)
        .append("\n(");
        List<Column> cols = table.getColumns();
        int i = 0;
        for(Column col: cols) {
            procBuilder
            .append("in ")
            .append(col.getName())
            .append(" ")
            .append(ColumnEnums.resolveType(col.getType()));
            if(++i < cols.size()) {
                procBuilder.append(", ");
            }
        }
        procBuilder
        .append(")\n")
        .append("update ")
        .append(table.getName())
        .append(" t1 set ");
        i = 0;
        for(Column col: cols) {
            ++i;
            if(col.getIsPrimary()) {
                continue;
            }
            procBuilder
            .append(col.getName())
            .append(" = ")
            .append(col.getName());
            if(i < cols.size()) {
                procBuilder.append(", ");
            }
        }
        procBuilder
        .append(" where ")
        .append("t1.")
        .append(primaryCol.getName())
        .append(" = ")
        .append(primaryCol.getName())
        .append(")\nend \n\\\\\n")
        .append("delimiter ;");
        return procBuilder.toString();
    }

    public String generateDeleteProc(Table table) {
        Column primaryCol = table.getPrimaryColumn();
        if(primaryCol == null) {
            return "<no primary key specified for " + table.getName() + ">";
        }
        StringBuilder procBuilder = new StringBuilder();
        String procName = String.format("sp_delete%s", table.getName().toLowerCase());
        procBuilder
        .append("delimiter //\n")
        .append("create procedure ")
        .append(procName)
        .append("\n")
        .append("(in ")
        .append(primaryCol.getName())
        .append(" ")
        .append(ColumnEnums.resolveType(primaryCol.getType()))
        .append(")\n")
        .append("begin\n")
        .append("delete from ")
        .append(table.getName())
        .append(" t1\n")
        .append("where t1.")
        .append(primaryCol.getName())
        .append(" = ")
        .append(primaryCol.getName())
        .append(";\nend\n\\\\\ndelimiter ;");
        return procBuilder.toString();
    }
} 