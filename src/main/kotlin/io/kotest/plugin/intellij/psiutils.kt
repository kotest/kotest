package io.kotest.plugin.intellij

import com.intellij.psi.PsiElement

inline fun <reified T, U> PsiElement.matches(thunk: (T) -> U): U? {
   return if (this is T) {
      thunk(this)
   } else {
      null
   }
}
