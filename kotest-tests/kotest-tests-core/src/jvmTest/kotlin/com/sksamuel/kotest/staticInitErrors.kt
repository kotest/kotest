package com.sksamuel.kotest

// these classes have failing init blcks.
class Foo1 {
   companion object {
      init {
         error("boom")
      }
   }
}


class Foo2 {
   class Bar {
      companion object {
         private val x: Nothing = error("boom")
      }
   }
}
