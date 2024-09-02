package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val LOG = logger<PluginService>()

@Service(Service.Level.APP)
class PluginService : Disposable {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val childProcessManager = ChildProcessManager.createChildProcessManager(scope)
    private val bookTomlManager = BookTomlManager()

    init {
        LOG.info("Initializing")
        Disposer.register(this, this@PluginService.bookTomlManager)
        LOG.info("Initialized")
    }

    override fun dispose() {
        LOG.info("Disposing")
        LOG.info("Disposed")
    }

    companion object {
        val bookTomlManager: BookTomlManager
            get() = getInstance().bookTomlManager

        val childProcessManager: ChildProcessManager
            get() = getInstance().childProcessManager

        private fun getInstance(): PluginService {
            return ApplicationManager.getApplication().getService(PluginService::class.java)
        }
    }
}
