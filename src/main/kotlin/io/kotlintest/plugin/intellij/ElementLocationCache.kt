package io.kotlintest.plugin.intellij

import com.intellij.openapi.components.ProjectComponent
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.util.concurrent.ConcurrentHashMap

class ElementLocationCache : ProjectComponent {

  private val elements = ConcurrentHashMap<String, KtFile>()

  fun element(fqn: String, line: Int): PsiElement? {
    val file = elements[fqn] ?: return null
    val element = file.allChildren.find { it.startOffset <= line && line <= it.endOffset }
    return element ?: file
  }

  fun add(ktclass: KtClassOrObject) {
    elements[ktclass.fqName!!.asString()] = ktclass.containingKtFile
  }

}