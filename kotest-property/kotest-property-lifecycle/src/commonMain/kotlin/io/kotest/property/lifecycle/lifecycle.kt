package io.kotest.property.lifecycle

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.BeforePropertyContextElement
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

abstract class BeforeAndAfterPropertyTestExtension : SpecExtension {

   abstract suspend fun beforeProperty()
   abstract suspend fun afterProperty()

   override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      withContext(BeforePropertyContextElement(::beforeProperty) + AfterPropertyContextElement(::afterProperty)) {
         process()
      }
   }
}

fun Spec.beforeProperty(f: suspend () -> Unit) {
   aroundTest { (testCase, execute) ->
      withContext(BeforePropertyContextElement(f)) {
         execute(testCase)
      }
   }
}

fun Spec.afterProperty(f: suspend () -> Unit) {
   aroundTest { (testCase, execute) ->
      withContext(AfterPropertyContextElement(f)) {
         execute(testCase)
      }
   }
}
