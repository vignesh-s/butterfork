package com.oguzbabaoglu

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class GenerateBTask extends DefaultTask {

  String rFilePath
  String bDirectoryPath
  String packageName

  @TaskAction
  void generate() {

    File rFile = project.file(rFilePath)
    File bFile = project.file(bDirectoryPath)

    try {
      BindingClassBuilder.brewJava(rFile, bFile, packageName)
    } catch (Exception e) {
      throw new GradleException("Error creating B file.", e)
    }
  }
}
