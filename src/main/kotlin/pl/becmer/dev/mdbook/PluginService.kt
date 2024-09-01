package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer

private val LOG = logger<PluginService>()

@Service(Service.Level.APP)
class PluginService : Disposable {

    private val bookTomlManager = BookTomlManager()

    init {
        LOG.info("Initializing")
        Disposer.register(this, bookTomlManager)
        LOG.info("Initialized")
    }

    override fun dispose() {
        LOG.info("Disposing")
        LOG.info("Disposed")
    }

    fun getBookTomlManager(): BookTomlManager = bookTomlManager

    companion object {
        fun getInstance(): PluginService {
            return ApplicationManager.getApplication().getService(PluginService::class.java)
        }
    }
}
