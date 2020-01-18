
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Table {
    static private Map<String, Table> registry = new HashMap<>();

    private String name;
    private List<Column> columns;
    private List<Column> uniqueCols;
    private Column primaryColumn;
    private Column psudoPrimaryColumn;
    private boolean isJoiningTable;
    private boolean hasJoiningTable;
    private List<Table> parentTables;

    public Table(String name, List<Column> columns, boolean hasJoiningTable) {
        this.name = name;
        this.columns = columns;
        this.hasJoiningTable = hasJoiningTable;
        this.isJoiningTable = true;
        this.parentTables = new ArrayList<>();
        for(Column col: columns) {
            if(col.isPrimary()) {
                primaryColumn = col;
                isJoiningTable = false;
                break;
            }
        } 
        uniqueCols = new ArrayList<>();
        for(Column col: columns) {
            for(ColumnEnums.Option option: col.getOptions()) {
                if(option == ColumnEnums.Option.unique) {
                    uniqueCols.add(col);
                    break;
                }
            }
            if (col.getOptions().contains(ColumnEnums.Option.foreignKey)) {
                String columnName = col.getPsudoName() != null ? col.getPsudoName() : col.getName();
                String tableName = columnName.substring(0, columnName.indexOf("_"));
                if(registry.containsKey(tableName)) {
                    parentTables.add(registry.get(tableName));
                }
            }
            if (col.getPsudoName() != null) {
                this.psudoPrimaryColumn = col;
            }
        }
        registry.put(name, this);
    }

    public String getName() {
        return name;
    }

    public String getPascalName() {
        return String.format("%c%s", name.toUpperCase().charAt(0), name.substring(1));
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getPrimaryColumn() {
        return primaryColumn;
    }

    public Column getPsudoPrimaryColumn() {
        return psudoPrimaryColumn;
    }

    public boolean isJoiningTable() {
        return isJoiningTable;
    }

    public boolean hasJoiningTable() {
        return hasJoiningTable;
    }

    public List<Column> getUniqueCols() {
        return uniqueCols;
    }

    public List<Table> getParentTables() {
        return parentTables;
    }

    public void setParentTables(List<Table> parentTables) {
        this.parentTables = parentTables;
    }

    public List<Column> getNonPrimaryCols() {
        List<Column> cols = new ArrayList<>();
        for(Column col: columns) {
            if(!col.isPrimary()) {
                cols.add(col);
            }
        }
        return cols;
    }

    public String getCleanName() {
        String[] parts = name.split("_");
        if(parts.length > 0) {
            StringBuilder b = new StringBuilder();
            for(String part: parts) {
                b.append(String.format("%c%s", part.toUpperCase().charAt(0), part.substring(1)));
            }
            return b.toString();
        } else {
            return getPascalName();
        }
    }

    public String getLowerCasedName() {
        return this.getCleanName().toLowerCase();
    }
}

// test {
//   test_id int primary auto_increment
//   subject_id int foreign
// }
