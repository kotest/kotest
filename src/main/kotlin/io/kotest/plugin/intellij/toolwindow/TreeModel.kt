package io.kotest.plugin.intellij.toolwindow

import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.styles.FunSpecStyle
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.name.FqName
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
         val specObj = TreeNodeUserObject.Spec(spec, fqn, FunSpecStyle)
         val specNode = DefaultMutableTreeNode(specObj)
         root.add(specNode)
         val tests = FunSpecStyle.tests(spec)
         tests.forEach { testElement ->
            val testObj = TreeNodeUserObject.Test(testElement.psi, testElement.name)
            val testNode = DefaultMutableTreeNode(testObj)
            specNode.add(testNode)
         }
      }
   }
   return DefaultTreeModel(root)
}

sealed class TreeNodeUserObject {
   data class Spec(val psi: KtClassOrObject, val fqn: FqName, val style: SpecStyle) : TreeNodeUserObject()
   data class Test(val psi: PsiElement, val name: String) : TreeNodeUserObject()
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
