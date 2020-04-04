package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Icons
import io.kotest.plugin.intellij.styles.SpecStyle
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
                         val style: SpecStyle) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = fqn.asString()
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}

class TestNodeDescriptor(project: Project,
                         val parent: NodeDescriptor<Any>,
                         val psi: PsiElement,
                         val testName: String) : PresentableNodeDescriptor<Any>(project, parent) {

   init {
      templatePresentation.presentableText = testName
   }

   override fun update(presentation: PresentationData) {
      presentation.isChanged = false
   }

   override fun getElement(): Any = this
}
