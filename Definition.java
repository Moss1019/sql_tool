
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

public class Definition {
    public static void writeFile(String fileName, String fileContent) {
        String filePath = String.format("../output/%s", fileName);
        FileOutputStream fOut = null;
        BufferedOutputStream bufOStream = null;
        try {
            fOut = new FileOutputStream(filePath);
            bufOStream = new BufferedOutputStream(fOut);
            bufOStream.write(fileContent.getBytes());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                bufOStream.close();
            } catch (Exception ex) {}
            try {
                fOut.close();
            } catch (Exception ex) {}
        }
    }

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
            writeFile(String.format("%s_create_table.sql", table.getName()), gen.generateCreateTable(table));
            writeFile(String.format("%s_select_app_proc.sql", table.getName()), gen.generateSelectAllProc(table));
            writeFile(String.format("%s_select_one_proc.sql", table.getName()), gen.generateSelectByPKProc(table));
            writeFile(String.format("%s_insert_proc.sql", table.getName()), gen.generateInsertProc(table));
            writeFile(String.format("%s_update_proc.sql", table.getName()), gen.generateUpdateProc(table));
            writeFile(String.format("%s_delete_proc.sql", table.getName()), gen.generateDeleteProc(table));
            BeanGenerator ben = new BeanGenerator(table);
            writeFile(String.format("%s.java", table.getName()), ben.generateEntity());
            writeFile(String.format("%sService.java", table.getName()), ben.generateService());
            writeFile(String.format("%sRepository.java", table.getName()), ben.generateRepo());
            writeFile(String.format("%sContoller.java", table.getName()), ben.generateController());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                iStream.close();
            } catch(Exception ex) {}
        }
    }
}