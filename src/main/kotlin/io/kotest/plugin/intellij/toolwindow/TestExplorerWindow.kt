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
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.styles.psi.specs
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.projectStructure.getModule
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.TreeSelectionModel

class TestExplorerWindow(private val toolWindow: ToolWindow,
                         private val project: Project) : SimpleToolWindowPanel(true, false) {

   private val tree = createTree()

   init {
      background = Color.WHITE
      toolbar = createToolbar()
      setContent(ScrollPaneFactory.createScrollPane(tree))
      loadContent()
      listenForSelectedFileChanges()
      toolWindow.activate {
         loadContent()
      }
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

      result.add(object : AnAction(AllIcons.Actions.Execute) {
         override fun actionPerformed(e: AnActionEvent) {
            val path = tree.selectionPath
            if (path != null) {
               when (val node = path.node()) {
                  is SpecNodeDescriptor -> runSpec(node, project, "Run")
                  is TestNodeDescriptor -> runTest(node, project, "Run")
               }
            }
         }
      })

      result.add(object : AnAction(AllIcons.Actions.StartDebugger) {
         override fun actionPerformed(e: AnActionEvent) {
            val path = tree.selectionPath
            if (path != null) {
               when (val node = path.node()) {
                  is SpecNodeDescriptor -> runSpec(node, project, "Debug")
                  is TestNodeDescriptor -> runTest(node, project, "Debug")
               }
            }
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
                  FileDocumentManager.getInstance().getDocument(file)?.addDocumentListener(object :
                     DocumentListener {
                     override fun documentChanged(event: DocumentEvent) {
                        refreshContent(file)
                     }
                  })
               }
            }
         }
      )
   }

   private fun loadContent() {
      try {
         val editor = FileEditorManager.getInstance(project).selectedEditor
         val file = editor?.file
         refreshContent(file)
      } catch (e: IndexNotReadyException) {
      }
   }

   private fun refreshContent(file: VirtualFile?) {
      if (file == null) {
         tree.model = emptyTreeModel()
         tree.isRootVisible = true
      } else {
         try {
            val module = file.getModule(project)
            if (module == null) {
               tree.model = emptyTreeModel()
               tree.isRootVisible = true
            } else {
               val specs = file.toPsiFile(project)?.specs() ?: emptyList()
               val model = treeModel(project, specs, module)
               tree.model = model
               tree.isRootVisible = false
               tree.expandAllNodes()
            }
         } catch (e: IndexNotReadyException) {
         }
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
