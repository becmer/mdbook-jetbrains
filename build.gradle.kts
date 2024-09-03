plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "pl.becmer.dev"
version = "2024.1.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}


dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.2")

        bundledPlugin("org.jetbrains.kotlin")

        instrumentationTools()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "${project.group}.mdbook"
        name = "MarkdownBook"
        version = "${project.version}"
        description = providers.fileContents { file("description.html") }.asText

        ideaVersion {
            sinceBuild = "232"
            untilBuild = "242.*"
        }

        vendor {
            name = "Kamil Becmer"
            email = "kamil.becmer@gmail.com"
            url = "https://github.com/becmer"
        }
    }

    signing {
        certificateChain = providers.fileContents { file("certificate/chain.crt") }.asText
        privateKey = providers.fileContents { file("certificate/private.pem") }.asText
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
