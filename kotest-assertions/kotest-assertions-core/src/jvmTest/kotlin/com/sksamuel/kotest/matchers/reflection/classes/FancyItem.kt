package com.sksamuel.kotest.matchers.reflection.classes

import com.sksamuel.kotest.matchers.reflection.annotations.Fancy
import kotlinx.coroutines.delay

@Fancy(cost = 500)
abstract class FancyItem {

  data class FancyData(val someString: String, val someBoolean: Boolean)

  open val name: String = "Fancy Item Name"
  open protected val value: Int = 100
  private val otherField: Long = 10
  private lateinit var youLate: String

  @Fancy
  fun fancyFunction(@Fancy fancyValue: Int): Int {
    return 1
  }

  @Deprecated("Use fancyFunction instead")
  protected open fun fancyFunctionWithString(@Fancy fancyStringValue: String): String {
    return "test"
  }

  abstract fun absFun()

  suspend fun suspendFun() {
    delay(500)
  }
}