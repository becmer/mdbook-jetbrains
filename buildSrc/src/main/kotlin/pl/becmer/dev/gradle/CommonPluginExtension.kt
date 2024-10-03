package pl.becmer.dev.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension

abstract class CommonPluginExtension(
    val changelog: ChangelogPluginExtension,
) {
    /**
     * Provides the rendered changelog for the given plugin version.
     *
     * @param pluginVersion The version of the plugin.
     * @return A Provider containing the rendered changelog in the requested format.
     */
    fun renderChangelog(outputType: Changelog.OutputType, pluginVersion: Provider<String>): Provider<String> {
        return pluginVersion.map { version ->
            (changelog.getOrNull(version) ?: changelog.getUnreleased())
                .withHeader(false)
                .withEmptySections(false)
                .let { changelog.renderItem(it, outputType) }
        }
    }

    /**
     * Provides the rendered changelog for the current plugin version as per project's version.
     *
     * @return A Provider containing the rendered changelog in the requested format.
     */
    fun renderChangelog(outputType: Changelog.OutputType) = renderChangelog(
        outputType,
        changelog.version,
    )
}
