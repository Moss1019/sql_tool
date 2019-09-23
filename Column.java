
import java.util.List;
import java.util.ArrayList;

public class Column {
    private String name;
    private ColumnEnums.Type type;
    private List<ColumnEnums.Option> options;

    public Column() {
        options = new ArrayList<>();
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
    }

    public List<ColumnEnums.Option> getOptions() {
        return options;
    }

    public String toSQLString() {
        StringBuilder b = new StringBuilder();
        b.append(name).append(" ");
        switch(type) {
        case integer:
            b.append("INTEGER ");
            break;
        case charArray:
            b.append("CHAR(32) ");
            break;
        case charUnit:
            b.append("CHAR(1) ");
            break;
        default:
            break;
        }
        for(ColumnEnums.Option option: options) {
            switch(option) {
            case primaryKey:
                b.append("PRIMARY KEY ");
                break;
            case autoIncrement:
                b.append("AUTO_INCREMENT ");
                break;

            }
        }
        return b.toString();
    }
}

/*
create table users(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name CHAR(32),
)
*/