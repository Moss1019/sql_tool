
public class DataTypeUtil {
  private static String intStr = "int";
  private static String stringStr = "string";
  private static String booleanStr = "boolean";
  private static String charStr = "char";
  private static String dateStr = "date";

  public static String resolveSqlType(String datatype) {
    if(datatype.equals(intStr)) {
      return "integer";
    } else if(datatype.equals(stringStr)) {
      return "char(64)";
    } else if (datatype.equals(booleanStr)) {
      return "bit";
    } else if (datatype.equals(charStr)) {
      return "char(1)";
    } else if (datatype.equals(dateStr)) {
      return "datetime";
    }
    return "integer";
  }

  public static String resolvePrimitiveType(String datatype) {
    if(datatype.equals(intStr)) {
      return "int";
    } else if(datatype.equals(stringStr)) {
      return "String";
    } else if (datatype.equals(booleanStr)) {
      return "boolean";
    } else if (datatype.equals(charStr)) {
      return "char";
    } else if (datatype.equals(dateStr)) {
      return "Date";
    }
    return "int";
  }

  public static String resolveWrapperType(String datatype) {
    if(datatype.equals(intStr)) {
      return "Integer";
    } else if(datatype.equals(stringStr)) {
      return "String";
    } else if (datatype.equals(booleanStr)) {
      return "Boolean";
    } else if (datatype.equals(charStr)) {
      return "Character";
    } else if (datatype.equals(dateStr)) {
      return "Date";
    }
    return "Integer";
  }
}