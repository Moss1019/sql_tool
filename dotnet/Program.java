
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

      Database db = new Database((List<Table>)visitor.visit(tree));
      System.out.println(db.toString());
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

