
import java.util.*;

public class Table {
  private String name;
  private List<Column> columns;
  private boolean joined;
  private boolean looped;
  private Column primaryColumn;
  private Column secondaryColumn;
  private List<Column> uniqueColumns;
  private List<Column> nonPrimaryColumns;
  private List<Table> parentTables;
  private List<Table> childTables;

  public Table(String name, boolean joined, boolean looped, List<Column> columns) {
    this.name = name;
    this.joined = joined;
    this.looped = looped;
    this.columns = columns;
    setupColumns();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(name);
      b.append(looped ? " looped" : joined ? " joined" : " =>");
    for(Column col: columns) {
      b.append("\n");
      b.append(col.toString());
    }
    return b.toString();
  }

  public String getName() {
    return name;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public boolean isJoined() {
    return joined;
  }

  public boolean isLooped() {
    return looped;
  }

  public Column getPrimaryColumn() {
    return primaryColumn;
  }

  public Column getSecondaryColumn() {
    if(secondaryColumn == null) {
      return primaryColumn;
    }
    return secondaryColumn;
  }

  public List<Column> getUniqueColumns() {
    return uniqueColumns;
  }

  public List<Column> getNonPrimaryColumns() {
    return nonPrimaryColumns;
  }

  public List<Table> getParentTables() {
    return parentTables;
  }

  public List<Table> getChildTables() {
    return childTables;
  }

  private void setupColumns() {
    this.uniqueColumns = new ArrayList<>();
    this.nonPrimaryColumns = new ArrayList<>();
    this.parentTables = new ArrayList<>();
    this.childTables = new ArrayList<>();
    for(Column col: columns) {
      col.setOwnerTable(this);
      if(col.isPrimary()) {
        this.primaryColumn = col;
      } else if (col.isSecondary()) {
        this.secondaryColumn = col;
      } else {
        nonPrimaryColumns.add(col);
      }
      if(col.isUnique()) {
        uniqueColumns.add(col);
      }
      if(col.isForeign()) {
        parentTables.add(col.getOwnerTable());
        col.getOwnerTable().childTables.add(this);
      }
    }
  }

}
