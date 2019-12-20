

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
        unique,
        foreignKey
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
        case unique:
            return "unique";
        case foreignKey:
            return "references";
        }
        return "";
    }

    static public ColumnEnums.Type resolveType(String typeStr) {
        if(typeStr.equals("int")) {
            return ColumnEnums.Type.integer;
        } else if(typeStr.equals("boolean")) {
            return ColumnEnums.Type.bool;
        } else if(typeStr.equals("string")) {
            return ColumnEnums.Type.charArray;
        } else if(typeStr.equals("char")) {
            return ColumnEnums.Type.charUnit;
        } else {
            return ColumnEnums.Type.integer;
        } 
    }

    static public ColumnEnums.Option resolveOption(String optionStr) {
        if(optionStr.equals("primary")) {
            return ColumnEnums.Option.primaryKey;
        } else if(optionStr.equals("auto_increment")) {
            return ColumnEnums.Option.autoIncrement;
        } else if(optionStr.equals("unique")) {
            return ColumnEnums.Option.unique;
        } else if(optionStr.equals("foreign")) {
            return ColumnEnums.Option.foreignKey;
        } else {
            return ColumnEnums.Option.foreignKey;
        }
        
    }
}

//foreign