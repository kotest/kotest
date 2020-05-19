package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.core.util.getLineCount

/**
 * Returns the offsets for the given line in this file, or -1 if the document cannot be loaded for this file.
 *
 * Note: This method is 1 indexed, which tallies with the line numbers reported by the console runner.
 */
fun PsiFile.offsetForLine(line: Int): IntRange? {
   return if (this.getLineCount() >= line) null else
      PsiDocumentManager.getInstance(project).getDocument(this)?.let {
         it.getLineStartOffset(line - 1)..it.getLineEndOffset(line - 1)
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
