package io.kotest.matchers.string

import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldStartWith(prefix: String): A? {
   this.toString() should startWith(prefix)
   return this
}

infix fun <A : CharSequence> A?.shouldNotStartWith(prefix: String): A? {
   this.toString() shouldNot startWith(prefix)
   return this
}
