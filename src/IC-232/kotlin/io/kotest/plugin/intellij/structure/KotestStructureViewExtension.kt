package io.kotest.plugin.intellij.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewExtension
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.Icon

class KotestStructureViewExtension : StructureViewExtension {

   override fun getType(): Class<out PsiElement> {
      return KtClassOrObject::class.java
   }

   override fun getChildren(parent: PsiElement): Array<StructureViewTreeElement> {
      val ktClassOrObject = parent as? KtClassOrObject ?: return emptyArray()
      val spec = ktClassOrObject.specStyle() ?: return emptyArray()
      val tests = spec.tests(parent, false)
      return tests.map { KotestTestStructureViewTreeElement(it) }.toTypedArray()
   }

   class KotestTestStructureViewTreeElement(private val test: TestElement) : StructureViewTreeElement {
      override fun getPresentation(): ItemPresentation {
         return object : ItemPresentation {
            override fun getIcon(unused: Boolean): Icon {
               return AllIcons.Nodes.Test
            }

            override fun getPresentableText(): String {
               return test.test.name.displayName()
            }
         }
      }

      override fun getChildren(): Array<TreeElement> {
         return test.nestedTests.map { KotestTestStructureViewTreeElement(it) }.toTypedArray()
      }

      override fun navigate(requestFocus: Boolean) {
         if (test.psi is NavigatablePsiElement) {
            test.psi.navigate(true)
         }
      }

      override fun canNavigate(): Boolean {
         return true
      }

      override fun canNavigateToSource(): Boolean {
         return true
      }

      override fun getValue(): Any {
         return test
      }
   }

   override fun getCurrentEditorElement(editor: Editor?, parent: PsiElement?): Any? {
      return null
   }
}
