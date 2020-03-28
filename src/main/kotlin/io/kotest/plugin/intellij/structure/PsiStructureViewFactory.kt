package io.kotest.plugin.intellij.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import javax.swing.Icon

class KotestStructureViewFactory : PsiStructureViewFactory {
   override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
      return object : TreeBasedStructureViewBuilder() {
         override fun createStructureViewModel(editor: Editor?): StructureViewModel {
            return object : TextEditorBasedStructureViewModel(editor) {
               override fun getRoot(): StructureViewTreeElement {
                  return object : StructureViewTreeElement {

                     /**
                      * Should take us to the kotest test
                      */
                     override fun navigate(requestFocus: Boolean) {
                        psiFile.navigate(requestFocus);
                     }

                     override fun getPresentation(): ItemPresentation {
                        return object : ItemPresentation {
                           override fun getLocationString(): String? = null
                           override fun getIcon(unused: Boolean): Icon? = null
                           override fun getPresentableText(): String? {
                              return "foo"
                           }
                        }
                     }

                     override fun getChildren(): Array<TreeElement> {
                        return emptyArray()
                     }

                     /**
                      * Indicates whether this instance supports navigation to source (that means some kind of editor).
                      */
                     override fun canNavigateToSource(): Boolean = true
                     override fun canNavigate(): Boolean = true
                     override fun getValue(): Any = psiFile
                  }
               }
            }
         }
      }
   }
}
