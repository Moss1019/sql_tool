
import java.util.List;

import java.util.ArrayList;

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public List<Table> visitDatabase(DefinitionParser.DatabaseContext ctx) {
    List<Table> tables = new ArrayList<>();
    for(DefinitionParser.TableContext tc: ctx.table()) {
      tables.add(visitTable(tc));
    }
    return tables;
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
      options.setIsPrimary(oc.getText().equals("primary"));
      options.setIsAutoIncrement(oc.getText().equals("auto_increment"));
      options.setIsUnique(oc.getText().equals("unique"));
      options.setIsForeign(oc.getText().contains("foreign"));
      if(oc.NAME() != null) {
      options.foreignKeyName = oc.NAME().getText();
      }
    }
    return new Column(ctx.NAME().getText(), ctx.DATA_TYPE().getText(), options);
  }  
}
