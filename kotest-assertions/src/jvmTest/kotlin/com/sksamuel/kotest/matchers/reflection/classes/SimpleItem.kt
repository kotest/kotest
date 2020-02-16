package com.sksamuel.kotest.matchers.reflection.classes

class SimpleItem(val isReallySimple: Boolean = true) {

  companion object {
    const val id = 1
  }

  sealed class Action {
    object Action1 : Action()
    object Action2 : Action()
  }

  fun simpleFunction(): Int {
    return 1
  }

  inline fun run(block: () -> Unit) {
    block()
  }

  infix fun sum(num: Int) {

  }
}