
import java.util.Map;
import java.util.HashMap;

public class Generator {
  protected boolean currentLoopedOrJoined;

  public Map<String, String> generate() {
    return new HashMap<>();
  }

  protected String loadTemplate(String path, String fileName) {
    FileHandler fh = new FileHandler(String.format("%s/%s.tmpl", path, fileName));
    String content = fh.readFile();
    if(fh.isInError()) {
      System.out.println("Could not read " + fileName);
      return "";
    }
    return content;
  }
}