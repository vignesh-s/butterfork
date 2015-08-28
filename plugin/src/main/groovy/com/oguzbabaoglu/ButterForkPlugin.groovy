package com.oguzbabaoglu

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The ButterFork plugin will add build tasks to generate B files from R files.
 */
class ButterForkPlugin implements Plugin<Project> {

    private Project project

    void apply(Project project) {

        this.project = project

        if (!project.hasProperty('android')) {
            throw new GradleException('Please apply the Android plugin first')
        }

        project.afterEvaluate {

            String packageName = findPackageName()

            // The Android variants are only available at this point.
            addGenerateTasks(packageName)
        }
    }

    private Object getNonTestVariants() {
        return project.android.hasProperty('libraryVariants') ?
                project.android.libraryVariants : project.android.applicationVariants
    }

    /**
     * Adds generateB tasks to the project.
     */
    private addGenerateTasks(String packageName) {
        getNonTestVariants().each { variant ->
            addTaskForVariant(variant, packageName)
        }
    }

    /**
     * Creates generateB task for a variant in an Android project.
     */
    private addTaskForVariant(final Object variant, final String packageName) {

        String taskName = 'generate' + variant.name.capitalize() + 'B'
        String rFilePath = 'build/generated/source/r/' + variant.dirName + '/' +
                packageName.replace('.', '/') + '/R.java'

        String bDirectoryPath = 'build/generated/source/b/' + variant.dirName

        GenerateBTask task = project.tasks.create(taskName, GenerateBTask)
        task.rFilePath = rFilePath
        task.bDirectoryPath = bDirectoryPath
        task.packageName = packageName

        variant.outputs.each { output ->
            if (output.name == variant.name) {
                task.dependsOn(output.processResources)
            }
        }

        variant.javaCompile.options.compilerArgs << "-Arespackagename=" + packageName

        variant.javaCompile.dependsOn(task)
        variant.registerJavaGeneratingTask(task, project.file(bDirectoryPath))
    }

    /**
     * Helper method that parses the manifest file and returns package name
     *
     * @return package name defined in manifest file
     */
    private String findPackageName() {
        File manifestFile = project.android.sourceSets.main.manifest.srcFile
        return (new XmlParser()).parse(manifestFile).@package
    }

}
