package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.styles.TestElement
import io.kotest.plugin.intellij.styles.psi.specStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

fun treeModel(project: Project, specs: List<KtClassOrObject>): TreeModel {

   fun addTests(node: DefaultMutableTreeNode, parent: NodeDescriptor<Any>, tests: List<TestElement>) {
      tests.forEach { test ->
         val testDescriptor = TestNodeDescriptor(project, parent, test.psi, test)
         val testNode = DefaultMutableTreeNode(testDescriptor)
         node.add(testNode)
         addTests(testNode, testDescriptor, test.tests)
      }
   }

   val kotest = KotestNodeDescriptor(project)
   val root = DefaultMutableTreeNode(kotest)
   specs.forEach { spec ->
      val fqn = spec.getKotlinFqName()
      val style = spec.specStyle()
      if (fqn != null && style != null) {
         val specDescriptor = SpecNodeDescriptor(project, kotest, spec, fqn, style)
         val specNode = DefaultMutableTreeNode(specDescriptor)
         root.add(specNode)
         val tests = style.tests(spec)
         addTests(specNode, specDescriptor, tests)

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
