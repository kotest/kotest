package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.content.ContentFactory
import io.kotest.plugin.intellij.styles.psi.specs
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.TreeSelectionModel

class KotestToolWindowFactory : ToolWindowFactory {

   override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
      val explorer = TestExplorerWindow(project)
      val contentFactory = ContentFactory.SERVICE.getInstance()
      val content = contentFactory.createContent(explorer, "", false)
      toolWindow.contentManager.addContent(content)
   }
}

class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {

   private val tree = createTree()

   init {
      background = Color.WHITE
      toolbar = createToolbar()
      setContent(ScrollPaneFactory.createScrollPane(tree))
      setInitialContent()
      listenForSelectedFileChanges()
   }

   private fun createToolbar(): JComponent {
      return ActionManager.getInstance().createActionToolbar(
         ActionPlaces.STRUCTURE_VIEW_TOOLBAR,
         createActionGroup(),
         true
      ).component
   }

   private fun createActionGroup(): DefaultActionGroup {
      val result = DefaultActionGroup()
      result.add(object : AnAction(AllIcons.RunConfigurations.TestState.Run_run) {
         override fun actionPerformed(e: AnActionEvent) {
            println("Running all tests")
         }
      })
      result.add(object : AnAction(AllIcons.RunConfigurations.TestState.Run) {
         override fun actionPerformed(e: AnActionEvent) {
            println("Running test")
         }
      })
      return result
   }

   private fun listenForSelectedFileChanges() {
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
   }

   private fun setInitialContent() {
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
         tree.model = model
         tree.expandAllNodes()
      } catch (e: IndexNotReadyException) {
      }
   }

   private fun createTree(): JTree {
      val tree = com.intellij.ui.treeStructure.Tree()
      tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      tree.showsRootHandles = true
      tree.cellRenderer = NodeRenderer()
      tree.addTreeSelectionListener(TestExplorerTreeSelectionListener)
      return tree
   }
}

