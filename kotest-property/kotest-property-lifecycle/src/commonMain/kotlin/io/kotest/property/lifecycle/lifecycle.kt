package io.kotest.property.lifecycle

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.BeforePropertyContextElement
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

interface BeforeAndAfterPropertyTestExtension : SpecExtension {

   suspend fun beforeProperty()
   suspend fun afterProperty()

   override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      withContext(BeforePropertyContextElement(::beforeProperty) + AfterPropertyContextElement(::afterProperty)) {
         process()
      }
   }
}

fun Spec.beforeProperty(f: suspend () -> Unit) {
   extension(object : BeforeAndAfterPropertyTestExtension {
      override suspend fun beforeProperty() { f() }
      override suspend fun afterProperty() {}
   })
}

fun Spec.afterProperty(f: suspend () -> Unit) {
   extension(object : BeforeAndAfterPropertyTestExtension {
      override suspend fun beforeProperty() {}
      override suspend fun afterProperty() { f() }
   })
}
