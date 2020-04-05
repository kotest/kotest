package io.kotest.plugin.intellij

import com.intellij.psi.PsiElement

inline fun <reified T> PsiElement.matches(thunk: (T) -> Boolean): Boolean {
   return if (this is T) {
      thunk(this)
   } else {
      false
   }
}


inline fun <reified T, U> PsiElement.map(thunk: (T) -> U): U? {
   return if (this is T) {
      thunk(this)
   } else {
      null
   }
}
