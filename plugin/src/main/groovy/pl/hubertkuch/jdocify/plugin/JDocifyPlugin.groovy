package pl.hubertkuch.jdocify.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.file.CopySpec
import java.io.File

class JDocifyPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('jdocify', JDocifyExtension)

        project.plugins.apply("java")

        def processedResourcesDir = project.layout.buildDirectory.dir("jdocify_resources").get().getAsFile()

        def processTask = project.tasks.register('processJdocifyDefaults', Copy) { Copy task ->
            task.group = "Documentation"
            task.description = "Processes default JDocify properties to set project group."

            task.from(project.sourceSets.main.resources) { CopySpec copySpec ->
                copySpec.include 'default.properties'
            }

            task.into(processedResourcesDir)

            def props = [projectGroup: project.group]
            task.filteringCharset = "UTF-8"
            task.expand(props)
        }

        project.tasks.register('jdocify', JavaExec) { JavaExec task ->

            task.dependsOn(processTask)

            task.group = "Documentation"

            task.mainClass.set('pl.hubertkuch.jdocify.JDocify')

            task.classpath.setFrom(
                    processedResourcesDir,
                    project.sourceSets.main.runtimeClasspath
            )

            task.doFirst {
                task.systemProperties.remove('jdocify.configFile')

                if (extension.scanPackage.equals("default_not_set")) {
                    extension.scanPackage = project.group
                }
                task.systemProperty 'jdocify.scanPackage', extension.scanPackage

                if (extension.modelPath != null) {
                    task.systemProperty 'jdocify.modelPath', extension.modelPath
                }

                File userPropertiesFile = project.file(extension.propertiesFileName)
                File defaultFile = new File(processedResourcesDir, "default.properties")

                if (userPropertiesFile.exists()) {
                    task.systemProperty 'jdocify.configFile', userPropertiesFile.getAbsolutePath()
                    project.logger.info("JDocify: Using user configuration file: ${userPropertiesFile.getAbsolutePath()}")

                } else if (defaultFile.exists()) {
                    task.systemProperty 'jdocify.configFile', defaultFile.getAbsolutePath()
                    project.logger.info("JDocify: User file not found. Using processed default configuration.")

                } else {
                    project.logger.info("JDocify: No local configuration file found. Falling back to default sources.")
                }
            }
        }
    }
}