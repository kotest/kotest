package io.kotest.matchers.string

import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldEndWith(suffix: String): A? {
   this.toString() should endWith(suffix)
   return this
}

infix fun <A : CharSequence> A?.shouldNotEndWith(suffix: String): A? {
   this.toString() shouldNot endWith(suffix)
   return this
}
