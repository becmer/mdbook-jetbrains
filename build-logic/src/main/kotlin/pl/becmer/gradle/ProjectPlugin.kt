@file:Suppress("unused")

package pl.becmer.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.changelog.ChangelogPlugin
import org.jetbrains.changelog.ChangelogPluginExtension
import org.jetbrains.intellij.platform.gradle.plugins.project.IntelliJPlatformPlugin
import pl.becmer.gradle.ext.CommonPluginExtension


class ProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        assert(target.parent == null) { "Project plugin can be applied to root project only" }

        target.pluginManager.apply(ChangelogPlugin::class.java)
        target.extensions.getByType(ChangelogPluginExtension::class.java).apply {
            groups.empty()
            repositoryUrl.set(target.providers.gradleProperty("pluginRepositoryUrl"))
        }.let {
            target.extensions.create("common", CommonPluginExtension::class.java, it)
        }

        target.pluginManager.apply(IntelliJPlatformPlugin::class.java)
    }
}
