package pl.becmer.dev.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.changelog.ChangelogPluginExtension

class CommonPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        when (target) {
            target.rootProject -> {
                // https://github.com/JetBrains/gradle-changelog-plugin
                target.pluginManager.apply(org.jetbrains.changelog.ChangelogPlugin::class.java)
                target.extensions.getByType(ChangelogPluginExtension::class.java).apply {
                    groups.empty()
                    repositoryUrl.set(target.providers.gradleProperty("pluginRepositoryUrl"))
                }
            }

            else -> {
                target.rootProject.extensions.getByType(ChangelogPluginExtension::class.java)
            }
        }.let { changelogExtension ->
            target.extensions.create("common", CommonPluginExtension::class.java, changelogExtension)
        }
    }
}
