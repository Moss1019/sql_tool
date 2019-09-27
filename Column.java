
import java.util.List;
import java.util.ArrayList;

public class Column {
    private String name;
    private String fieldName;
    private String fieldNameCaps;
    private ColumnEnums.Type type;
    private List<ColumnEnums.Option> options;
    private boolean isPrimary;

    public Column() {
        options = new ArrayList<>();
        isPrimary = false;
    }

    public Column(String name, ColumnEnums.Type type) {
        this();
        setName(name);
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
        String[] words = name.split("_");
        StringBuilder fieldNameBuilder = new StringBuilder(words[0]);
        for(int i = 1; i < words.length; ++i) {
            fieldNameBuilder.append(String.format("%c%s", words[i].toUpperCase().charAt(0), words[i].substring(1)));
        }
        fieldName = fieldNameBuilder.toString();
        fieldNameCaps = String.format("%c%s", fieldName.toUpperCase().charAt(0), fieldName.substring(1));
    }

    public String getName() { 
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldNameCaps() {
        return fieldNameCaps;
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