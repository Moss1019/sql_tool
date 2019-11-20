
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.HashMap;
import java.util.Map;
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

    private static Map<String, String> processArgs(String[] args) {
        Map<String, String> argMapping = new HashMap<>();
        for(int i = 0; i < args.length; i += 2) {
            if(args[i].equals("-f")) {
                argMapping.put("file_name", args[i + 1]);
            } else if(args[i].equals("-p")) {
                argMapping.put("package_name", args[i + 1]);
            } else if(args[i].equals("-u")) {
                argMapping.put("db_user", args[i + 1]);
            }
        }
        return argMapping;
    }

    public static void main(String[] args) {
        InputStream iStream = null;
        Map<String, String> argMapping = processArgs(args);
        try {
            iStream = new FileInputStream(String.format("../%s", argMapping.get("file_name")));
            ANTLRInputStream antlrIStream = new ANTLRInputStream(iStream);
            DefinitionLexer lexer = new DefinitionLexer(antlrIStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            DefinitionParser parser = new DefinitionParser(tokens);
            ParseTree tree = parser.definition();
            ExtractionVisitor visitor = new ExtractionVisitor();
            Table table = (Table)visitor.visit(tree);
            SqlGenerator gen = new SqlGenerator();
            writeFile(String.format("%s_create_table.sql", table.getName()), gen.generateCreateTable(table));
            writeFile(String.format("%s_select_all_proc.sql", table.getName()), gen.generateSelectAllProc(table));
            writeFile(String.format("%s_select_one_proc.sql", table.getName()), gen.generateSelectByPKProc(table));
            writeFile(String.format("%s_insert_proc.sql", table.getName()), gen.generateInsertProc(table));
            writeFile(String.format("%s_update_proc.sql", table.getName()), gen.generateUpdateProc(table));
            writeFile(String.format("%s_delete_proc.sql", table.getName()), gen.generateDeleteProc(table));
            writeFile(String.format("%s_proc_grants.sql", table.getName()), gen.generateProcGrants(table, argMapping.get("db_user")));
            BeanGenerator ben = new BeanGenerator(table, argMapping.get("package_name"));
            writeFile(String.format("%s.java", table.getName()), ben.generateEntity());
            writeFile(String.format("%sService.java", table.getName()), ben.generateService());
            writeFile(String.format("%sRepository.java", table.getName()), ben.generateRepo());
            writeFile(String.format("%sController.java", table.getName()), ben.generateController());
        } catch (Exception ex) {
            System.out.println(ex.getClass().toString());
            System.out.println(ex.getMessage());
        } finally {
            try {
                iStream.close();
            } catch(Exception ex) {}
        }
    }
}