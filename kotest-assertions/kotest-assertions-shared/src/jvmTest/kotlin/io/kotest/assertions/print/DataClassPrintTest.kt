package io.kotest.assertions.print

data class Foo(var other: Foo?) {
   override fun hashCode(): Int = 0
   override fun equals(other: Any?): Boolean = false
   override fun toString(): String = "foo"
}

data class Bar(val a: Int, val list: MutableList<Bar>) {
   override fun hashCode(): Int = 0
   override fun equals(other: Any?): Boolean = false
   override fun toString(): String = "bar"
}
