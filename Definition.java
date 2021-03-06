
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;

public class Definition {
    public static void writeFile(String fileName, String fileContent, String directory) {
        String filePath = null;
        if(directory != null) {
            File f = new File(String.format("../output/%s", directory));
            if(!f.exists()) {
                f.mkdir();
            }
            filePath = String.format("../output/%s/%s", directory, fileName);
        } else {
            filePath = String.format("../output/%s", fileName);
        }
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

    private static void writeFiles(Map<String, String> contents, String extension, String folder) {
        for(String f: contents.keySet()) {
            writeFile(String.format("%s.%s", f, extension), contents.get(f), folder);
        }
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
            ParseTree tree = parser.database();
            DatabaseVisitor visitor = new DatabaseVisitor();
            String dbUser = argMapping.get("db_user");
            String packageName = argMapping.get("package_name");

            Database db = new Database(dbUser, packageName, (List<Table>)visitor.visit(tree));

            SqlGenerator sqlGen = new SqlGenerator(db);
            writeFiles(sqlGen.generate(), "sql", "");

            EntityGenerator eGen = new EntityGenerator(db);
            writeFiles(eGen.generate(), "java", "entity");

        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(ex.getClass().toString());
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                iStream.close();
            } catch(Exception ex) {}
        }
    }
}