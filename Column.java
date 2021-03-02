

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