
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.stream.Collectors;

public class Table {
  private String name;
  private String pascalName;
  private String camelName;
  private boolean isLooped;
  private boolean isJoined;
  private Column primaryCol;
  private List<Column> columns;
  private List<Column> uniqueColumns;
  private List<Column> nonPrimaryColumns;

  public Table(String name, boolean isLooped, boolean isJoined, List<Column> columns) {
    this.name = name;
    this.isLooped = isLooped;
    this.isJoined = isJoined;
    this.columns = columns;
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
    primaryCol = null;
    for(Column c: this.columns) {
      if(c.getIsPrimary()) {
        primaryCol = c;
        break;
      }
    }
    uniqueColumns = new ArrayList<>();
    nonPrimaryColumns = new ArrayList<>();
    for(Column c: this.columns) {
      if(c.getIsUnique()) {
        uniqueColumns.add(c);
      }
      if(!c.getIsPrimary()) {
        nonPrimaryColumns.add(c);
      }
    }
    if(primaryCol == null) {
      for(Column c: this.columns) {
        if(isLooped || isJoined) {
          if(c.getName().equals(c.getForeignKeyName())) {
            primaryCol = c;
            break;
          }
        }
      }
    }
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

  public boolean getIsLooped() {
    return isLooped;
  }

  public boolean getIsJoined() {
    return isJoined;
  }

  public int getNumColumns() {
    return columns.size();
  }

  public Column getPrimaryColumn() {
    return primaryCol;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public List<Column> getUniqueColumns() {
    return uniqueColumns;
  }

  public List<Column> getNonPrimaryColumns() {
    return nonPrimaryColumns;
  }
}
