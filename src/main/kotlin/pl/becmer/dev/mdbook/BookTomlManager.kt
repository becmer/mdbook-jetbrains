package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager

private val LOG = logger<BookTomlManager>()

class BookTomlManager : Disposable {

    private var managedBookTomls: MutableMap<VirtualFile, String> = mutableMapOf()

    init {
        LOG.info("Initializing")
        VirtualFileManager.getInstance().addAsyncFileListener(BookTomlListener(), this)
        LOG.info("Initialized")
    }

    override fun dispose() {
        LOG.info("Disposing")
        managedBookTomls.keys.toList().forEach { removeBook(it) }
        LOG.info("Disposed")
    }

    fun tryUpdate(bookToml: VirtualFile) {
        if (!isBookToml(bookToml)) {
            return
        }

        val isManaged = managedBookTomls.containsKey(bookToml)
        if (bookToml.exists()) {
            if (!isManaged) {
                addBook(bookToml)
            }
        } else if (isManaged) {
            removeBook(bookToml)
        }
    }

    private fun addBook(bookToml: VirtualFile) {
        managedBookTomls[bookToml] = ""
        LOG.info("Start book for ${bookToml.path}")
    }

    private fun removeBook(bookToml: VirtualFile) {
        managedBookTomls.remove(bookToml)
        LOG.info("Stop book for ${bookToml.path}")
    }

    companion object {
        fun isBookToml(bookToml: VirtualFile) = bookToml.name == "book.toml" && !bookToml.isDirectory
    }
}
