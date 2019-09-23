
import java.util.List;
import java.util.ArrayList;

class Table {
    private String name;
    private List<Column> columns;

    public Table() {
        columns = new ArrayList<>();
    }

    public Table(String name) {
        this();
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }
}