/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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
