
import java.util.List;

public class Table {
  private String name;
  private String pascalName;
  private String camelName;
  private boolean isLooped;
  private boolean isJoined;
  private List<Column> columns;

  public Table(String name, boolean isLooped, boolean isJoined, List<Column> columns) {
    this.name = name;
    this.isLooped = isLooped;
    this.isJoined = isJoined;
    this.columns = columns;
  }

  public String getName() {
    return name;
  }

  public boolean getIsLooped() {
    return isLooped;
  }

  public boolean getIsJoined() {
    return isJoined;
  }

  public List<Column> getColumns() {
    return columns;
  }
}
