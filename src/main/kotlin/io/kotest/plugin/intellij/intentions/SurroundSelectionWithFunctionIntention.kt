package io.kotest.plugin.intellij.intentions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

abstract class SurroundSelectionWithFunctionIntention : TestSourceOnlyIntentionAction(), DumbAware {

   override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
      return isTestSource(element)
   }

   abstract val importFQN: FqName
   abstract val function: String

   override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
      if (editor == null) return
      addPsi(project, editor, element)
   }

   private fun addPsi(project: Project, editor: Editor, element: PsiElement) {
      val docManager = PsiDocumentManager.getInstance(project)
      val ktfactory = KtPsiFactory(project)

      try {

         val selection = editor.selectionModel
         if (selection.hasSelection() == true) {

            val file = element.containingFile
            if (file is KtFile) {

               val line1 = editor.document.getLineNumber(selection.selectionStart)
               val linen = editor.document.getLineNumber(selection.selectionEnd)

               // if our end position is column 0, then we've selected a full line - intellij wraps this onto the next line for some reason
               //  val lineN0 = if (selection.selectionEndPosition?.column == 0) linen - 1 else linen

               // expand the text range to include the full lines of the selection
               val lineStart = editor.document.getLineStartOffset(line1)
               val lineEnd = editor.document.getLineEndOffset(linen)
               val lineRange = TextRange(lineStart, lineEnd)
               val text = editor.document.getText(lineRange)

               // we need to work out how indented the first line was, so we can ident the function name the same amount
               val whitespacePrefix = text.takeWhile { it.isWhitespace() }

               // pad each of the original lines to include some extra padding as it will be further indented
               // 4 spaces seems to be what most kotlin files use but I like 2 :)
               val paddedStatements = text.split('\n').joinToString("\n") { "  $it" }

               // create a new string containing the wrapping function and the now-padded original statements
               val wrapped = "$whitespacePrefix$function {\n$paddedStatements\n$whitespacePrefix}"

               // place the new block at the position of the original lines
               editor.document.replaceString(lineStart, lineEnd, wrapped)
               docManager.commitDocument(editor.document)

               // best add the import if needed for the function
               val importPath = ImportPath(importFQN, false)
               val list = file.importList
               if (list != null) {
                  if (list.imports.none { it.importPath == importPath }) {
                     val imp = ktfactory.createImportDirective(importPath)
                     list.add(imp)
                  }
               }

               docManager.doPostponedOperationsAndUnblockDocument(editor.document)
            }
         }
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }
}
