
import java.util.List;
import java.util.ArrayList;

public class Column {
    private String name;
    private ColumnEnums.Type dataType;
    private List<ColumnEnums.Option> options;
    private boolean isPrimary;

    public Column(String name, ColumnEnums.Type dataType, List<ColumnEnums.Option> options) {
        this.name = name;
        this.dataType = dataType;
        this.options = options;
        isPrimary = false;
        for(ColumnEnums.Option option: options) {
            if(option == ColumnEnums.Option.primaryKey) {
                isPrimary = true;
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public ColumnEnums.Type getDataType() {
        return dataType;
    } 

    public List<ColumnEnums.Option> getOptions() {
        return options;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getPascalName() {
        return String.format("%c%s", name.toUpperCase().charAt(0), name.substring(1));
    }
}

// test_id int primary auto_increment
