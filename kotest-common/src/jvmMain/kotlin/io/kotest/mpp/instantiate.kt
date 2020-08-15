package io.kotest.mpp

import io.kotest.fp.Try

fun <T> instantiate(klass: Class<T>): Try<T> = Try {
   when (val field = klass.declaredFields.find { it.name == "INSTANCE" }) {
      // if the static field for an object cannot be found, then instantiate
      null -> {
         val zeroArgsConstructor = klass.constructors.find { it.parameterCount == 0 }
            ?: throw IllegalArgumentException("Class ${klass.name} should have a zero-arg constructor")
         zeroArgsConstructor.newInstance() as T
      }
      // if the static field can be found then use it
      else -> field.get(null) as T
   }
}
