package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex

private val LOG = logger<ProjectService>()

@Service(Service.Level.PROJECT)
class ProjectService(project: Project) : Disposable {
    init {
        LOG.info("Initializing")
        val bookTomlManager = PluginService.getInstance().getBookTomlManager()
        val index = ProjectFileIndex.getInstance(project)
        index.iterateContent {
            bookTomlManager.tryUpdate(it)
            return@iterateContent true
        }
        LOG.info("Initialized")
    }

    override fun dispose() {
        LOG.info("Disposing")
        LOG.info("Disposed")
    }

    companion object {
        fun getInstance(project: Project): ProjectService {
            return project.getService(ProjectService::class.java)
        }
    }
}
