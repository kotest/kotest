package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import io.kotest.plugin.intellij.getContentFactory

/**
 * Wired into the plugin.xml and creates a [TestExplorerWindow] upon demand.
 */
class TestExplorerToolWindowFactory : ToolWindowFactory {

   override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
      val explorer = TestExplorerWindow(project)
      val contentFactory = getContentFactory()
      val content = contentFactory.createContent(explorer, "", false)
      toolWindow.contentManager.addContent(content)
   }
}
