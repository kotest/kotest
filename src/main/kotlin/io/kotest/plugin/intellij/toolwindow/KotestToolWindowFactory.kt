package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import io.kotest.plugin.intellij.styles.specs
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

class KotestToolWindowFactory : ToolWindowFactory {

   override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
      val explorer = KotestToolWindow(toolWindow, project)
      val contentFactory = ContentFactory.SERVICE.getInstance()
      val content = contentFactory.createContent(explorer.getContent(), "", false)
      toolWindow.contentManager.addContent(content)
   }
}

class KotestToolWindow(window: ToolWindow, private val project: Project) {

   private val content: JPanel = JPanel()

   init {
      IdeFocusManager.getGlobalInstance().apply {
         this.doWhenFocusSettlesDown {
            println("Refreshing content")
            refreshContent()
         }
      }
      refreshContent()
   }

   private fun refreshContent() {
      try {
         val editor = FileEditorManager.getInstance(project).selectedEditor
         val psi = editor?.file?.toPsiFile(project)
         val specs = psi?.specs() ?: emptyList()
         val model = treeModel(specs)
         val tree = com.intellij.ui.treeStructure.Tree(model)
         content.removeAll()
         content.add(tree)
      } catch (e: IndexNotReadyException) {

      }
   }

   fun getContent(): JPanel {
      return content
   }
}

fun treeModel(specs: List<KtClassOrObject>): TreeModel {
   val root = DefaultMutableTreeNode("Kotest")
   specs.forEach { spec ->
      val node = DefaultMutableTreeNode(spec.getKotlinFqName()?.asString() ?: "Unknown")
      root.add(node)
   }
   return DefaultTreeModel(root)
}
