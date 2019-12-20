
import java.util.List;
import java.util.ArrayList;

class Table {
    private String name;
    private List<Column> columns;
    private List<Column> uniqueCols;
    private Column primaryColumn;
    private boolean isJoiningTable;
    private boolean hasJoiningTable;

    public Table(String name, List<Column> columns, boolean hasJoiningTable) {
        this.name = name;
        this.columns = columns;
        isJoiningTable = true;
        this.hasJoiningTable = hasJoiningTable;
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
        }
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

    public boolean isJoiningTable() {
        return isJoiningTable;
    }

    public boolean hasJoiningTable() {
        return hasJoiningTable;
    }

    public List<Column> getUniqueCols() {
        return uniqueCols;
    }

    public List<Column> getNonPrimaryCols() {
        List<Column> cols = new ArrayList();
        for(Column col: columns) {
            if(!col.isPrimary()) {
                cols.add(col);
            }
        }
        return cols;
    }
}

// test {
//   test_id int primary auto_increment
//   subject_id int foreign
// }
