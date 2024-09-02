package pl.becmer.dev.mdbook

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val LOG = logger<BookTomlManager>()

class BookTomlManager : Disposable {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val managedBooksLock = ReentrantLock(true)
    private var managedBooks: MutableMap<VirtualFile, ManagedBook> = mutableMapOf()

    init {
        LOG.info("Initializing")
        VirtualFileManager.getInstance().addAsyncFileListener(BookTomlListener(), this)
        LOG.info("Initialized")
    }

    override fun dispose() {
        LOG.info("Disposing")
        val booksToStop = managedBooksLock.withLock {
            managedBooks.keys.toList().mapNotNull { removeBook(it) }
        }

        val cleanupScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        cleanupScope.launch {
            booksToStop.forEach {
                when (it) {
                    is ManagedBookRequest.Start -> {}
                    is ManagedBookRequest.Stop -> it.book.stop()
                }
            }
        }.invokeOnCompletion {
            cleanupScope.cancel()
        }

        scope.cancel()
        LOG.info("Disposed")
    }

    fun tryUpdate(bookToml: VirtualFile) {
        if (!bookToml.isBookToml) {
            return
        }

        val exists = bookToml.exists()
        val request = managedBooksLock.withLock {
            val isManaged = managedBooks.containsKey(bookToml)
            if (exists && !isManaged) {
                addBook(bookToml)
            } else if (!exists && isManaged) {
                removeBook(bookToml)
            } else {
                null
            }
        }

        request?.let {
            scope.launch {
                when (request) {
                    is ManagedBookRequest.Start -> request.book.start()
                    is ManagedBookRequest.Stop -> request.book.stop()
                }
            }
        }
    }

    private fun addBook(toml: VirtualFile): ManagedBookRequest? {
        return ManagedBook.create(toml)?.let {
            managedBooks[toml] = it
            LOG.info("Book added: ${toml.path}")
            ManagedBookRequest.Start(it)
        } ?: run {
            LOG.warn("Cannot add book: ${toml.path}")
            null
        }
    }

    private fun removeBook(bookToml: VirtualFile): ManagedBookRequest? {
        return managedBooks.remove(bookToml)?.let {
            LOG.info("Book removed: ${bookToml.path}")
            ManagedBookRequest.Stop(it)
        }
    }

    private sealed class ManagedBookRequest {
        data class Start(val book: ManagedBook) : ManagedBookRequest()
        data class Stop(val book: ManagedBook) : ManagedBookRequest()
    }
}

val VirtualFile.isBookToml: Boolean
    get() = this.name == "book.toml" && !this.isDirectory
