package com.sksamuel.kotlintest.matchers.reflection.classes

import com.sksamuel.kotlintest.matchers.reflection.annotations.Fancy

@Fancy(cost = 500)
open class FancyItem {

  open val name: String = "Fancy Item Name"
  open protected val value: Int = 100
  private val otherField: Long = 10

  @Fancy
  fun fancyFunction(@Fancy fancyValue: Int): Int {
    return 1
  }

  @Deprecated("Use fancyFunction instead")
  protected open fun fancyFunctionWithString(@Fancy fancyValue: String): String {
    return "test"
  }
}