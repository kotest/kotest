package io.kotest.plugin.intellij.psi

import com.intellij.execution.PsiLocation
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore

/**
 * Returns the offsets for the given line in this file, or -1 if the document cannot be loaded for this file.
 *
 * Note: This method is 1 indexed.
 */
fun PsiFile.offsetForLine(line: Int): IntRange? {
   val doc = PsiDocumentManager.getInstance(project).getDocument(this) ?: return null
   return try {
      doc.getLineStartOffset(line - 1)..doc.getLineEndOffset(line - 1)
   } catch (e: Exception) {
      null
   }
}

/**
 * Finds the first [PsiElement] for the given offset range by iterating over
 * the values in the range until an element that is not a whitespace is found.
 */
fun PsiElement.findElementInRange(offsets: IntRange): PsiElement? {
   return offsets.asSequence()
      .mapNotNull { offset -> findElementAt(offset)?.takeIf { it.text?.isNotBlank() == true } }
      .firstOrNull()
}
/**
 * Returns the first element for the given line in this file,
 * or null if the document cannot be loaded for this file.
 *
 * Note: This method is 1 indexed.
 */
fun PsiFile.elementAtLine(line: Int): PsiElement? =
   offsetForLine(line)?.let { findElementInRange(it) }

fun PsiElement.toPsiLocation() = PsiLocation(project, this)
