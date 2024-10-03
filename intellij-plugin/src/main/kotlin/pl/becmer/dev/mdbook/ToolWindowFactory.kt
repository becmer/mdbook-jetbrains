package pl.becmer.dev.mdbook

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import pl.becmer.dev.mdbook.ToolWindowFactory.JBookPanel.Companion.createContent
import java.awt.BorderLayout
import javax.swing.*

class ToolWindowFactory : com.intellij.openapi.wm.ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

    }

    internal class ManagedBookListener(private val project: Project) : ManagedBook.Listener {
        override fun bookAdded(book: ManagedBook) {
            invokeLater { toolWindow ->
                if (book.isInProject(project)) {
                    val content = toolWindow.createContent(book)
                    project.getUserData(JBookPanel.KEY)?.let { it[book] = content } ?: project.putUserData(
                        JBookPanel.KEY,
                        mutableMapOf(book to content)
                    )
                }
            }
        }

        override fun bookServed(book: ManagedBook, portNumber: Int) {
            invokeLater {
                project.getUserData(JBookPanel.KEY)?.get(book)
                    ?.let {
                        (it.component as JBookPanel).loadURL("http://localhost:$portNumber")
                    }
            }
        }

        override fun bookRemoved(book: ManagedBook) {
            invokeLater { toolWindow ->
                project.getUserData(JBookPanel.KEY)?.remove(book)
                    ?.let {
                        toolWindow.contentManager.removeContent(it, true)
                    }
            }
        }

        private fun invokeLater(block: (ToolWindow) -> Unit) {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            toolWindowManager.invokeLater {
                toolWindowManager.getToolWindow("Books")?.let(block)
            }
        }
    }

    class JBookPanel private constructor() : JPanel() {
        companion object {
            internal val KEY: Key<MutableMap<ManagedBook, Content>> = Key.create(JBookPanel::class.java.canonicalName)

            internal fun ToolWindow.createContent(book: ManagedBook): Content {
                val content = ContentFactory.getInstance().createContent(JBookPanel(), book.name, false)
                contentManager.addContent(content)
                return content
            }
        }

        private val browser = JBCefBrowser("about:blank")

        init {
            layout = BorderLayout(0, 0)
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
            add(browser.component, BorderLayout.CENTER)
        }

        fun loadURL(url: String) {
            browser.loadURL(url)
        }
    }
}
