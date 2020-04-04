package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.content.ContentFactory
import io.kotest.plugin.intellij.styles.psi.specs
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import java.awt.GridLayout
import javax.swing.JPanel

class KotestToolWindowFactory : ToolWindowFactory {

   override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
      val explorer = KotestStructureWindow(project)
      val contentFactory = ContentFactory.SERVICE.getInstance()
      val content = contentFactory.createContent(ScrollPaneFactory.createScrollPane(explorer.getContent()), "", false)
      toolWindow.contentManager.addContent(content)
   }
}

class KotestStructureWindow(private val project: Project) {

   private val content: JPanel = JPanel().apply {
      layout = GridLayout(0, 1)
   }

   init {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
              refreshContent(event.newFile)
            }
         }
      )
      setupContent()
   }

   private fun setupContent() {
      try {
         val editor = FileEditorManager.getInstance(project).selectedEditor
         val file = editor?.file
         refreshContent(file)
      } catch (e: IndexNotReadyException) {
      }
   }

   private fun refreshContent(file: VirtualFile?) {
      val psi = file?.toPsiFile(project)
      val specs = psi?.specs() ?: emptyList()
      val model = treeModel(specs)
      val tree = com.intellij.ui.treeStructure.Tree(model)
      tree.expandAllNodes()
      content.removeAll()
      content.add(tree)
   }

   fun getContent(): JPanel {
      return content
   }
}

