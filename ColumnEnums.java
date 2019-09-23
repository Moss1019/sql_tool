

public class ColumnEnums {
    public enum Type {
        integer,
        charArray,
        bit,
        charUnit
    }

    public enum Option {
        primaryKey,
        autoIncrement,
        notNull
    }

    public static String resolveType(Type type) {
        switch(type) {
        case integer:
            return "integer";
        case charArray:
            return "char(32)";
        case bit:
            return "bit";
        case charUnit:
            return "char(1)";
        }
        return "char(16)";
    }

    public static String resolveOption(Option option) {
        switch(option) {
        case primaryKey:
            return "primary key";
        case autoIncrement:
            return "auto_increment";
        case notNull:
            return "not null";
        }
        return "";
    }
}