package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter
import com.intellij.ui.JBColor
import com.intellij.ui.PopupHandler
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.actions.RunAction
import io.kotest.plugin.intellij.actions.runNode
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

/**
 * The main panel for the test explorer 'tool window'.
 *
 * A [ToolWindow] is one of the side views that intellij provides, like the project view or the run view.
 * Kotest provides a tool window that shows the tests in a tree view.
 */
class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {
   val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   private val fileEditorManager = FileEditorManager.getInstance(project)
   private val tree = TestFileTree(project)

   init {

      // run the test at the node on a double click
      tree.addMouseListener(object : MouseAdapter() {
         override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2) {
               runNode(tree, project, "Run", false)
            }
         }
      })

      background = JBColor.WHITE
      toolbar = createToolbar(this, tree, project)
      setContent(ScrollPaneFactory.createScrollPane(tree))

      // install context menu with Run/Debug/Coverage on right-click
      addContextMenuForTests()

      listenForSelectedEditorChanges()
      listenForFileChanges()
      listenForDocumentChanges()
      refreshContent()
   }

   private fun addContextMenuForTests() {
      val popupGroup = DefaultActionGroup().apply {
         add(RunAction("Run", AllIcons.Actions.Execute, tree, project, "Run"))
         add(RunAction("Debug", AllIcons.Actions.StartDebugger, tree, project, "Debug"))
         add(RunAction("Run with coverage", AllIcons.General.RunWithCoverage, tree, project, "Coverage"))
      }
      tree.addMouseListener(object : PopupHandler() {
         override fun invokePopup(component: Component, x: Int, y: Int) {
            // select the row under cursor so actions act on that node
            tree.getPathForLocation(x, y)?.let { tree.selectionPath = it }
            val popup = ActionManager.getInstance().createActionPopupMenu(
               ActionPlaces.getPopupPlace("KotestTestTreePopup"), popupGroup
            )
            popup.component.show(component, x, y)
         }
      })
   }

   private fun listenForFileChanges() {
      project.messageBus.connect().subscribe(
         VirtualFileManager.VFS_CHANGES,
         object : BulkFileListener {
            override fun after(events: MutableList<out VFileEvent>) {
               val selectedFile = fileEditorManager.selectedEditor?.file
               if (selectedFile != null) {
                  val files = events.mapNotNull { it.file }
                  val modified = files.firstOrNull { it.name == selectedFile.name }
                  if (modified != null)
                     kotestTestExplorerService.currentFile = modified
               }
            }
         }
      )
   }

   /**
    * Listens for [FileEditorManagerEvent]s that are fired whenever the open editor changes (eg by opening
    * a new file or tabbing into an already open file)
    */
   private fun listenForSelectedEditorChanges() {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
               val file = fileEditorManager.selectedEditor?.file
               if (file != null) {
                  kotestTestExplorerService.currentFile = file
               }
            }
         }
      )
   }

   private fun listenForDocumentChanges() {
      val listener = object : PsiTreeAnyChangeAbstractAdapter() {
         override fun onChange(file: PsiFile?) {
            if (file != null) {
               val selectedFile = fileEditorManager.selectedEditor?.file
               if (selectedFile != null) {
                  if (file.virtualFile.name == selectedFile.name) {
                     kotestTestExplorerService.currentFile = file.virtualFile
                  }
               }
            }
         }
      }
      val manager = PsiManager.getInstance(project)
      manager.addPsiTreeChangeListener(listener) { manager.removePsiTreeChangeListener(listener) }

      val tagsListener = KotestTagFileListener(tree, project)
      manager.addPsiTreeChangeListener(tagsListener) { manager.removePsiTreeChangeListener(tagsListener) }
   }

   private fun refreshContent() {
      kotestTestExplorerService.scanTags()
      fileEditorManager.selectedEditor?.file?.let {
         kotestTestExplorerService.currentFile = it
      }
   }
}
