
import java.util.List;
import java.util.ArrayList;

public class Database {
    public List<Table> tables;

    @Override 
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(Table t: tables) {
            b.append(t.name).append("\n");
            for(Column c: t.columns) {
                b
                .append(" ")
                .append(c.name)
                .append(" ")
                .append(ColumnEnums.resolveType(c.dataType))
                .append(" ");
                for(ColumnEnums.Option o: c.options) {
                    b
                    .append(ColumnEnums.resolveOption(o)).append(" ");
                }
                b
                .append("\n");
            }
        }
        return b.toString();
    }
}
