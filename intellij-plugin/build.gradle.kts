import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import pl.becmer.gradle.ext.common

plugins {
    id("java")
    id("pl.becmer.gradle.module.intellij")
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        instrumentationTools()
        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        id = providers.gradleProperty("pluginId")
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- BEGIN PLUGIN DESCRIPTION -->"
            val end = "<!-- END PLUGIN DESCRIPTION -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        // Get the latest available change notes from the changelog file
        changeNotes = project.common.renderChangelog(Changelog.OutputType.HTML)

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }

        vendor {
            name = providers.gradleProperty("vendorName")
            email = providers.gradleProperty("vendorEmail")
            url = providers.gradleProperty("vendorUrl")
        }
    }

    signing {
        certificateChain = providers.fileContents { file("certificate/chain.crt") }.asText
        privateKey = providers.fileContents { file("certificate/private.pem") }.asText
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    // Disabled to enable Auto-Reload in the Development Instance.
    // Refer to https://plugins.jetbrains.com/docs/intellij/ide-development-instance.html#intellij-platform-gradle-plugin-2x
    buildSearchableOptions = false

    // Enabled explicitly just in case something interfered with the default.
    // Refer to https://plugins.jetbrains.com/docs/intellij/ide-development-instance.html#intellij-platform-gradle-plugin-2x
    autoReload = true
}
