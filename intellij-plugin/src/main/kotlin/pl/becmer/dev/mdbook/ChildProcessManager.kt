package pl.becmer.dev.mdbook

import com.intellij.openapi.diagnostic.logger
import com.intellij.util.io.awaitExit
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.kotlin.tools.projectWizard.core.ignore
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val LOG = logger<ChildProcessManager>()

class ChildProcessManager private constructor(
    private val scope: CoroutineScope,
    private val cleanupInterval: Duration
) : CoroutineScope by scope {

    private val managedProcessesLock = Mutex()
    private val managedProcesses = HashMap<ProcessHandle, ManagedProcess>()
    private var cleanupJob: Job? = null
    private var isShutdown = false

    companion object {
        fun createChildProcessManager(
            scope: CoroutineScope,
            cleanupInterval: Duration = 250.milliseconds
        ): ChildProcessManager {
            val manager = ChildProcessManager(scope, cleanupInterval)

            scope.coroutineContext[Job]?.invokeOnCompletion {
                scope.launch { manager.shutdown() }
            }

            return manager
        }
    }

    suspend fun start(processSupplier: () -> Process): ManagedProcess? {
        val process = ManagedProcess()

        return managedProcessesLock.withLock {
            if (isShutdown) {
                return@withLock null
            }

            process.start(processSupplier)
            val handle = process.handle
            managedProcesses[handle] = process
            LOG.info("Started $handle")

            // Restart the managerJob if necessary
            if (cleanupJob?.isCompleted == true) {
                cleanupJob = scope.launch { cleanupStaleProcesses() }
            }
            process
        }
    }

    private suspend fun cleanupStaleProcesses() {
        while (isActive) {
            getStaleProcesses().forEach { it.cleanup().ignore() }
            if (!isActive) break
            delay(cleanupInterval)
        }
    }

    private suspend fun getStaleProcesses() =
        managedProcessesLock.withLock { managedProcesses.values.filter { !it.isAlive } }

    suspend fun shutdown() = withContext(coroutineContext) {
        cleanupJob?.cancelAndJoin()
        // Stopping a process may take a while, so defer this to be done out of the lock
        takeAllProcesses().forEach { it.stop().ignore() }
    }

    private suspend fun takeAllProcesses(): List<ManagedProcess> {
        return managedProcessesLock.withLock {
            isShutdown = true
            val processes = managedProcesses.values.toList()
            managedProcesses.clear()
            processes
        }
    }

    inner class ManagedProcess {
        private lateinit var process: Process
        private val isStopping = AtomicBoolean(false)

        val stdout: InputStream get() = process.inputStream
        val isAlive: Boolean get() = process.isAlive
        internal val handle: ProcessHandle get() = process.toHandle()

        internal fun start(processSupplier: () -> Process) {
            process = processSupplier()
        }

        suspend fun stop(): Result<Unit> {
            var result = Result.success(Unit)
            if (isStopping.compareAndSet(false, true)) {
                val handle = this.handle
                LOG.info("Stopping $handle")
                try {
                    process.destroy()
                    process.awaitExit()
                    LOG.info("Stopped $handle")
                } catch (e: Throwable) {
                    LOG.warn("The $handle process did not exit gracefully: ${e.message}")
                    result = Result.failure(e)
                } finally {
                    val cleanupResult = cleanup()
                    isStopping.set(false)
                    if (result.isSuccess && cleanupResult.isFailure) {
                        result = cleanupResult
                    }
                }
            }
            return result
        }

        internal suspend fun cleanup(): Result<Unit> {
            val handle = this.handle
            try {
                handle.onExit().join()
                LOG.info("Cleaned $handle up")
                return Result.success(Unit)
            } catch (e: Throwable) {
                LOG.warn("The $handle process did not cleanup gracefully: ${e.message}")
                return Result.failure(e)
            } finally {
                managedProcessesLock.withLock { managedProcesses.remove(handle) }
            }
        }
    }
}
