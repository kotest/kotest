package com.sksamuel.kotest.eq

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EmbeddedMapTest: StringSpec() {
   init {
      "should be the same" {
         val map1 = NamedMap("map1", mapOf("foo" to "bar"))
         val map2 = NamedMap("1pam".reversed(), mapOf("foo" to "bar"))
         map1 shouldBe map2
      }
      "should be different" {
         val map1 = NamedMap("map1", mapOf("foo" to "bar"))
         val map2 = NamedMap("map2", mapOf("foo" to "bar"))
         map1 shouldNotBe map2
      }
   }

   private data class NamedMap(
      val name: String,
      val map: Map<String, String>,
   ) : Map<String, String> by map
}
