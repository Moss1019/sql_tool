
import java.util.*;
import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Program {
  private static Map<String, String> arguments;

  private static void processArguments(String[] args) {
    arguments = new HashMap<>();
    for(int i = 0; i < args.length; i += 2) {
      if(args[i].equals("-f")) {
        arguments.put("file", args[i + 1]);
      }
      if(args[i].equals("-rn")) {
        arguments.put("rootname", args[i + 1]);
      }
      if(args[i].equals("-db")) {
        arguments.put("databasetype", args[i + 1]);
      }
    }
  }

  public static void writeFiles(Map<String, String> files, String dir) {
    for(String key: files.keySet()) {
      String outputDir = String.format("../output/%s/%s", dir, key);
      FileHandler fh = new FileHandler(outputDir);
      fh.writeFile(files.get(key));
      if(fh.isInError()) {
        System.out.println("Error while writing file");
      }
    }
  }

  public static void main(String[] args) {
    processArguments(args);
    try {
      InputStream s = new FileInputStream(String.format("../%s", arguments.get("file")));
      ANTLRInputStream antlrIStream = new ANTLRInputStream(s);
      GeneratorLexer lexer = new GeneratorLexer(antlrIStream);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      GeneratorParser parser = new GeneratorParser(tokens);
      s.close();
      ParseTree tree = parser.database();
      DatabaseVisitor visitor = new DatabaseVisitor();

      Database db = new Database(arguments.get("rootname"), 
        arguments.get("databasetype"), 
        (List<Table>)visitor.visit(tree));
      
      EntityGenerator entityGenerator = new EntityGenerator(db);
      writeFiles(entityGenerator.generate(), "Entities");

      ViewGenerator viewGenerator = new ViewGenerator(db);
      writeFiles(viewGenerator.generate(), "Views");

      MapperGenerator mapperGenerator = new MapperGenerator(db);
      writeFiles(mapperGenerator.generate(), "Mappers");

      ControllerGenerator controllerGenerator = new ControllerGenerator(db);
      writeFiles(controllerGenerator.generate(), "Controllers");

      ServiceGenerator serviceGenerator = new ServiceGenerator(db);
      writeFiles(serviceGenerator.generate(), "Services");

      RepositoryGenerator repositoryGenerator = new RepositoryGenerator(db);
      writeFiles(repositoryGenerator.generate(), "Repositories");

      ConfigGenerator configGenerator = new ConfigGenerator(db);
      writeFiles(configGenerator.generate(), "");

    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    // generate sql    

    // generate controllers

    // generate services

    // generate repositories

    // generate entities

    // generate views
  }
}

