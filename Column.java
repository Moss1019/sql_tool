
import java.util.List;
import java.util.ArrayList;

public class Column {
    private String name;
    private ColumnEnums.Type type;
    private List<ColumnEnums.Option> options;
    private boolean isPrimary;

    public Column() {
        options = new ArrayList<>();
        isPrimary = false;
    }

    public Column(String name, ColumnEnums.Type type) {
        this();
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { 
        return name;
    }

    public void setType(ColumnEnums.Type type) {
        this.type = type;
    }

    public ColumnEnums.Type getType() {
        return type;
    }

    public void setOptions(List<ColumnEnums.Option> options) {
        this.options = options;
        for(ColumnEnums.Option option: options) {
            if(option == ColumnEnums.Option.primaryKey) {
                isPrimary = true;
            }
        }
    }

    public List<ColumnEnums.Option> getOptions() {
        return options;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }
}

/*
create table users(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name CHAR(32),
)
*/