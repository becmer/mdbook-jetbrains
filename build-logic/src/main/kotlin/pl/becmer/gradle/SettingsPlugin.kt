@file:Suppress("unused")

package pl.becmer.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.jetbrains.intellij.platform.gradle.plugins.settings.IntelliJPlatformSettingsPlugin


class SettingsPlugin: Plugin<Settings> {
    override fun apply(target: Settings) {
        target.pluginManager.apply(IntelliJPlatformSettingsPlugin::class.java)
    }
}
