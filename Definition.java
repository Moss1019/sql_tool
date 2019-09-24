
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.InputStream;
import java.io.FileInputStream;

public class Definition {
    public static void main(String[] args) {
        InputStream iStream = null;
        try {
            iStream = new FileInputStream("../test.txt");
            ANTLRInputStream antlrIStream = new ANTLRInputStream(iStream);
            DefinitionLexer lexer = new DefinitionLexer(antlrIStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            DefinitionParser parser = new DefinitionParser(tokens);
            ParseTree tree = parser.definition();
            ExtractionVisitor visitor = new ExtractionVisitor();
            Table table = (Table)visitor.visit(tree);
            SqlGenerator gen = new SqlGenerator();
            System.out.println(gen.generateCreateTable(table));
            System.out.println(gen.generateSelectAllProc(table));
            System.out.println(gen.generateSelectByPKProc(table));
            System.out.println(gen.generateInsertProc(table));
            System.out.println(gen.generateUpdateProc(table));
            System.out.println(gen.generateDeleteProc(table));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                iStream.close();
            } catch(Exception ex) {}
        }
    }
}