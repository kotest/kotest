package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.styles.psi.specs
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.TreeSelectionModel

class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {

   private val tree = createTree()
   private var mod = 0L

   init {
      background = Color.WHITE
      toolbar = createToolbar()
      setContent(ScrollPaneFactory.createScrollPane(tree))
      listenForSelectedEditorChanges()
      listenForPsiChanges()
      refreshContent()
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
      result.add(RunAction(AllIcons.Actions.Execute, tree, project, "Run"))
      result.add(RunAction(AllIcons.Actions.StartDebugger, tree, project, "Debug"))
      result.add(RunAction(AllIcons.General.RunWithCoverage, tree, project, "Coverage"))
      return result
   }

   private fun listenForPsiChanges() {
      project.messageBus.connect().subscribe(
         PsiModificationTracker.TOPIC,
         PsiModificationTracker.Listener {
            val count = PsiModificationTracker.SERVICE.getInstance(project).modificationCount
            PsiModificationTracker.SERVICE.getInstance(project)
            if (count > mod) {
               mod = count
               refreshContent()
            }
         }
      )
   }

   private fun listenForSelectedEditorChanges() {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
               mod = 0
               refreshContent()
            }
         }
      )
   }

   private fun refreshContent() {
      val manager = FileEditorManager.getInstance(project)
      val editor = manager.selectedEditor
      val file = editor?.file
      refreshContent(file)
   }

   private fun refreshContent(file: VirtualFile?) {

      if (file == null) {
         tree.model = emptyTreeModel()
         tree.isRootVisible = true
      } else {
         val module = ModuleUtilCore.findModuleForFile(file, project)
         if (module == null) {
            tree.model = emptyTreeModel()
            tree.isRootVisible = true
         } else {
            DumbService.getInstance(project).runWhenSmart {
               try {
                  val specs = PsiManager.getInstance(project).findFile(file)?.specs() ?: emptyList()
                  val model = treeModel(project, specs, module)
                  tree.model = model
                  tree.isRootVisible = false
                  tree.expandAllNodes()
               } catch (e: Throwable) {
               }
            }
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
