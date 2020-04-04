package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
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
import javax.swing.JTree
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

class KotestToolWindowFactory : ToolWindowFactory {

   override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
      val explorer = TestExplorerWindow(project)
      val contentFactory = ContentFactory.SERVICE.getInstance()
      val content = contentFactory.createContent(ScrollPaneFactory.createScrollPane(explorer.getContent()), "", false)
      toolWindow.contentManager.addContent(content)
   }
}

class TestExplorerWindow(private val project: Project) {

   private val content: JPanel = JPanel().apply {
      layout = GridLayout(0, 1)
   }

   init {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
               refreshContent(event.newFile)
               val file = event.newFile
               if (file != null) {
                  FileDocumentManager.getInstance().getDocument(file)?.addDocumentListener(object : DocumentListener {
                     override fun documentChanged(event: DocumentEvent) {
                        refreshContent(file)
                     }
                  })
               }
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
      try {
         val psi = file?.toPsiFile(project)
         val specs = psi?.specs() ?: emptyList()
         val model = treeModel(project, specs)
         val tree = setupTree(model)
         content.removeAll()
         content.add(tree)
      } catch (e: IndexNotReadyException) {
      }
   }

   private fun setupTree(model: TreeModel): JTree {
      val tree = com.intellij.ui.treeStructure.Tree(model)
      tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      tree.showsRootHandles = true
      tree.cellRenderer = NodeRenderer()
      tree.expandAllNodes()
      tree.addTreeSelectionListener(TestExplorerTreeSelectionListener)
      return tree
   }

   fun getContent(): JPanel {
      return content
   }
}

