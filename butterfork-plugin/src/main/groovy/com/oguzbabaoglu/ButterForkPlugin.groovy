package com.oguzbabaoglu

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

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
            // The Android variants are only available at this point.
            addGenerateTasks()
        }
    }

    private Object getNonTestVariants() {
        return project.android.hasProperty('libraryVariants') ?
                project.android.libraryVariants : project.android.applicationVariants
    }

    /**
     * Adds generateB tasks to the project.
     */
    private addGenerateTasks() {
        getNonTestVariants().each { variant ->
            addTaskForVariant(variant)
        }
    }

    /**
     * Creates generateB task for a variant in an Android project.
     */
    private addTaskForVariant(final Object variant) {

        String taskName = 'generate' + getSubstringForTaskName(variant.name) + 'B'

        GenerateBTask task = project.getTasks().create(taskName, GenerateBTask.class);
        variant.javaCompile.dependsOn(taskName)
    }

    /**
     * Returns the conventional substring that represents the variant in task names,
     * e.g., "generateDebugB"
     */
    static String getSubstringForTaskName(String variantName) {
        return variantName == SourceSet.MAIN_SOURCE_SET_NAME ?
                '' : variantName.capitalize()
    }

}
