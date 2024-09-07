<!-- BEGIN PLUGIN DESCRIPTION -->
Integrate [mdbook](https://rust-lang.github.io/mdBook/) into your JetBrains IDE.

This plugin provides essential features for working with mdBook projects directly within your IDE:

- Live Preview: Launch and manage the <code>mdbook serve</code> instance for real-time previews.
- Embedded Browser: View your beautifully rendered mdBook documentation right within the IDE.

**Streamline your documentation workflow and focus on creating high-quality content with the MarkdownBook plugin.**

<br/>

This is the initial release. Expect UI/UX issues.

<br/>

Caveats:
- Assumes that `mdbook` is available on the `PATH`
- Tested on Linux only so far
- Likely many unknowns.

<br/>

**TODO:**

- File Watcher: Generally done, but watch also for non-obvious changes like file renames.
- Browser State: Preserve cookies, e.g., to preserve selected theme.
- Configuration: Allow customizing mdBook settings and parameters.
- Autodiscovery: Automatic detection of the mdBook binary.
<!-- END PLUGIN DESCRIPTION -->
