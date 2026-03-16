package sim;

import picocli.CommandLine;
import sim.view.App;

public class Main {
  public static void main(String[] args) {
    new App();
    //int exitCode = new CommandLine(new SimCommand()).execute(args);
    //System.exit(exitCode);
  }
}
