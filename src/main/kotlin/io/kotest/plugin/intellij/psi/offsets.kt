package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Returns the offsets for the given line in this file, or -1 if the document cannot be loaded for this file.
 *
 * Note: This method is 1 indexed, which tallies with the line numbers reported by the console runner.
 */
fun PsiFile.offsetForLine(line: Int): IntRange? {
   val doc = PsiDocumentManager.getInstance(project).getDocument(this) ?: return null
   return try {
      doc.getLineStartOffset(line - 1)..doc.getLineEndOffset(line - 1)
   } catch (e: Exception) {
      null
   }
}

fun PsiElement.findElementInRange(offsets: IntRange): PsiElement? {
   return offsets.asSequence()
      .mapNotNull { this.findElementAt(it) }
      .firstOrNull()
}

/**
 * Returns the element for the given line in this file, or null if the document cannot be loaded for this file.
 *
 * Note: This method is 1 indexed, which tallies with the line numbers reported by the console runner.
 */
fun PsiFile.elementAtLine(line: Int): PsiElement? =
   offsetForLine(line)?.let { findElementInRange(it) }
