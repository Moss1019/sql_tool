
import java.util.Arrays;

import java.util.stream.Collectors;

public class Column {
  private String name;
  private String pascalName;
  private String camelName;
  private String lowerName;
  private String foreignKeyName;
  private String foreignTableName; 
  private String dataType;
  private boolean isPrimary;
  private boolean isAutoIncrement;
  private boolean isUnique;
  private boolean isForeign;

  public Column(String name, String dataType, Options options) {
    this.name = name;
    this.dataType = dataType;
    foreignKeyName = options.foreignKeyName != null ? options.foreignKeyName : name;
    isPrimary = options.isPrimary;
    isAutoIncrement = options.isAutoIncrement;
    isUnique = options.isUnique;
    isForeign = options.isForeign;
    setupNames();
    if(isForeign) {
      foreignTableName = foreignKeyName.split("_")[0];
    }
  }

  private void setupNames() {
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
    lowerName = name.replace("_", "");
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

  public String getLowerName() {
    return lowerName;
  }

  public String getForeignKeyName() {
    return foreignKeyName;
  }

  public String getForeignKeyTable() {
    return foreignTableName;
  }

  public String getDataType() {
    return dataType;
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
    public boolean isPrimary = false;
    public boolean isAutoIncrement = false;
    public boolean isUnique = false;
    public boolean isForeign = false;
    public String foreignKeyName;

    public void setIsPrimary(boolean value) {
      if(!isPrimary && value) {
        isPrimary = value;
      }
    }

    public void setIsAutoIncrement(boolean value) {
      if(!isAutoIncrement && value) {
        isAutoIncrement = value;
      }
    }

    public void setIsUnique(boolean value) {
      if(!isUnique && value) {
        isUnique = value;
      }
    }

    public void setIsForeign(boolean value) {
      if(!isForeign && value) {
        isForeign = value;
      }
    }
  }
}