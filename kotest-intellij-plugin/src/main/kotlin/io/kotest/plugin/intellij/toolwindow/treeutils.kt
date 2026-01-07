package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

fun JTree.expandAllNodes() = expandAllNodes(0, rowCount)

fun JTree.expandAllNodes(startingIndex: Int, rowCount: Int) {
   for (i in startingIndex until rowCount) {
      expandRow(i)
   }
   if (getRowCount() != rowCount) {
      expandAllNodes(rowCount, getRowCount())
   }
}

@Suppress("UNCHECKED_CAST")
fun JTree.collapseTopLevelNodes() {
   val root = model.root as DefaultMutableTreeNode
   for (node in root.children().toList() as List<DefaultMutableTreeNode>) {
      val path = TreePath(node.path)
      this.collapsePath(path)
   }
}

fun TreePath.nodeDescriptor(): PresentableNodeDescriptor<*>? {
   return when (val last = lastPathComponent) {
      is DefaultMutableTreeNode -> when (val obj = last.userObject) {
         is PresentableNodeDescriptor<*> -> obj
         else -> null
      }
      else -> null
   }
}

// --- Expansion state helpers ---

/**
 * Builds a logical key for a node to preserve expansion state across model rebuilds.
 * Only nodes that can have children are keyed (root, modules, module, tags, file, spec, container test).
 */
private fun DefaultMutableTreeNode.expansionKeyOrNull(): String? {
   return when (val descriptor = userObject) {
      is KotestRootNodeDescriptor -> "root"
      is ModulesNodeDescriptor -> "modules"
      is ModuleNodeDescriptor -> "module:${descriptor.module.name}"
      is TagsNodeDescriptor -> "tags"
      is TestFileNodeDescriptor -> "file"
      is SpecNodeDescriptor -> "spec:${descriptor.fqn.asString()}"
      is TestNodeDescriptor -> {
         // Only container tests can have children; still safe to key all tests
         "test:${descriptor.test.test.descriptorPath()}"
      }
      else -> null
   }
}

/**
 * Returns a set of expansion path keys for the currently expanded nodes.
 */
fun JTree.collectExpandedPathKeys(): Set<String> {
   val root = model.root as? DefaultMutableTreeNode ?: return emptySet()
   val expanded = mutableSetOf<String>()
   // Enumerate all expanded descendants starting from root
   val enumeration = getExpandedDescendants(TreePath(root.path)) ?: return emptySet()
   while (enumeration.hasMoreElements()) {
      val path = enumeration.nextElement()
      val key = pathToExpansionKey(path)
      if (key != null) expanded.add(key)
   }
   return expanded
}

/**
 * Expands nodes in the current model whose logical expansion keys appear in [keys].
 */
fun JTree.expandPathsByKeys(keys: Set<String>) {
   val root = model.root as? DefaultMutableTreeNode ?: return
   fun recurse(node: DefaultMutableTreeNode, prefix: String?) {
      val key = node.expansionKeyOrNull()
      val pathKey = if (key == null) prefix else listOfNotNull(prefix, key).joinToString("/")
      if (pathKey != null && keys.contains(pathKey)) {
         expandPath(TreePath(node.path))
      }
      val children = node.children()
      while (children.hasMoreElements()) {
         val child = children.nextElement() as DefaultMutableTreeNode
         recurse(child, pathKey)
      }
   }
   recurse(root, null)
}

private fun pathToExpansionKey(path: TreePath): String? {
   val parts = path.path
      .mapNotNull { it as? DefaultMutableTreeNode }
      .mapNotNull { it.expansionKeyOrNull() }
   return if (parts.isEmpty()) null else parts.joinToString("/")
}

/**
 * Returns a set of path keys for all nodes in the current model.
 */
fun JTree.collectAllPathKeys(): Set<String> {
   val root = model.root as? DefaultMutableTreeNode ?: return emptySet()
   val keys = mutableSetOf<String>()
   fun recurse(node: DefaultMutableTreeNode) {
      val path = TreePath(node.path)
      val key = pathToExpansionKey(path)
      if (key != null) keys.add(key)
      val children = node.children()
      while (children.hasMoreElements()) {
         recurse(children.nextElement() as DefaultMutableTreeNode)
      }
   }
   recurse(root)
   return keys
}

/**
 * Expands all ancestor prefixes for the given full path keys.
 */
fun JTree.expandAncestorPrefixesFor(keys: Set<String>) {
   if (keys.isEmpty()) return
   val toExpand = mutableSetOf<String>()
   keys.forEach { key ->
      val parts = key.split('/')
      val acc = mutableListOf<String>()
      parts.forEach { part ->
         acc.add(part)
         toExpand.add(acc.joinToString("/"))
      }
   }
   expandPathsByKeys(toExpand)
}
