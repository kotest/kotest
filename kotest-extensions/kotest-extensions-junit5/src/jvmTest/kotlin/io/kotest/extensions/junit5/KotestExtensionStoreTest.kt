package io.kotest.extensions.junit5

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtensionContext

class KotestExtensionStoreTest : WordSpec({

   "ExtensionContext" should {
      "return value when type is of the required type" {
         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val a = Person("person", 20)

         store.put("person", a)
         val b = store.get("person", Person::class.java)

         a shouldBe b
      }

      "throw when value is not of the required type" {
         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val person = Person("person", 20)

         store.put("person", person)

         shouldThrowAny {
            store.get("person", Vehicle::class.java)
         }
      }

      "return value when type is of the removing type" {
         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val a = Person("person", 20)
         store.put("person", a)

         val b = store.remove("person", Person::class.java)
         a shouldBe b
      }

      "compute when requested type is absent"{
         val store = ExtensionStore(ExtensionContext.Namespace.GLOBAL)
         val a = Person("person", 20)

         val b = store.getOrComputeIfAbsent("person") { a }

         a shouldBe b
      }
   }
})

data class Person(val name: String, val age: Int)
data class Vehicle(val name: String, val model: String)
