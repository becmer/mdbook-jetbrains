plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.intellij.plugins:gradle-changelog-plugin:2.2.1")
}

gradlePlugin {
    plugins {
        create("common") {
            id = "pl.becmer.dev.gradle.common"
            implementationClass = "pl.becmer.dev.gradle.CommonPlugin"
        }
    }
}
