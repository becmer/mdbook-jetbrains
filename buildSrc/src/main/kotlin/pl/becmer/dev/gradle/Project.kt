package pl.becmer.dev.gradle

import org.gradle.api.Project


/**
 * Extension property to access the [CommonPluginExtension].
 */
val Project.common: CommonPluginExtension
    get() = extensions.getByType(CommonPluginExtension::class.java)
