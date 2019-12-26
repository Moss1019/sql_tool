
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
        String[] parts = name.split("_");
        if(parts.length > 0) {
            StringBuilder b = new StringBuilder();
            int index = 0;
            for(String part: parts) {
                if(index++ == 0) {
                    b.append(part);
                } else {
                    b.append(String.format("%c%s", part.toUpperCase().charAt(0), part.substring(1)));
                }
            }
            return b.toString();
        } else {
            return name;
        }
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
}

// test_id int primary auto_increment
