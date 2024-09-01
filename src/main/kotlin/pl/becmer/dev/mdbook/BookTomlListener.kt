package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

internal class BookTomlListener : AsyncFileListener, Disposable {
    override fun dispose() {

    }

    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        val bookTomlEvents = events.mapNotNull { it.file?.takeIf(BookTomlManager::isBookToml) }

        return if (bookTomlEvents.isNotEmpty()) {
            object : AsyncFileListener.ChangeApplier {
                override fun afterVfsChange() {
                    bookTomlEvents.forEach { PluginService.getInstance().getBookTomlManager().tryUpdate(it) }
                }
            }
        } else {
            null // No relevant file changes, no action needed
        }
    }


}
