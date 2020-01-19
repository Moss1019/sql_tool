
import java.util.List;

import java.util.ArrayList;

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public Object visitDatabase(DefinitionParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList();
    for(DefinitionParser.TableContext tblCtx : ctx.table()) {
      tables.add((Table)visit(tblCtx));
    }
    return tables;
  }

  @Override
  public Object visitTable(DefinitionParser.TableContext ctx) {
    List<Column> columns = new ArrayList();
    String name = ctx.NAME().getText();
    boolean hasJoin = false;
    for(DefinitionParser.RowContext rwCtx: ctx.row()) {
      Object o = visit(rwCtx);
      Column c = null;
      try {
        c = (Column)o;
      } catch (ClassCastException ex) {
        try {
          hasJoin = (Boolean)o;
        } catch (ClassCastException ex_1) {
          c = null;
        }
      }
      if(c != null) {
        columns.add(c);
      }
    }
    Table t = new Table(name, columns, hasJoin);
    return t;
  }

  @Override
  public Object visitRow(DefinitionParser.RowContext ctx) {
    String colName = ctx.NAME().getText();
    try {
      if(ctx.JOINED() != null) {
        return true;
      }
      String dataType = ctx.DATA_TYPE().getText();
      String psudoName = null;
      List<ColumnEnums.Option> options = new ArrayList<>();
      for(DefinitionParser.OptionContext o: ctx.option()) {
        options.add(ColumnEnums.resolveOption(o.getText()));
        if(o.NAME() != null) {
          psudoName = o.NAME().getText();
        }
      }
      return new Column(colName, ColumnEnums.resolveType(dataType), options, psudoName);
    } catch (NullPointerException ex) {
      return null;
    }
  }
}
