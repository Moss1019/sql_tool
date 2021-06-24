
import java.io.*;

public abstract class Generator {
  protected Database db;
  protected String baseTemplateDir;

  public Generator(Database db, String baseTemplateDir) {
    this.db = db;
    this.baseTemplateDir = baseTemplateDir;
    loadTemplates();
  }

  protected String loadTemplate(String fileName) {
    String path = String.format("%s/%s.tmpl", baseTemplateDir, fileName);
    FileHandler fh = new FileHandler(path);
    String content = fh.readFile();
    if(fh.isInError()) {
      System.err.println("************************\nError while reading file " + path + "\n**************************");
    }
    return content;
  }

  protected abstract void loadTemplates();
}
