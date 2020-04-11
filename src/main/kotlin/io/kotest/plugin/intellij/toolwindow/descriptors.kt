package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Icons
import io.kotest.plugin.intellij.psi.Callback
import io.kotest.plugin.intellij.psi.CallbackType
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.TestElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotestNodeDescriptor(project: Project) : PresentableNodeDescriptor<Any>(project, null) {

   init {
      templatePresentation.presentableText = "Kotest"
      templatePresentation.setIcon(Icons.Kotest16)
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class SpecNodeDescriptor(project: Project,
                         val parent: NodeDescriptor<Any>,
                         val psi: KtClassOrObject,
                         val fqn: FqName,
                         val style: SpecStyle,
                         val module: Module) : PresentableNodeDescriptor<Any>(project, parent) {

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

class TestNodeDescriptor(project: Project,
                         val parent: NodeDescriptor<Any>,
                         val psi: PsiElement,
                         val test: TestElement,
                         val spec: SpecNodeDescriptor,
                         private val root: Boolean,
                         private val isUnique: Boolean, // if false then this test name is a duplicate
                         val module: Module) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.locationString = null
      templatePresentation.presentableText = test.test.name
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

class CallbackNodeDescriptor(project: Project,
                             parent: SpecNodeDescriptor,
                             val psi: PsiElement,
                             val callback: Callback) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = callback.type.text
      when (callback.type) {
         CallbackType.BeforeTest -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterTest -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.BeforeSpec -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
         CallbackType.AfterSpec -> templatePresentation.setIcon(AllIcons.Nodes.Controller)
      }
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}
