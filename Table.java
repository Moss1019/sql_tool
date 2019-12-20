
import java.util.List;

class Table {
    private String name;
    private List<Column> columns;
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
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getPrimaryColumn() {
        return primaryColumn;
    }
}

// test {
//   test_id int primary auto_increment
//   subject_id int foreign
// }
