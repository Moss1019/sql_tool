
import java.util.*;

public class SqlGenerator extends Generator {
  private String createTableTmpl;
  private String columnTmpl;
  private String foreignTmpl;

  public SqlGenerator(Database db) {
    super(db, "../templates/dotnet/sql");
  }

  public Map<String, String> generate() {
    Map<String, String> files = new HashMap<>();
    files.put(db.getRootName() + ".sql", generateSql());
    return files;
  }
  
  private String generateSql() {
    StringBuilder b = new StringBuilder();
    b
    .append("use ")
    .append(db.getRootName())
    .append("_db\n\ngo\n\n");
    for(Table t: db.getTables()) {
      b
      .append(createTableTmpl
        .replace("{tablename}", t.getPascalName())
        .replace("{columns}", generateColumns(t))
        .replace("{constraints}", generateConstraints(t)))
      .append("\n\n");
    }
    return b.toString();
  }

  private String generateColumns(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Column c: t.getColumns()) {
      b
      .append("\n")
      .append(columnTmpl
        .replace("{columncamel}", c.getCamelName())
        .replace("{options}", generateOptions(c))
        .replace("{datatype}", DataTypeUtil.resolveSqlType(c))
        .replace("{default}", DataTypeUtil.resovleSqlDefault(c)));
      if(++i < t.getColumns().size()) {
        b
        .append(",");
      }
    }
    return b.toString();
  }

  private String generateOptions(Column c) {
    StringBuilder b = new StringBuilder();
    if(c.isPrimary()) {
      b.append(" primary key ");
    }
    if(c.isUnique()) {
      b.append(" unique not null ");
    }
    return b.toString();
  }

  private String generateConstraints(Table t) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for(Table pt: t.getParentTables()) {
      String foreignColumn = "";
      String tableName = "";
      for(Column c: t.getColumns()) {
        if(!c.isForeign()) {
          continue;
        }
        if (c.getForeignTableName().equals(pt.getName())) {
          if(t.isLooped()) {
            if(i++ == 0) {
              foreignColumn = c.getCamelName();
              tableName = t.getName();
            } else {
              foreignColumn = t.getSecondaryColumn().getCamelName();
              tableName = t.getLoopedJoinedName();
            }
            break;
          } else {
            foreignColumn = c.getCamelName();
            tableName = t.getName();
            break;
          }
        }
      }
      b
      .append(",\n")
      .append(foreignTmpl
        .replace("{parentname}", pt.getName())
        .replace("{tablename}", tableName)
        .replace("{parentprimarycamel}", pt.getPrimaryColumn().getCamelName())
        .replace("{foreigncamel}", foreignColumn));
    }
    return b.toString();
  }

  @Override
  protected void loadTemplates() {
    createTableTmpl = loadTemplate("createtable");
    columnTmpl = loadTemplate("column");
    foreignTmpl = loadTemplate("foreign");
  }
}