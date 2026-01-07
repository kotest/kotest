package com.sksamuel.foo

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.behaviorSpec
import io.kotest.core.spec.style.funSpec

fun aTestFactoryFunction() = funSpec {

}

val aTestFactionValue = behaviorSpec {  }
val bTestFactionValue = behaviorSpec {  }
val cTestFactionValue = behaviorSpec {  }

fun dTestFactoryFunction() = funSpec {

}

val myListener = object : TestListener {

   override suspend fun beforeSpec(spec: Spec) {
   }

   override suspend fun afterSpec(spec: Spec) {
   }
}
