package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.Icons
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.psi.Callback
import io.kotest.plugin.intellij.psi.CallbackType
import io.kotest.plugin.intellij.psi.Include
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotestRootNodeDescriptor(project: Project) : PresentableNodeDescriptor<Any>(project, null) {
   init {
      templatePresentation.presentableText = Constants.FRAMEWORK_NAME
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class ModulesNodeDescriptor(project: Project) : PresentableNodeDescriptor<Any>(project, null) {
   init {
      templatePresentation.presentableText = "Modules"
      templatePresentation.setIcon(AllIcons.Nodes.ModuleGroup)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class TagsNodeDescriptor(project: Project) : PresentableNodeDescriptor<Any>(project, null) {
   init {
      templatePresentation.presentableText = "Tags"
      templatePresentation.setIcon(AllIcons.Nodes.Tag)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class TestFileNodeDescriptor(
   file: VirtualFile,
   project: Project,
   parent: NodeDescriptor<Any>
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = file.name
      templatePresentation.setIcon(Icons().Kotest16Grey)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class SpecNodeDescriptor(
   project: Project,
   val parent: NodeDescriptor<Any>,
   val psi: KtClassOrObject,
   val fqn: FqName,
   val style: SpecStyle,
   val module: Module
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = fqn.asString()
      templatePresentation.locationString = style.specStyleName()
      templatePresentation.setIcon(AllIcons.Nodes.TestGroup)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class TestNodeDescriptor(
   project: Project,
   val parent: NodeDescriptor<Any>,
   val psi: PsiElement,
   val test: TestElement,
   val spec: SpecNodeDescriptor,
   isUnique: Boolean, // if false then this test name is a duplicate
   val module: Module
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.locationString = null
      templatePresentation.presentableText = test.test.name.displayName()
      when (test.test.enabled) {
         true -> when (isUnique) {
            true -> templatePresentation.setIcon(AllIcons.Nodes.Test)
            false -> {
               templatePresentation.setIcon(AllIcons.Nodes.ErrorIntroduction)
               templatePresentation.locationString = "duplicate test name"
            }
         }
         false -> templatePresentation.setIcon(AllIcons.Nodes.TestIgnored)
      }
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class CallbackNodeDescriptor(
   project: Project,
   parent: SpecNodeDescriptor,
   val psi: PsiElement,
   callback: Callback
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = callback.type.text
      when (callback.type) {
         CallbackType.BeforeTest -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterTest -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.BeforeSpec -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterSpec -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.BeforeContainer -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterContainer -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.BeforeEach -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterEach -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.BeforeAny -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterAny -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
      }
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

/**
 * [NodeDescriptor] for an individual module in the project.
 */
class ModuleNodeDescriptor(
   val module: Module,
   project: Project,
   parent: NodeDescriptor<Any>
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = module.name
      templatePresentation.setIcon(AllIcons.Nodes.Module)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

/**
 * [NodeDescriptor] for a detected kotest tag.
 */
class TagNodeDescriptor(
   tag: String,
   project: Project,
   parent: NodeDescriptor<Any>
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = tag
      templatePresentation.setIcon(AllIcons.Nodes.Tag)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class IncludeNodeDescriptor(
   project: Project,
   parent: SpecNodeDescriptor,
   val psi: PsiElement,
   val include: Include
) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = "Include: ${include.name}"
      templatePresentation.locationString = include.type.name
      templatePresentation.setIcon(AllIcons.Nodes.Tag)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}
