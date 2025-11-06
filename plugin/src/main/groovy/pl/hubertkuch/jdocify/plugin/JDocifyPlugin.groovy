package pl.hubertkuch.jdocify.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class JDocifyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('jdocify', JDocifyExtension)

        project.tasks.register('jdocify', JDocifyTask) { task ->
            task.description = 'Runs JDocify documentation generator.'
            task.group = 'documentation'

            task.packageToScan.set(extension.scanPackage)
            task.sourcePropertiesFile.set(extension.propertiesFile)
        }
    }
}