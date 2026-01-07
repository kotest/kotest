package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.TreeUIHelper
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

private data class FileTreeState(
   val allKeys: Set<String>,
   val expandedKeys: Set<String>,
   var initiallyExpanded: Boolean,
)

class TestFileTree(
   project: Project,
) : com.intellij.ui.treeStructure.Tree(),
   KotestTestExplorerService.ModelListener {

   private val testExplorerTreeSelectionListener = TestExplorerTreeSelectionListener(project)
   private val kotestTestExplorerService: KotestTestExplorerService =
      project.getService(KotestTestExplorerService::class.java)
   private var initialized = false
   private var lastFileKey: String? = null
   private val stateByFileKey = mutableMapOf<String, FileTreeState>()

   init {
      selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      showsRootHandles = true
      isRootVisible = false
      cellRenderer = NodeRenderer()
      // enable speed search like in the Project tool window, using presentable text
      TreeUIHelper.getInstance().installTreeSpeedSearch(this, { path ->
         val node = path.lastPathComponent as? DefaultMutableTreeNode
         val descriptor = node?.userObject as? PresentableNodeDescriptor<*>
         descriptor?.presentation?.presentableText ?: node?.userObject?.toString() ?: ""
      }, false)
      // listens to changes in the selections
      addTreeSelectionListener(testExplorerTreeSelectionListener)
      kotestTestExplorerService.registerModelListener(this)
      initialized = true
   }

   override fun setModel(treeModel: TreeModel) {
      if (!initialized) {
         super.setModel(treeModel)
         return
      }
      val newFileKey = currentFileKey()

      // If switching away from a file, save its state first
      if (lastFileKey != null && lastFileKey != newFileKey) {
         val prevAll = collectAllPathKeys()
         val prevExpanded = collectExpandedPathKeys()
         val prevInit = stateByFileKey[lastFileKey!!]?.initiallyExpanded ?: false
         stateByFileKey[lastFileKey!!] = FileTreeState(prevAll, prevExpanded, prevInit)
      }

      val sameFile = newFileKey == lastFileKey
      val prevStateForNew = if (newFileKey != null) stateByFileKey[newFileKey] else null
      val firstOpenForFile = newFileKey != null && prevStateForNew == null

      // Baselines (use live tree for same file; fallback to stored state when switching)
      val prevAllKeysForThisFile: Set<String> = when {
         firstOpenForFile -> emptySet()
         sameFile -> collectAllPathKeys()
         newFileKey != null -> prevStateForNew?.allKeys ?: emptySet()
         else -> emptySet()
      }
      val expandedKeysToRestore: Set<String> = when {
         firstOpenForFile -> emptySet()
         sameFile -> collectExpandedPathKeys()
         newFileKey != null -> prevStateForNew?.expandedKeys ?: emptySet()
         else -> emptySet()
      }

      super.setModel(treeModel)

      // Compute added nodes relative to the previous snapshot of this file (if any)
      val newAllKeys = collectAllPathKeys()
      if (!firstOpenForFile) {
         val addedKeys = newAllKeys - prevAllKeysForThisFile
         if (addedKeys.isNotEmpty()) expandAncestorPrefixesFor(addedKeys)
      }

      if (firstOpenForFile) {
         // First time this file is shown in the tool window: expand everything
         expandAllNodes()
         stateByFileKey[newFileKey] = FileTreeState(newAllKeys, collectExpandedPathKeys(), initiallyExpanded = true)
      } else {
         // Restore previous expansion state for this file
         if (expandedKeysToRestore.isNotEmpty()) expandPathsByKeys(expandedKeysToRestore)
         if (newFileKey != null) {
            val init = prevStateForNew?.initiallyExpanded ?: true
            stateByFileKey[newFileKey] = FileTreeState(newAllKeys, collectExpandedPathKeys(), init)
         }
      }

      lastFileKey = newFileKey
   }

   fun markFileClosed(file: VirtualFile) {
      stateByFileKey.remove(file.path)
      if (lastFileKey == file.path) lastFileKey = null
   }

   private fun currentFileKey(): String? = kotestTestExplorerService.currentFile?.path

}
