package io.kotest.plugin.intellij.toolwindow

import com.intellij.facet.FacetManager
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.fqname
import io.kotest.plugin.intellij.psi.callbacks
import io.kotest.plugin.intellij.psi.includes
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

/**
 * Creates a [TreeModel] for the given [VirtualFile].
 */
fun createTreeModel(
   file: VirtualFile,
   project: Project,
   specs: List<KtClassOrObject>,
   module: Module
): TreeModel {

   val kotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)
   val kotest = KotestRootNodeDescriptor(project)
   val root = DefaultMutableTreeNode(kotest)

   fun addTests(
      node: DefaultMutableTreeNode,
      parent: NodeDescriptor<Any>,
      specDescriptor: SpecNodeDescriptor,
      tests: List<TestElement>
   ) {

      val groups = tests.groupBy { it.test.name }

      tests.forEach { test ->
         val isUnique = (groups[test.test.name]?.size ?: 0) < 2
         val testDescriptor = TestNodeDescriptor(project, parent, test.psi, test, specDescriptor, isUnique, module)
         val testNode = DefaultMutableTreeNode(testDescriptor)
         node.add(testNode)
         addTests(testNode, testDescriptor, specDescriptor, test.nestedTests)
      }
   }

   if (kotestTestExplorerService.showModules) {

      val allModulesDescriptor = ModulesNodeDescriptor(project)
      val allModulesNode = DefaultMutableTreeNode(allModulesDescriptor)
      root.add(allModulesNode)

      project.allModules()
         .filter { it.isKotlin() }
         .filter { it.name.endsWith("jvmTest") || it.name.endsWith("test") }
         .forEach {
            val moduleDescriptor = ModuleNodeDescriptor(it, project, allModulesDescriptor)
            val moduleNode = DefaultMutableTreeNode(moduleDescriptor)
            allModulesNode.add(moduleNode)
         }
   }

   if (kotestTestExplorerService.showTags) {

      val descriptor = TagsNodeDescriptor(project)
      val node = DefaultMutableTreeNode(descriptor)
      root.add(node)

      kotestTestExplorerService.tags.forEach {
         val tagDescriptor = TagNodeDescriptor(it, project, descriptor)
         val tagNode = DefaultMutableTreeNode(tagDescriptor)
         node.add(tagNode)
      }
   }

   val fileDescriptor = TestFileNodeDescriptor(file, project, kotest)
   val fileNode = DefaultMutableTreeNode(fileDescriptor)
   root.add(fileNode)

   specs.forEach { spec ->

      val fqn = spec.fqname()
      val style = spec.specStyle()
      if (fqn != null && style != null) {

         val specDescriptor = SpecNodeDescriptor(project, fileDescriptor, spec, fqn, style, module)
         val specNode = DefaultMutableTreeNode(specDescriptor)
         fileNode.add(specNode)

         if (kotestTestExplorerService.showCallbacks) {
            val callbacks = spec.callbacks()
            callbacks.forEach {
               val callbackDescriptor = CallbackNodeDescriptor(project, specDescriptor, it.psi, it)
               val callbackNode = DefaultMutableTreeNode(callbackDescriptor)
               specNode.add(callbackNode)
            }
         }

         if (kotestTestExplorerService.showIncludes) {
            val includes = spec.includes()
            includes.forEach {
               val includeDescriptor = IncludeNodeDescriptor(project, specDescriptor, it.psi, it)
               val includeNode = DefaultMutableTreeNode(includeDescriptor)
               specNode.add(includeNode)
            }
         }

         val tests = style.tests(spec, false)
         addTests(specNode, specDescriptor, specDescriptor, tests)
      }
   }

   return DefaultTreeModel(root)
}

fun Module.isKotlin(): Boolean = FacetManager.getInstance(this).allFacets.filterIsInstance<KotlinFacet>().isNotEmpty()
