package io.kotest.plugin.intellij

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement

fun PsiClass.elementAtLine(line: Int, doc: Document): PsiElement? {
   println("Finding element at line $line in $doc")
   val start = doc.getLineStartOffset(line)
   val end = doc.getLineEndOffset(line)
   println("Looking for $start to $end")
   fun PsiElement.elementAtLine(): PsiElement? {
      println("Testing element ${this::class.java.simpleName} for offset $textOffset ${textOffset in start..end}")
      // if the start offset of this element is inside the line range then we can use that to
      // navigate to, as intellij navigates to the elements start offset
      if (textOffset in start..end) return this
      println("Children count ${children.size}")
      this.children.forEach { child ->
         val atLine = child.elementAtLine()
         if (atLine != null)
            return atLine
      }
      return null
   }
   return elementAtLine()
}
