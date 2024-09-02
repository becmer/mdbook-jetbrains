package pl.becmer.dev.mdbook

import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.kotlin.tools.projectWizard.core.ignore
import pl.becmer.dev.mdbook.ChildProcessManager.ManagedProcess
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ManagedBook private constructor(
    val toml: VirtualFile,
    private val path: Path,
) {
    private val guard = Mutex()
    private var state: State = State.Stopped

    companion object {
        fun create(toml: VirtualFile): ManagedBook? {
            val path = toml.parent?.let { it.fileSystem.getNioPath(it) } ?: return null
            return ManagedBook(toml, path)
        }
    }

    suspend fun start(readyTimeout: Duration = 500.milliseconds) {
        guard.withLock {
            if (state is State.Stopped) {
                PluginService.childProcessManager.start {
                    ProcessBuilder("mdbook", "serve", "--port", "0")
                        .directory(path.toFile())
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectErrorStream(true)
                        .start()
                }?.let {
                    state = State.Starting(it)
                    it
                }
            } else {
                null
            }
        }?.let { process ->
            val isRunning = withTimeoutOrNull(readyTimeout) {
                waitUntilReady(process)
            } ?: false

            if (!isRunning) {
                process.stop()
            }
        }
    }

    private suspend fun waitUntilReady(process: ManagedProcess): Boolean = withContext(Dispatchers.IO) {
        val pattern = Regex("""listening on http://\[::1]:(\d+)""")

        process.stdout.bufferedReader().useLines { lines ->
            for (line in lines) {
                pattern.find(line)?.groups?.get(1)?.value?.toIntOrNull()?.let {
                    guard.withLock {
                        if (state is State.Starting) {
                            state = State.Running(process, it)
                            return@useLines true
                        }
                    }
                    return@useLines false
                }
            }
            false
        }
    }

    suspend fun stop() {
        guard.withLock {
            state.process()?.stop()?.ignore()
        }
    }

    private sealed class State {
        data object Stopped : State()
        data class Starting(val process: ManagedProcess) : State()
        data class Running(val process: ManagedProcess, val portNumber: Int) : State()

        fun process(): ManagedProcess? = when (this) {
            is Running -> process
            is Starting -> process
            is Stopped -> null
        }
    }
}
