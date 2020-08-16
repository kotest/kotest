package io.kotest.extensions.junit5

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtensionContext

class KotestExtensionContextTest : WordSpec({

   "ExtensionContext" should {
      "return value when type is of the required type" {
         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val a = Person("foo", 20)

         store.put("foo", a)
         val b = store.get("foo", Person::class.java)

         a shouldBe b
      }
      "throw when value is not of the required type" {

         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val person = Person("foo", 20)

         store.put("foo", person)

         shouldThrowAny {
            store.get("foo", Vehicle::class.java)
         }
      }
   }
})

data class Person(val name: String, val age: Int)
data class Vehicle(val name: String, val model: String)
