package com.github.signer4j.pjeoffice.shell;

import static com.github.signer4j.pjeoffice.shell.Strings.at;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public class ShellExtension {
  
  private static final String ENVIRONMENT_VARIABLE = "PJEOFFICE_HOME";
  
  private ShellExtension() {}

	public static void main(String[] args) {
    Optional<Path> watchPath = Environment.resolveTo(ENVIRONMENT_VARIABLE, "watch");
    if (!watchPath.isPresent()) {
      return;
    }
	  Optional<Task> otask = Task.from(at(args, 0));
	  if (!otask.isPresent()) {
	    return;
	  }
	  File input = new File(at(args, 1));
	  if (!input.exists()) {
	    return;
	  }
	  
	  Task task = otask.get();
	  Properties p = new Properties();
	  p.put("task", task.getId());
    p.put("arquivo", input.getAbsolutePath());
	  task.echo(args, p);
	  
	  File output = watchPath.get().resolve(task.getId() + "." + input.getName() + ".task").toFile();
	  
    try(FileOutputStream out = new FileOutputStream(output)) {
      p.store(out, null);
    } catch (Exception e) {
      ;//ignore!
    }
  }
}
