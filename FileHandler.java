
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHandler {
  private String fileName;
  private boolean inError;

  public FileHandler(String fileName) {
    this.fileName = fileName;
    inError = false;
  }

  public boolean isInError() {
    return inError;
  }

  public String readFile() {
    String fileContent;
    FileInputStream fiStream = null;
    BufferedInputStream bufStream = null;
    try {
      fiStream = new FileInputStream(fileName);
      bufStream = new BufferedInputStream(fiStream);
      fileContent = new String(bufStream.readAllBytes());
    } catch (IOException ex) {
      fileContent = "";
      inError = true;
    } finally {
      try {
        bufStream.close();
      } catch (Exception ex) {}
      try {
        fiStream.close();
      } catch (Exception ex){}
    }
    return fileContent;
  }
}

