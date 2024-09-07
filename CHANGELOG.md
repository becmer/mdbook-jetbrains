<!-- Keep a Changelog guide -> https://keepachangelog.com -->
<!-- Gradle Changelog Plugin guide -> https://github.com/JetBrains/gradle-changelog-plugin/blob/main/README.md -->

# MarkdownBook Plugin for JetBrains IDEs Changelog

## [Unreleased]

### Changed

- Migrate to [IntelliJ Platform Gradle Plugin 2.0](https://blog.jetbrains.com/platform/2024/07/intellij-platform-gradle-plugin-2-0/)
- Adapt to [IntelliJ Platform Plugin Template](https://plugins.jetbrains.com/docs/intellij/plugin-github-template.html)
- Migrate to the [Keep a Changelog](https://keepachangelog.com) convention
- Update the minimum supported platform version to `2023.3.7` (`233-242.*`)
- Refactor the scaffold to support [dynamic plugins](https://plugins.jetbrains.com/docs/intellij/dynamic-plugins.html) from the ground up

## [2024.1.0] - 2024-09-03

### Added

- Initial project scaffold
- Application service to manage `book.toml` files
- Coroutine-based child process management to spawn `mdbook serve`
- Tool window to preview books served by `mdbook`

[Unreleased]: https://github.com/becmer/mdbook-jetbrains/compare/v2024.1.0...HEAD
[2024.1.0]: https://github.com/becmer/mdbook-jetbrains/commits/v2024.1.0
