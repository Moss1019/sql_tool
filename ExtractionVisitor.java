
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;


public class ExtractionVisitor extends DefinitionBaseVisitor<Object> {
    @Override
    public Object visitDefinition(DefinitionParser.DefinitionContext ctx) {
        Table table;
        List<Column> columns = new ArrayList<>();
        table = new Table(ctx.NAME().getText());
        for(DefinitionParser.ColumnDefContext chCtx: ctx.columnDef()) {
            columns.add((Column)visit(chCtx));
        }
        table.setColumns(columns);
        return table;
    }

    @Override
    public Object visitColumnDef(DefinitionParser.ColumnDefContext ctx) {
        String typeStr = ctx.TYPE().getText();
        Column column = new Column();
        if(typeStr.equals("int")) {
            column.setType(ColumnEnums.Type.integer);   
        } else if (typeStr.equals("string")) {
            column.setType(ColumnEnums.Type.charArray);
        } else if (typeStr.equals("char")) {
            column.setType(ColumnEnums.Type.charUnit);
        }
        column.setName(ctx.NAME().getText());
        column.setOptions((List<ColumnEnums.Option>)visit(ctx.options()));
        return column;
    }

    @Override 
    public Object visitOptions(DefinitionParser.OptionsContext ctx) {
        List<ColumnEnums.Option> options = new ArrayList<>();
        for(DefinitionParser.OptionContext chCtx: ctx.option()) {
            String optionStr = (String)visit(chCtx);
            if(optionStr.equals("primary_key")) {
                options.add(ColumnEnums.Option.primaryKey);
            } else if (optionStr.equals("auto_increment")) {
                options.add(ColumnEnums.Option.autoIncrement);
            }
        }
        return options;
    }

    @Override
    public Object visitOption(DefinitionParser.OptionContext ctx) {
        return ctx.NAME().getText();
    }
}
