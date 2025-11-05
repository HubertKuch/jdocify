import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

class JDocifyPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('jdocify', JDocifyExtension)

        project.plugins.apply("java")

        project.tasks.register('processJdocifyDefaults', Copy) { processTask ->
            processTask.group = "Documentation"
            processTask.description = "Processes default JDocify properties to set project group."

            processTask.from(project.sourceSets.main.resources) {
                include 'default.properties'
            }

            processTask.into(project.file("${project.buildDir}/jdocify_resources"))

            def props = [projectGroup: project.group]

            processTask.filteringCharset = "UTF-8"
            processTask.expand(props)
        }

        project.tasks.register('jdocify', JavaExec) { task ->

            task.dependsOn('processJdocifyDefaults')

            task.group = "Documentation"

            def processedResourcesDir = project.file("${project.buildDir}/jdocify_resources")
            task.classpath = project.files(processedResourcesDir) + project.sourceSets.main.runtimeClasspath

            File userPropertiesFile = project.file(extension.propertiesFileName)
            if (userPropertiesFile.exists()) {
                task.systemProperty 'jdocify.configFile', userPropertiesFile.getAbsolutePath()
            } else {
                project.logger.info("JDocify: User configuration file ${extension.propertiesFileName} not found. Using defaults.")
            }

            task.mainClass = 'pl.hubertkuch.jdocify.generator.DocumentationGenerator'
        }
    }
}