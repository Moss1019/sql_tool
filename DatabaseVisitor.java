
import java.util.List;

import java.util.ArrayList;

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public Database visitDatabase(DefinitionParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList<>();
    for(DefinitionParser.TableContext tc: ctx.table()) {
      tables.add(visitTable(tc));
    }
    return new Database(tables);
  }

  @Override
  public Table visitTable(DefinitionParser.TableContext ctx) {
    List<Column> columns = new ArrayList<>();
    for(DefinitionParser.ColumnContext cc: ctx.column()) {
      columns.add(visitColumn(cc));
    }
    return new Table(ctx.NAME().getText(), ctx.LOOPED() != null, ctx.JOINED() != null, columns);
  }

  @Override
  public Column visitColumn(DefinitionParser.ColumnContext ctx) {
    Column.Options options = Column.createOptions();
    for(DefinitionParser.OptionContext oc: ctx.option()) {
      options.isPrimary = oc.getText().equals("primary");
      options.isAutoIncrement = oc.getText().equals("auto_increment");
      options.isUnique = oc.getText().equals("unique");
      options.isForeign = oc.getText().equals("foreign");
      if(oc.NAME() != null) {
        options.foreignKeyName = oc.NAME().getText();
      }
    }
    return new Column(ctx.NAME().getText(), ctx.DATA_TYPE().getText(), options);
  }

  
}
