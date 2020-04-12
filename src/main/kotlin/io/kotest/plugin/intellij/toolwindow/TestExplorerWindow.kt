package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.ui.ScrollPaneFactory
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

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

      background = Color.WHITE
      toolbar = createToolbar(tree, project)
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
      tree.setVirtualFile(file)
   }
}
