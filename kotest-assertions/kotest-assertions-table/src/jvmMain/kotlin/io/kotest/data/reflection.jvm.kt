package io.kotest.data

import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

actual fun paramNames(fn: Function<*>): List<String> {
   @OptIn(ExperimentalReflectionOnLambdas::class)
   val fnReflect = fn.reflect()
   return fnReflect?.parameters?.mapNotNull { it.name } ?: emptyList()
}
