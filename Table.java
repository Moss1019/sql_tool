
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import java.util.stream.Collectors;

public class Table {
  private String name;
  private String pascalName;
  private String camelName;
  private boolean isLooped;
  private boolean isJoined;
  private Column primaryCol;
  private List<Table> childTables;
  private List<Table> parentTables;
  private List<Column> columns;
  private List<Column> uniqueColumns;
  private List<Column> nonPrimaryColumns;

  private static Map<String, Table> tableRegistry = new HashMap<>();

  public Table(String name, boolean isLooped, boolean isJoined, List<Column> columns) {
    tableRegistry.put(name, this);
    this.name = name;
    this.isLooped = isLooped;
    this.isJoined = isJoined;
    this.columns = columns;
    setupNames(name);
    setupPrimaryKey();
    setupRelationships();
  }

  private void setupRelationships() {
    childTables = new ArrayList<>();
    parentTables = new ArrayList<>();
    uniqueColumns = new ArrayList<>();
    nonPrimaryColumns = new ArrayList<>();
    for(Column c: this.columns) {
      if(c.getIsUnique()) {
        uniqueColumns.add(c);
      }
      if(!c.getIsPrimary()) {
        nonPrimaryColumns.add(c);
      }
      if(c.getIsForeign()) {
        Table parentTable = tableRegistry.get(c.getForeignKeyTable());
        if(!parentTables.contains(parentTable)) {
          parentTables.add(parentTable);
          parentTable.childTables.add(this);
        }
      }
    }
  }

  private void setupPrimaryKey() {
    primaryCol = null;
    for(Column c: this.columns) {
      if(c.getIsPrimary()) {
        primaryCol = c;
        break;
      }
    }
    if(primaryCol == null) {
      if(isLooped || isJoined) {
        for(Column c: this.columns) {
          if(c.getName().equals(c.getForeignKeyName())) {
            primaryCol = c;
            break;
          }
        }
      }
    }
  }

  private void setupNames(String name) {
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
    if(isLooped || isJoined) {
      return columns.get(0);
    } else {
      return primaryCol;
    }
  }

  public Column getJoinedColumn() {
    if(isLooped || isJoined) {
      return columns.get(1);
    } else {
      return primaryCol;
    }
  }

  public List<Table> getParentTables() {
    return parentTables;
  }

  public List<Table> getChildTables() {
    return childTables;
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
