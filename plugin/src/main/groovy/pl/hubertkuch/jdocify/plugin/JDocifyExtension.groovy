package pl.hubertkuch.jdocify.plugin

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet

abstract class JDocifyExtension {

    abstract Property<String> getScanPackage()
    abstract RegularFileProperty getPropertiesFile()

    JDocifyExtension(Project project) {
        scanPackage.convention(project.provider { project.group.toString() })

        def defaultPropsFileProvider = project.provider {
            def javaExtension = project.extensions.findByType(JavaPluginExtension)
            if (javaExtension) {
                def mainSourceSet = javaExtension.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)
                if (mainSourceSet && !mainSourceSet.resources.srcDirs.empty) {
                    def resourcesDir = mainSourceSet.resources.srcDirs.iterator().next()

                    return new File(resourcesDir, "jdocify.properties")
                }
            }
            return project.layout.projectDirectory.file("src/main/resources/jdocify.properties").asFile
        }

        propertiesFile.convention(project.layout.file(defaultPropsFileProvider))
    }
}