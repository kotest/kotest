package com.sksamuel.kotest.eq

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EmbeddedListTest: StringSpec() {
   init {
      "should be the same" {
         val list1 = NamedList("list1", listOf("apple", "banana"))
         val list2 = NamedList("1tsil".reversed(), listOf("apple", "banana"))
         list1 shouldBe list2
      }
      "should be different" {
         val list1 = NamedList("list1", listOf("apple", "banana"))
         val list2 = NamedList("list2", listOf("apple", "banana"))
         list1 shouldNotBe list2
      }
   }

   private data class NamedList(
      val name: String,
      val list: List<String>,
   ) : List<String> by list
}
