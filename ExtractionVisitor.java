
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;


public class ExtractionVisitor {
    // private Column currentColumn = null;

    // @Override
    // public Object visitDefinition(DefinitionParser.DefinitionContext ctx) {
    //     Table table;
    //     List<Column> columns = new ArrayList<>();
    //     table = new Table(ctx.NAME().getText());
    //     for(DefinitionParser.ColumnDefContext chCtx: ctx.columnDef()) {
    //         columns.add((Column)visit(chCtx));
    //     }
    //     table.setColumns(columns);
    //     return table;
    // }

    // @Override
    // public Object visitColumnDef(DefinitionParser.ColumnDefContext ctx) {
    //     String typeStr = ctx.TYPE().getText();
    //     Column column = new Column();
    //     currentColumn = column;
    //     if(typeStr.equals("int")) {
    //         column.setType(ColumnEnums.Type.integer);   
    //     } else if (typeStr.equals("string")) {
    //         column.setType(ColumnEnums.Type.charArray);
    //     } else if (typeStr.equals("char")) {
    //         column.setType(ColumnEnums.Type.charUnit);
    //     } else if (typeStr.equals("bool")) {
    //         column.setType(ColumnEnums.Type.bool);
    //     } else if (typeStr.equals("date")) {
    //         column.setType(ColumnEnums.Type.date);
    //     }
    //     column.setName(ctx.NAME().getText());
    //     column.setOptions((List<ColumnEnums.Option>)visit(ctx.options()));
    //     return column;
    // }

    // @Override 
    // public Object visitOptions(DefinitionParser.OptionsContext ctx) {
    //     List<ColumnEnums.Option> options = new ArrayList<>();
    //     for(DefinitionParser.OptionContext chCtx: ctx.option()) {
    //         String optionStr = (String)visit(chCtx);
    //         if(optionStr.equals("primary_key")) {
    //             options.add(ColumnEnums.Option.primaryKey);
    //         } else if (optionStr.equals("auto_increment")) {
    //             options.add(ColumnEnums.Option.autoIncrement);
    //         } else if (optionStr.equals("not_null")) {
    //             options.add(ColumnEnums.Option.notNull); 
    //         } else if (optionStr.equals("foreign_key")) {
    //             options.add(ColumnEnums.Option.foreignKey);
    //         } else {
    //             currentColumn.setReferencedTable(optionStr);
    //         }
    //     }
    //     return options;
    // }

    // @Override
    // public Object visitOption(DefinitionParser.OptionContext ctx) {
    //     return ctx.NAME().getText();
    // }
}
