
import java.util.List;
import java.util.ArrayList;

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public Object visitDatabase(DefinitionParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList();
    for(DefinitionParser.TableContext tblCtx : ctx.table()) {
      tables.add((Table)visit(tblCtx));
    }
    Database d = new Database();
    d.tables = tables;
    System.out.println(d);
    return d;
  }

  @Override
  public Object visitTable(DefinitionParser.TableContext ctx) {
    List<Column> columns = new ArrayList();
    String name = ctx.NAME().getText();
    for(DefinitionParser.RowContext rwCtx: ctx.row()) {
      columns.add((Column)visit(rwCtx));
    }
    Table t = new Table();
    t.name = name;
    t.columns = columns;
    return t;
  }

  @Override
  public Object visitRow(DefinitionParser.RowContext ctx) {
    String colName = ctx.NAME().getText();
    String dataType = ctx.DATA_TYPE().getText();
    Column c = new Column();
    c.name = colName;
    c.dataType = ColumnEnums.resolveType(dataType);
    List<ColumnEnums.Option> options = new ArrayList();
    for(Object o: ctx.OPTION()) {
      options.add(ColumnEnums.resolveOption(o.toString()));
    }
    c.options = options;
    return c;
  }
}
