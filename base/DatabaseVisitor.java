
import java.util.*;


public class DatabaseVisitor extends GeneratorBaseVisitor<Object> {
  @Override
  public List<Table> visitDatabase(GeneratorParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList<>();
    for(GeneratorParser.TableContext tCtx: ctx.table()) {
      tables.add(visitTable(tCtx));
    }
    return tables;
  }
  
  public Table visitTable(GeneratorParser.TableContext ctx) {
    String name = ctx.NAME().getText();
    boolean joined = ctx.JOINED() != null;
    boolean looped = ctx.LOOPED() != null;
    List<Column> columns = new ArrayList<>();
    for(GeneratorParser.ColumnContext cCtx: ctx.column()) {
      columns.add(visitColumn(cCtx));
    }
    return new Table(name, joined, looped, columns);
  }

  public Column visitColumn(GeneratorParser.ColumnContext ctx) {
    String name = ctx.NAME().getText();
    String dataType = ctx.DATA_TYPE().getText();
    boolean primary = false;
    boolean secondary = false;
    boolean autoIncrement = false;
    boolean unique = false;
    boolean foreign = false;
    String foreignTableName = null;
    for(GeneratorParser.OptionContext oCtx: ctx.option()) {
      if(oCtx.getText().equals("primary")) {
        primary = true;
      }
      if(oCtx.getText().equals("secondary")) {
        secondary = true;
      }
      if(oCtx.getText().equals("auto_increment")) {
        autoIncrement = true;
      }
      if(oCtx.getText().equals("unique")) {
        unique = true;
      }
      if(oCtx.getText().contains("foreign")) {
        foreign = true;
        foreignTableName = oCtx.NAME().getText();
      }
    }
    return new Column(name, dataType, foreignTableName, primary, secondary, autoIncrement, foreign, unique);
  }
}