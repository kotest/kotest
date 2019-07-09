package com.sksamuel.kotlintest.matchers.reflection.classes

class SimpleItem(val isReallySimple: Boolean = true) {

  companion object {
    val id = 1
  }

  sealed class Action {
    object Action1 : Action()
    object Action2 : Action()
  }

  fun simpleFunction(): Int {
    return 1
  }
}