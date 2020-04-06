package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.styles.TestElement
import io.kotest.plugin.intellij.styles.psi.callbacks
import io.kotest.plugin.intellij.styles.psi.specStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

fun emptyTreeModel(): TreeModel {
   val root = DefaultMutableTreeNode("<no specs detected>")
   return DefaultTreeModel(root)
}

fun treeModel(project: Project, specs: List<KtClassOrObject>, module: Module): TreeModel {

   fun addTests(node: DefaultMutableTreeNode,
                parent: NodeDescriptor<Any>,
                specDescriptor: SpecNodeDescriptor,
                tests: List<TestElement>) {
      tests.forEach { test ->
         val testDescriptor = TestNodeDescriptor(project, parent, test.psi, test, specDescriptor, module)
         val testNode = DefaultMutableTreeNode(testDescriptor)
         node.add(testNode)
         addTests(testNode, testDescriptor, specDescriptor, test.tests)
      }
   }

   val kotest = KotestNodeDescriptor(project)
   val root = DefaultMutableTreeNode(kotest)
   specs.forEach { spec ->

      val fqn = spec.getKotlinFqName()
      val style = spec.specStyle()
      if (fqn != null && style != null) {

         val specDescriptor = SpecNodeDescriptor(project, kotest, spec, fqn, style, module)
         val specNode = DefaultMutableTreeNode(specDescriptor)
         root.add(specNode)

         val callbacks = spec.callbacks()
         callbacks.forEach {
            val callbackDescriptor = CallbackNodeDescriptor(project, specDescriptor, it.psi, it)
            val callbackNode = DefaultMutableTreeNode(callbackDescriptor)
            specNode.add(callbackNode)
         }

         val tests = style.tests(spec)
         addTests(specNode, specDescriptor, specDescriptor, tests)
      }
   }
   return DefaultTreeModel(root)
}
