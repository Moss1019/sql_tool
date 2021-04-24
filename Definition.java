
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
  private static Map<String, String> processArgs(String[] args) {
    Map<String, String> argMapping = new HashMap<>();
    for(int i = 0; i < args.length; i += 2) {
      if(args[i].equals("-f")) {
        argMapping.put("file_name", args[i + 1]);
      } else if(args[i].equals("-p")) {
        argMapping.put("package_name", args[i + 1]);
      } else if(args[i].equals("-u")) {
        argMapping.put("db_user", args[i + 1]);
      } else if(args[i].equals("-o")) {
        argMapping.put("storage_option", args[i + 1]);
      } else if(args[i].equals("-bend")) {
        argMapping.put("backend_folder", args[i + 1]);
      } else if(args[i].equals("-fend")) {
        argMapping.put("frontend_folder", args[i + 1]);
      } else if(args[i].equals("-endp")) {
        argMapping.put("end_point", args[i + 1]);
      }
    }
    return argMapping;
  }

  public static void writeFile(String fileName, String fileContent, String directory) {
    String filePath = null;
    if(directory != null) {
      File f = new File(String.format("%s", directory));
      if(!f.exists()) {
        f.mkdirs();
      }
      filePath = String.format("%s/%s", directory, fileName);
    } else {
      filePath = String.format("%s", fileName);
    }
    FileOutputStream fOut = null;
    BufferedOutputStream bufOStream = null;
    System.out.println(filePath);
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

  private static void writeFiles(Map<String, String> contents, String extension, String folder, String path) {
    String fullPath = String.format("%s/%s", path, folder);
    for(String f: contents.keySet()) {
      writeFile(String.format("%s.%s", f, extension), contents.get(f), fullPath);
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
      String be = argMapping.get("backend_folder");
      String fe = argMapping.get("frontend_folder");
      String endp = argMapping.get("end_point");

      Database db = new Database(dbUser, packageName, (List<Table>)visitor.visit(tree));

      ViewGenerator vGen = new ViewGenerator(db);
      writeFiles(vGen.generate(), "java", "view", be);

      ServiceGenerator sGen = new ServiceGenerator(db);
      writeFiles(sGen.generate(), "java", "service", be);

      MapperGenerator mGen = new MapperGenerator(db);
      writeFiles(mGen.generate(), "java", "mapper", be);

      ControllerGenerator cGen = new ControllerGenerator(db);
      writeFiles(cGen.generate(), "java", "controller", be);

      ConfigGenerator confGen = new ConfigGenerator(db);
      writeFiles(confGen.generate(), "java", "", be);

      TypeScriptGenerator tGen = new TypeScriptGenerator(db);
      writeFiles(tGen.generate(), "ts", "common", fe);

      HttpGenerator httpGen = new HttpGenerator(db, endp);
      writeFiles(httpGen.generate(), "ts", "http", fe);

		  int storageOption = Integer.parseInt(argMapping.get("storage_option"));  
      switch(storageOption) {
        case 0: // MySql
          SqlGenerator sqlGen = new SqlGenerator(db);
          writeFiles(sqlGen.generate(), "sql", "", be);      
          EntityGenerator eGen = new EntityGenerator(db);
          writeFiles(eGen.generate(), "java", "entity", be);
          RepositoryGenerator rGen = new RepositoryGenerator(db);
          writeFiles(rGen.generate(), "java", "repository", be);
          break;
        case 1: // Firestore
          FirebaseRepositoryGenerator fireGen = new FirebaseRepositoryGenerator(db);
          writeFiles(fireGen.generate(), "java", "repository", be);
          FirebaseEntityGenerator fireEGen = new FirebaseEntityGenerator(db);
          writeFiles(fireEGen.generate(), "java", "entity", be);
          FirebaseUtilGenerator fireUGen = new FirebaseUtilGenerator(db);
          writeFiles(fireUGen.generate(), "java", "util", be);
          break;
        case 2: // In memory
          InMemoryRepositoryGenerator inMemGen = new InMemoryRepositoryGenerator(db);
          writeFiles(inMemGen.generate(), "java", "repository", be);
          InMemoryEntityGenerator inMemEGen = new InMemoryEntityGenerator(db);
          writeFiles(inMemEGen.generate(), "java", "entity", be);
          InMemoryUtilGenerator inMemUGen = new InMemoryUtilGenerator(db);
          writeFiles(inMemUGen.generate(), "java", "util", be);
          break;
        default:
          System.out.println("Unknown storage option selected");

      }
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