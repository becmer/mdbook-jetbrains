@file:Suppress("unused")

package pl.becmer.gradle.ext

import org.gradle.api.Project


/**
 * Extension property to access the [CommonPluginExtension].
 */
val Project.common: CommonPluginExtension
    get() = extensions.getByType(CommonPluginExtension::class.java)
