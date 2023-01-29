package io.kotest.plugin.intellij

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.findExistingEditor

fun PsiElement.existingEditor() = this.findExistingEditor()
