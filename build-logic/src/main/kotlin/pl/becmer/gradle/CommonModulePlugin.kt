@file:Suppress("UnstableApiUsage")

package pl.becmer.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.changelog.ChangelogPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import pl.becmer.gradle.ext.CommonPluginExtension


open class CommonModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        assert(target.parent != null) { "Module plugin cannot be applied to root project" }

        target.pluginManager.apply(KotlinPluginWrapper::class.java)
        target.extensions.getByType(KotlinJvmProjectExtension::class.java).apply {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
                vendor.set(JvmVendorSpec.JETBRAINS)
            }
        }

        target.rootProject.extensions.getByType(ChangelogPluginExtension::class.java).let {
            target.extensions.create("common", CommonPluginExtension::class.java, it)
        }
    }
}
