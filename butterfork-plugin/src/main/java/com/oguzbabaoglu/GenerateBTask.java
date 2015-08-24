package com.oguzbabaoglu;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GenerateBTask extends DefaultTask {

  @TaskAction
  public void greet() {

    System.out.println("Hello from Generate Task");
  }

}
