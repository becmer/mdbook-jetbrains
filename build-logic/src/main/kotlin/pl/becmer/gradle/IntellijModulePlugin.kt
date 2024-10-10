@file:Suppress("unused")

package pl.becmer.gradle

import org.gradle.api.Project
import org.jetbrains.intellij.platform.gradle.plugins.project.IntelliJPlatformPlugin


class IntellijModulePlugin : CommonModulePlugin() {
    override fun apply(target: Project) {
        super.apply(target)

        // This is not an IntelliJPlatformModulePlugin because the project focuses on functionality that may span
        // multiple platforms, not only IntelliJ.
        //
        // A module dedicated to the IntelliJ Platform is considered a distribution module, not a platform module.
        target.pluginManager.apply(IntelliJPlatformPlugin::class.java)
    }
}
