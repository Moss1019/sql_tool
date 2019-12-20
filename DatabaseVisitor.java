
import java.util.List;
import java.util.ArrayList;

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public Object visitDatabase(DefinitionParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList();
    for(DefinitionParser.TableContext tblCtx : ctx.table()) {
      tables.add((Table)visit(tblCtx));
    }
    Database d = new Database(tables);
    return d;
  }

  @Override
  public Object visitTable(DefinitionParser.TableContext ctx) {
    List<Column> columns = new ArrayList();
    String name = ctx.NAME().getText();
    boolean hasJoin = false;
    for(DefinitionParser.RowContext rwCtx: ctx.row()) {
      Column c = (Column)visit(rwCtx);
      if(c != null) {
        columns.add(c);
      } else {
        hasJoin = true;
      }
    }
    Table t = new Table(name, columns, hasJoin);
    return t;
  }

  @Override
  public Object visitRow(DefinitionParser.RowContext ctx) {
    String colName = ctx.NAME().getText();
    try {
      String dataType = ctx.DATA_TYPE().getText();
      List<ColumnEnums.Option> options = new ArrayList();
      for(Object o: ctx.OPTION()) {
        options.add(ColumnEnums.resolveOption(o.toString()));
      }
      return new Column(colName, ColumnEnums.resolveType(dataType), options);
    } catch (NullPointerException ex) {
      return null;
    }
  }
}
