package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.print
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class AsyncPrintTest : FunSpec({

   test("concurrent printing of cyclic lists should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..100).map { index ->
            async {
               repeat(10) {
                  val cyclicList = mutableListOf<Any?>()
                  cyclicList.add(cyclicList)
                  cyclicList.add("item-$index-$it")

                  shouldNotThrowAny {
                     val printed = cyclicList.print().value
                     printed shouldContain "(this ArrayList)"
                     printed shouldContain "item-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }

   test("concurrent printing of indirect cyclic lists should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..100).map { index ->
            async {
               repeat(10) {
                  val list1 = mutableListOf<Any?>()
                  val list2 = mutableListOf<Any?>()
                  list1.add(list2)
                  list1.add("list1-$index-$it")
                  list2.add(list1)
                  list2.add("list2-$index-$it")

                  shouldNotThrowAny {
                     val printed1 = list1.print().value
                     printed1 shouldContain "(this ArrayList)"
                     printed1 shouldContain "list1-$index-$it"

                     val printed2 = list2.print().value
                     printed2 shouldContain "(this ArrayList)"
                     printed2 shouldContain "list2-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }

   test("concurrent printing of cyclic maps should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..100).map { index ->
            async {
               repeat(10) {
                  val cyclicMap = mutableMapOf<String, Any?>()
                  cyclicMap["self"] = cyclicMap
                  cyclicMap["id"] = "map-$index-$it"

                  shouldNotThrowAny {
                     val printed = cyclicMap.print().value
                     printed shouldContain "(this LinkedHashMap)"
                     printed shouldContain "map-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }

   test("concurrent printing of indirect cyclic maps should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..100).map { index ->
            async {
               repeat(10) {
                  val map1 = mutableMapOf<String, Any?>()
                  val map2 = mutableMapOf<String, Any?>()
                  map1["ref"] = map2
                  map1["id"] = "map1-$index-$it"
                  map2["ref"] = map1
                  map2["id"] = "map2-$index-$it"

                  shouldNotThrowAny {
                     val printed1 = map1.print().value
                     printed1 shouldContain "(this LinkedHashMap)"
                     printed1 shouldContain "map1-$index-$it"

                     val printed2 = map2.print().value
                     printed2 shouldContain "(this LinkedHashMap)"
                     printed2 shouldContain "map2-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }

   test("concurrent printing of mixed cyclic structures should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..100).map { index ->
            async {
               repeat(10) {
                  val list = mutableListOf<Any?>()
                  val map = mutableMapOf<String, Any?>()
                  list.add(map)
                  list.add("list-$index-$it")
                  map["list"] = list
                  map["id"] = "map-$index-$it"

                  shouldNotThrowAny {
                     val printedList = list.print().value
                     printedList shouldContain "list-$index-$it"

                     val printedMap = map.print().value
                     printedMap shouldContain "map-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }

   test("concurrent printing of deeply nested cyclic structures should not interfere with each other") {
      withContext(Dispatchers.Default) {
         val jobs = (1..50).map { index ->
            async {
               repeat(5) {
                  val list1 = mutableListOf<Any?>()
                  val list2 = mutableListOf<Any?>()
                  val list3 = mutableListOf<Any?>()
                  list1.add(list2)
                  list1.add("l1-$index-$it")
                  list2.add(list3)
                  list2.add("l2-$index-$it")
                  list3.add(list1) // cycle back
                  list3.add("l3-$index-$it")

                  shouldNotThrowAny {
                     val printed = list1.print().value
                     printed shouldContain "(this ArrayList)"
                     printed shouldContain "l1-$index-$it"
                  }
               }
            }
         }
         jobs.awaitAll()
      }
   }
})

