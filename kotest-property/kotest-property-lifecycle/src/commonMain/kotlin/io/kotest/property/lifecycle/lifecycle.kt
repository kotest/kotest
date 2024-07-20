package io.kotest.property.lifecycle

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.BeforePropertyContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass

interface BeforeAndAfterPropertyTestInterceptExtension : SpecExtension {

   suspend fun beforeProperty()
   suspend fun afterProperty()

   @Deprecated("See `SpecExtension.intercept(spec: KClass<out Spec>, process: suspend () -> Unit)`")
   override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      val before = coroutineContext[BeforePropertyContextElement]?.before
      val after = coroutineContext[AfterPropertyContextElement]?.after
      withContext(AfterPropertyContextElement {
         after?.invoke()
         afterProperty()
      } + BeforePropertyContextElement {
         before?.invoke()
         beforeProperty()
      }) {
         process()
      }
   }
}

fun Spec.beforeProperty(f: suspend () -> Unit) {
   extension(object : BeforeAndAfterPropertyTestInterceptExtension {
      override suspend fun beforeProperty() {
         f()
      }

      override suspend fun afterProperty() {}
   })
}

fun Spec.afterProperty(f: suspend () -> Unit) {
   extension(object : BeforeAndAfterPropertyTestInterceptExtension {
      override suspend fun beforeProperty() {}
      override suspend fun afterProperty() {
         f()
      }
   })
}
