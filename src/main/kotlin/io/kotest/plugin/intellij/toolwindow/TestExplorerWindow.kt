package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiManager
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.psi.specs
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.tree.TreeSelectionModel

class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {

   private val fileEditorManager = FileEditorManager.getInstance(project)
   private val tree = createTree()
   private val runActions = createRunActions(tree, project)

   init {

      tree.addMouseListener(object : MouseAdapter() {
         override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2) {
               runTest(tree, project, "Run", false)
            }
         }
      })

      tree.addTreeSelectionListener(TestExplorerTreeSelectionListener(runActions))

      background = Color.WHITE
      toolbar = createToolbar(runActions, tree)
      setContent(ScrollPaneFactory.createScrollPane(tree))
      listenForSelectedEditorChanges()
      listenForFileChanges()
      refreshContent()
   }

   private fun listenForFileChanges() {
      project.messageBus.connect().subscribe(
         VirtualFileManager.VFS_CHANGES,
         object : BulkFileListener {
            override fun after(events: MutableList<out VFileEvent>) {
               val file = fileEditorManager.selectedEditor?.file
               if (file != null) {
                  val files = events.mapNotNull { it.file }
                  val modified = files.firstOrNull { it.name == file.name }
                  if (modified != null)
                     refreshContent(modified)
               }
            }
         }
      )
   }

   private fun listenForSelectedEditorChanges() {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
               refreshContent()
            }
         }
      )
   }

   private fun refreshContent() {
      val file = fileEditorManager.selectedEditor?.file
      refreshContent(file)
   }

   private fun refreshContent(file: VirtualFile?) {

      if (file == null) {
         tree.model = emptyTreeModel()
      } else {
         val module = ModuleUtilCore.findModuleForFile(file, project)
         if (module == null) {
            tree.model = emptyTreeModel()
         } else {
            DumbService.getInstance(project).runWhenSmart {
               try {
                  val specs = PsiManager.getInstance(project).findFile(file)?.specs() ?: emptyList()
                  val model = treeModel(file, project, specs, module)
                  tree.model = model
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
      tree.isRootVisible = true
      tree.cellRenderer = NodeRenderer()
      return tree
   }
}
