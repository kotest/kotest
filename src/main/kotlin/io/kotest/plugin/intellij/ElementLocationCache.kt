package io.kotest.plugin.intellij

import com.intellij.openapi.components.ProjectComponent
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.core.util.getLineStartOffset
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap

class ElementLocationCache : ProjectComponent {

  private val elements = ConcurrentHashMap<String, KtFile>()

  fun element(fqn: String, line: Int): PsiElement? {
    val file = elements[fqn] ?: return null
    val offset = file.getLineStartOffset(line) ?:-1
    return file.findElementAt(offset) ?: file
  }

  fun add(ktclass: KtClassOrObject) {
    elements[ktclass.fqName!!.asString()] = ktclass.containingKtFile
  }

}