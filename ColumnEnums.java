

public class ColumnEnums {
    public enum Type {
        integer,
        charArray,
        bool,
        charUnit,
        date
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
        case bool:
            return "bit";
        case charUnit:
            return "char(1)";
        case date:
            return "datetime";
        }
        return "char(16)";
    }

    public static String resolveWrapperType(Type type) {
        switch(type) {
            case integer:
                return "Integer";
            case charArray:
                return "String";
            case bool:
                return "Boolean";
            case charUnit:
                return "Character";
            case date:
                return "Date";
            }
            return "String";
    }

    public static String resolvePrimitiveType(Type type) {
        switch(type) {
            case integer:
                return "int";
            case charArray:
                return "String";
            case bool:
                return "boolean";
            case charUnit:
                return "char";
            case date:
                return "Date";
            }
            return "String";
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