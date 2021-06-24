
public class DataTypeUtil {
  private static String dInt = "int";
  private static String dString = "string";
  private static String dDate = "date";
  private static String dGuid = "guid";

  public static String resolvePrimitive(Column col) {
    String d = col.getDataType();
    if(d.equals(dInt)) {
      return "int";
    }
    if(d.equals(dString)) {
      return "string";
    }
    if(d.equals(dDate)) {
      return "DateTime";
    }
    if(d.equals(dGuid)) {
      return "Guid";
    }
    return "UNKNOWN " + d;
  }

  public static String resolveDefault(Column col) {
    String d = col.getDataType();
    if(d.equals(dInt)) {
      return "0";
    }
    if(d.equals(dString)) {
      return "string.Empty";
    }
    if(d.equals(dDate)) {
      return "new DateTime()";
    }
    if(d.equals(dGuid)) {
      return "Guid.Empty";
    }
    return "null!";
  }
}
