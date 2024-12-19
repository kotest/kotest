package io.kotest.matchers.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.LinkedList

class UtilsKtTest : FunSpec({
   context("ContainerName") {

      test("Should return 'List' for lists") {
         emptyList<Any>().containerName() shouldBe "List"
         arrayListOf(1, 2, 3).containerName() shouldBe "List"
         LinkedList<Any>().containerName() shouldBe "List"
      }

      test("Should return 'Set' for sets") {
         emptySet<Any>().containerName() shouldBe "Set"
         hashSetOf(1, 2, 3).containerName() shouldBe "Set"
         linkedSetOf<Any>().containerName() shouldBe "Set"
      }


      test("Should return 'Collection' for collections that are neither lists nor sets") {
         val customCollection = object : Collection<Any> {
            override val size = 0
            override fun contains(element: Any) = false
            override fun containsAll(elements: Collection<Any>) = false
            override fun isEmpty() = true
            override fun iterator() = emptyList<Any>().iterator()
         }
         customCollection.containerName() shouldBe "Collection"
      }

      test("Should return 'Iterable' for iterables that are not collections") {
         val customIterable = object : Iterable<Any> {
            override fun iterator() = listOf(1, 2, 3).iterator()
         }
         customIterable.containerName() shouldBe "Iterable"
      }
   }
})

