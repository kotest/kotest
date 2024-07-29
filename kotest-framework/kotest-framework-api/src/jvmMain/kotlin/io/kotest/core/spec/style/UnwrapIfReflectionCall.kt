package io.kotest.core.spec.style

import java.lang.reflect.InvocationTargetException

/**
 * In some particular cases, such as AnnotationSpec, a call will be made using Reflection.
 * When using reflection, any error will be wrapped around a InvocationTargetException, as explained
 * in https://stackoverflow.com/questions/6020719/what-could-cause-java-lang-reflect-invocationtargetexception
 * By verifying if this is an InvocationTargetException, we can unwrap it and throw the cause instead
 */
fun Throwable.unwrapIfReflectionCall(): Throwable {
   return when (this) {
      is InvocationTargetException -> cause ?: this
      else -> this
   }
}
