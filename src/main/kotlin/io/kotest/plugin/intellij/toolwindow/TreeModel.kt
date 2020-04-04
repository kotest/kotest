package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.styles.FunSpecStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

fun treeModel(project: Project, specs: List<KtClassOrObject>): TreeModel {
   val kotest = KotestNodeDescriptor(project)
   val root = DefaultMutableTreeNode(kotest)
   specs.forEach { spec ->
      val fqn = spec.getKotlinFqName()
      if (fqn != null) {
         val specDescriptor = SpecNodeDescriptor(project, kotest, spec, fqn, FunSpecStyle)
         val specNode = DefaultMutableTreeNode(specDescriptor)
         root.add(specNode)
         val tests = FunSpecStyle.tests(spec)
         tests.forEach { testElement ->
            val testObj = TestNodeDescriptor(project, specDescriptor, testElement.psi, testElement.name)
            val testNode = DefaultMutableTreeNode(testObj)
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
