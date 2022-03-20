package com.github.signer4j.pjeoffice.shell;

import static com.github.signer4j.pjeoffice.shell.Strings.at;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class ShellExtension {
  
  public static final String APP = "pjeoffice-pro";

  public static final Path HOME = Paths.get(System.getProperty("user.home"));
  
  public static final Path HOME_CONFIG_FOLDER = HOME.resolve("." + APP);
  
  public static final Path HOME_CONFIG_FILE = HOME_CONFIG_FOLDER.resolve(APP + ".config");
  
  public static final Path HOME_WATCHING = HOME_CONFIG_FOLDER.resolve("watching");
  
  private ShellExtension() {}

  public static void main(String... args) {
    
    if (!HOME_WATCHING.toFile().exists()) {
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

    File output = HOME_WATCHING.resolve(task.getId() + "." + input.getName() + ".task").toFile();

    try(FileOutputStream out = new FileOutputStream(output)) {
      p.store(out, null);
    } catch (Exception e) {
      ;//ignore!
    }
  }
}
