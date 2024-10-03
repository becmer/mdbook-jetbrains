plugins {
    idea
    id("pl.becmer.dev.gradle.common")
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// https://docs.gradle.org/8.9/dsl/org.gradle.plugins.ide.idea.model.IdeaModule.html
idea {
    module {
        excludeDirs.add(file(".intellijPlatform"))
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}
