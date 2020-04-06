package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Icons
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.TestElement
import io.kotest.plugin.intellij.styles.psi.Callback
import io.kotest.plugin.intellij.styles.psi.CallbackType
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
                         val module: Module) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = test.test.name
      when (test.test.enabled) {
         true -> templatePresentation.setIcon(AllIcons.Nodes.Test)
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
