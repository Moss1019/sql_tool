
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

  public void setOwnerTable(Table ownerTable) {
    this.ownerTable = ownerTable;
  }
}
