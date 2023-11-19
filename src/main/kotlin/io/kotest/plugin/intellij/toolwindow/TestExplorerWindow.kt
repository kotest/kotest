package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter
import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.actions.runNode
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

/**
 * The main panel for the test explorer 'tool window'.
 */
class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {

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
      toolbar = createToolbar(tree, project)
      setContent(ScrollPaneFactory.createScrollPane(tree))
      listenForSelectedEditorChanges()
      listenForFileChanges()
      listenForDocumentChanges()
      refreshContent()
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
                     tree.setVirtualFile(modified)
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
                  tree.setVirtualFile(file)
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
                     tree.setVirtualFile(file.virtualFile)
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
      scanTags(project)
      val file = fileEditorManager.selectedEditor?.file
      tree.setVirtualFile(file)
   }
}
