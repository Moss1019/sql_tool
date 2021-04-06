
public class DataTypeUtil {
  private static String intStr = "int";
  private static String stringStr = "string";
  private static String booleanStr = "boolean";
  private static String charStr = "char";
  private static String dateStr = "date";
  private static String guidStr = "guid";

  private static String extractGuidTmpl = "UUID.fromString(values.get(\"%s\").toString())";
  private static String extractStrTmpl = "values.get(\"%s\").toString()";
  private static String extractIntTmpl = "Integer.parseInt(values.get(\"%s\").toString())";

  private static String insertGuidTmpl = "%s.toString()";
  private static String insertStrTmpl = "%s";
  private static String insertIntTmpl = "%s";

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
    } else if (datatype.equals(guidStr)) {
      return "char(36)";
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
    } else if (datatype.equals(guidStr)) {
      return "UUID";
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
    } else if(datatype.equals(guidStr)) {
      return "String";
    }
    return "Integer";
  }

  public static String resolveSetParam(String name, Column c, boolean isGetter) {
    String temp = isGetter ? "get" + name + "()" : name;
    if(c.getDataType().equals(guidStr)) {
      return String.format("%s.toString()", temp);
    }
    return temp;
  }

  public static String resolveTypeScriptType(String datatype) {
    if(datatype.equals(intStr)) {
      return "number";
    } else if(datatype.equals(stringStr)) {
      return "string";
    } else if(datatype.equals(booleanStr)) {
      return "boolean";
    } else if(datatype.equals(charStr)) {
      return "string";
    } else if(datatype.equals(dateStr)) {
      return "Date";
    } else if(datatype.equals(guidStr)) {
      return "string";
    }
    return "number";
  }

  public static String resolveSqlInsertSelectPK(Column col) {
    if(col.getDataType().equals(guidStr)) {
      return col.getName();
    }
    return "last_insert_id()";
  }

  public static String resolvePrimaryDefault(String datatype) {
    if(datatype.equals(guidStr)) {
      return "UUID.randomUUID()";
    }
    return "-1";
  }

  public static String getObjPrimaryKey(Table t) {
    if(t.getIsLooped() || t.getIsJoined()) {
      return "";
    }
    return String.format("\n\t\t%s.set%s(%s);",
      t.getCamelName(),
      t.getPrimaryColumn().getPascalName(),
      resolvePrimaryDefault(t.getPrimaryColumn().getDataType()));
  }

  public static String getMapPrimaryKey(Table t) {
    if(t.getIsLooped() || t.getIsJoined()) {
      return "UUID.randomUUID()";
    }
    return String.format("%s.getPrimary()", t.getCamelName());
  }

  public static String getExtractLine(Column c) {
    String d = c.getDataType();
    String template;
    if(d.equals(guidStr)) {
      template = extractGuidTmpl;
    } else if (d.equals(intStr)) {
      template = extractIntTmpl;
    } else {
      template = extractStrTmpl;
    }
    return String.format(template, c.getCamelName());
  }

  public static String getInsertLine(Column c) {
    String d = c.getDataType();
    String template;
    if(d.equals(guidStr)) {
      template = insertGuidTmpl;
    } else if (d.equals(intStr)) {
      template = insertIntTmpl;
    } else {
      template = insertStrTmpl;
    }
    return String.format(template, c.getCamelName());
  }
}