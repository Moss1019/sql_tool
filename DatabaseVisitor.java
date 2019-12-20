

public class DatabaseVisitor extends DefinitionBaseVisitor<Object> {
  @Override
  public Object visitDatabase(DefinitionParser.DatabaseContext ctx) {
    System.out.println(ctx.toString());
    return new Table();
  }
}
