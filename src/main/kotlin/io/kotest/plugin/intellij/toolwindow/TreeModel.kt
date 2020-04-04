package io.kotest.plugin.intellij.toolwindow

import io.kotest.plugin.intellij.styles.FunSpecStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

fun treeModel(specs: List<KtClassOrObject>): TreeModel {
   val root = DefaultMutableTreeNode("Kotest")
   specs.forEach { spec ->
      val fqn = spec.getKotlinFqName()
      if (fqn != null) {
         println("$fqn")
         val specNode = DefaultMutableTreeNode(fqn.asString())
         root.add(specNode)
         val tests = FunSpecStyle.tests(spec)
         println("$tests")
         tests.forEach {
            val testNode = DefaultMutableTreeNode(it.name)
            specNode.add(testNode)
         }
      }
   }
   return DefaultTreeModel(root)
}

fun JTree.expandAllNodes() = expandAllNodes(0, rowCount)

fun JTree.expandAllNodes(startingIndex: Int, rowCount: Int) {
   for (i in startingIndex until rowCount) {
      expandRow(i)
   }
   if (getRowCount() != rowCount) {
      expandAllNodes(rowCount, getRowCount())
   }
}
