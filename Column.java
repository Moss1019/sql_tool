
import java.util.Arrays;

import java.util.stream.Collectors;

public class Column {
  private String name;
  private String pascalName;
  private String camelName;
  private String foreignKeyName;
  private String dataType;
  private boolean isPrimary;
  private boolean isAutoIncrement;
  private boolean isUnique;
  private boolean isForeign;

  public Column(String name, String dataType, Options options) {
    this.name = name;
    this.dataType = dataType;
    foreignKeyName = options.foreignKeyName;
    isPrimary = options.isPrimary;
    isAutoIncrement = options.isAutoIncrement;
    isUnique = options.isUnique;
    isForeign = options.isForeign;
    pascalName = String.join("", Arrays.asList(name.split("_"))
      .stream()
      .map(p -> {
        return String.format("%c%s", Character.toUpperCase(p.charAt(0)), p.substring(1));
      })
      .collect(Collectors.toList()));
    camelName = String.join("", Arrays.asList(name.split("_"))
      .stream()
      .map(p -> {
        return String.format("%c%s", Character.toUpperCase(p.charAt(0)), p.substring(1));
      })
      .collect(Collectors.toList()));
    camelName = String.format("%c%s", Character.toLowerCase(camelName.charAt(0)), camelName.substring(1));
  }

  public String getName() {
    return name;
  }

  public String getPascalName() {
    return pascalName;
  }

  public String getCamelName() {
    return camelName;
  }

  public String getForeignKeyName() {
    return foreignKeyName;
  }

  public boolean getIsPrimary() {
    return isPrimary;
  }

  public boolean getIsAutoIncrement() {
    return isAutoIncrement;
  }

  public boolean getIsUnique() {
    return isUnique;
  }

  public boolean getIsForeign() {
    return isForeign;
  }

  public static Options createOptions() {
    return new Options();
  }

  public static class Options {
    public boolean isPrimary;
    public boolean isAutoIncrement;
    public boolean isUnique;
    public boolean isForeign;
    public String foreignKeyName;
  }
}