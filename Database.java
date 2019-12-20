
import java.util.List;
import java.util.ArrayList;

public class Database {
    private List<Table> tables;

    public Database(List<Table> tables) {
        this.tables = tables;
    }

    public List<Table> getTables() {
        return tables;
    }

    @Override 
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(Table t: tables) {
            b.append(t.getName()).append("\n");
            for(Column c: t.getColumns()) {
                b
                .append(" ")
                .append(c.getName());
                try {
                    b
                    .append(" ")
                    .append(ColumnEnums.resolveType(c.getDataType()))
                    .append(" ");
                    for(ColumnEnums.Option o: c.getOptions()) {
                        b
                        .append(ColumnEnums.resolveOption(o))
                        .append(" ");
                    }
                } catch (Exception ex) {

                }
                b
                .append("\n");
            }
        }
        return b.toString();
    }
}
