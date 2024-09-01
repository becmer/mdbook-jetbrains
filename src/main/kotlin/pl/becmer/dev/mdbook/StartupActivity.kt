package pl.becmer.dev.mdbook

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionNotApplicableException
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Invoked on opening a project
 */
internal class StartupActivity : ProjectActivity {
    init {
        if (ApplicationManager.getApplication().isHeadlessEnvironment ||
            ApplicationManager.getApplication().isUnitTestMode
        ) {
            throw ExtensionNotApplicableException.create()
        }
    }

    override suspend fun execute(project: Project) {
        ProjectService.getInstance(project)
    }

}
