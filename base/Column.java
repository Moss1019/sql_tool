
public class Column {
  private String name;
  private String dataType;
  private String foreignTableName;
  private boolean primary;
  private boolean secondary;
  private boolean autoIncrement;
  private boolean foreign;
  private boolean unique;
  private Table ownerTable;
  private Table foreignTable;
  private String pascalName;
  private String camelName;

  public Column(String name,
    String dataType,
    String foreignTableName,
    boolean primary, 
    boolean secondary,
    boolean autoIncrement,
    boolean foreign,
    boolean unique) {
      this.name = name;
      this.dataType = dataType;
      this.foreignTableName = foreignTableName;
      this.primary = primary;
      this.secondary = secondary;
      this.autoIncrement = autoIncrement;
      this.foreign = foreign;
      this.unique = unique;
      setupNames();
  }

  @Override
  public String toString() {
    return String.format("%s: %s |p %s s %s ai %s f %s u %s| => %s", 
      name, 
      dataType,
      primary,
      secondary, 
      autoIncrement,
      foreign,
      unique,
      foreignTableName);
  }

  public String getName() {
    return name;
  }

  public String getDataType() {
    return dataType;
  }

  public String getForeignTableName() {
    return foreignTableName;
  }

  public boolean isPrimary() {
    return primary;
  }

  public boolean isSecondary() {
    return secondary;
  }

  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public boolean isForeign() {
    return foreign;
  }

  public boolean isUnique() {
    return unique;
  }

  public Table getOwnerTable() {
    return ownerTable;
  }

  public Table getForeignTable() {
    return foreignTable;
  }

  public String getPascalName() {
    return pascalName;
  }

  public String getCamelName() {
    return camelName;
  }

  public String getLowerName() {
    return pascalName.toLowerCase();
  }

  public void setOwnerTable(Table ownerTable) {
    this.ownerTable = ownerTable;
  }

  public void setForeignTable(Table foreignTable) {
    this.foreignTable = foreignTable;
  }

  private void setupNames() {
    String[] parts = name.split("_");
    StringBuilder c = new StringBuilder();
    StringBuilder p = new StringBuilder();
    for(int i = 0; i < parts.length; ++i) {
      String part = parts[i];
      p.append(part.toUpperCase().charAt(0));
      p.append(part.substring(1).toLowerCase());
      if(i == 0) {
        c.append(part.toLowerCase().charAt(0));
      } else {
        c.append(part.toUpperCase().charAt(0));
      }
      c.append(part.substring(1).toLowerCase());
    }
    pascalName = p.toString();
    camelName = c.toString();
  } 
}
