plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
    implementation(libs.bundles.build.plugins)
}

gradlePlugin {
    plugins {
        create("settingsPlugin") {
            id = "pl.becmer.gradle.settings"
            implementationClass = "pl.becmer.gradle.SettingsPlugin"
        }
        create("projectPlugin") {
            id = "pl.becmer.gradle.project"
            implementationClass = "pl.becmer.gradle.ProjectPlugin"
        }
        create("commonModulePlugin") {
            id = "pl.becmer.gradle.module"
            implementationClass = "pl.becmer.gradle.CommonModulePlugin"
        }
        create("intellijModulePlugin") {
            id = "pl.becmer.gradle.module.intellij"
            implementationClass = "pl.becmer.gradle.IntellijModulePlugin"
        }
    }
}
