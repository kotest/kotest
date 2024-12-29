package io.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class NullsMatchersTest : FunSpec({

   context("shouldContainOnlyNulls") {

      context("on Iterable") {
         test("should pass when all elements are null") {
            val list = listOf(null, null, null)
            list.shouldContainOnlyNulls()
         }

         test("should fail when any element is not null") {
            val list = listOf(null, "not null", null)
            shouldThrow<AssertionError> { list.shouldContainOnlyNulls() }
         }

         test("should pass for an empty iterable") {
            val list = emptyList<String?>()
            list.shouldContainOnlyNulls()
         }
      }

      context("on Array") {
         test("should pass when all elements are null") {
            val array = arrayOf<String?>(null, null, null)
            array.shouldContainOnlyNulls()
         }

         test("should fail when any element is not null") {
            val array = arrayOf(null, "not null", null)
            shouldThrow<AssertionError> { array.shouldContainOnlyNulls() }
         }

         test("should pass for an empty array") {
            val array = emptyArray<String?>()
            array.shouldContainOnlyNulls()
         }
      }

      context("on Collection") {
         test("should pass when all elements are null") {
            val collection: Collection<String?> = listOf(null, null, null)
            collection.shouldContainOnlyNulls()
         }

         test("should fail when any element is not null") {
            val collection: Collection<String?> = listOf(null, "not null", null)
            shouldThrow<AssertionError> {
               collection.shouldContainOnlyNulls()
            }
         }

         test("should pass for an empty collection") {
            val collection: Collection<String?> = emptyList()
            collection.shouldContainOnlyNulls()
         }
      }
   }

   context("shouldNotContainOnlyNulls") {

      context("on Iterable") {
         test("should pass when at least one element is not null") {
            val iterable: Iterable<String?> = listOf(null, "not null", null)
            iterable.shouldNotContainOnlyNulls()
         }

         test("should fail when all elements are null") {
            val iterable: Iterable<String?> = listOf(null, null, null)
            shouldThrow<AssertionError> { iterable.shouldNotContainOnlyNulls() }
         }

         test("should fail for an empty iterable") {
            val iterable: Iterable<String?> = emptyList()
            shouldThrow<AssertionError> { iterable.shouldNotContainOnlyNulls() }
         }
      }

      context("on Array") {
         test("should pass when at least one element is not null") {
            val array = arrayOf(null, "not null", null)
            array.shouldNotContainOnlyNulls()
         }

         test("should fail when all elements are null") {
            val array = arrayOf<String?>(null, null, null)
            shouldThrow<AssertionError> { array.shouldNotContainOnlyNulls() }
         }

         test("should fail for an empty array") {
            val array = emptyArray<String?>()
            shouldThrow<AssertionError> { array.shouldNotContainOnlyNulls() }
         }
      }

      context("on Collection") {
         test("should pass when at least one element is not null") {
            val collection: Collection<String?> = listOf(null, "not null", null)
            collection.shouldNotContainOnlyNulls()
         }

         test("should fail when all elements are null") {
            val collection: Collection<String?> = listOf(null, null, null)
            shouldThrow<AssertionError> { collection.shouldNotContainOnlyNulls() }
         }

         test("should fail for an empty collection") {
            val collection: Collection<String?> = emptyList()
            shouldThrow<AssertionError> { collection.shouldNotContainOnlyNulls() }
         }
      }
   }

   context("containOnlyNulls matcher") {

      test("should pass when all elements in the collection are null") {
         val collection: Collection<String?> = listOf(null, null, null)
         collection should containOnlyNulls()
      }

      test("should fail when any element in the collection is not null") {
         val collection: Collection<String?> = listOf(null, "not null", null)
         shouldThrow<AssertionError> {
            collection should containOnlyNulls()
         }
      }

      test("should pass for an empty collection") {
         val collection: Collection<String?> = emptyList()
         collection should containOnlyNulls()
      }

      test("should pass when used with shouldNot and at least one element is not null") {
         val collection: Collection<String?> = listOf(null, "not null", null)
         collection shouldNot containOnlyNulls()
      }

      test("should fail when used with shouldNot and all elements are null") {
         val collection: Collection<String?> = listOf(null, null, null)
         shouldThrow<AssertionError> { collection shouldNot containOnlyNulls() }
      }

      test("should fail when used with shouldNot and the collection is empty") {
         val collection: Collection<String?> = emptyList()
         shouldThrow<AssertionError> { collection shouldNot containOnlyNulls() }
      }
   }

   context("shouldContainNull matcher") {

      context("on Iterable") {
         test("should pass when at least one element is null") {
            val iterable: Iterable<String?> = listOf("a", null, "b")
            iterable.shouldContainNull()
         }

         test("should fail when no elements are null") {
            val iterable: Iterable<String?> = listOf("a", "b", "c")
            shouldThrow<AssertionError> { iterable.shouldContainNull() }
         }

         test("should fail for an empty iterable") {
            val iterable: Iterable<String?> = emptyList()
            shouldThrow<AssertionError> { iterable.shouldContainNull() }
         }
      }

      context("on Array") {
         test("should pass when at least one element is null") {
            val array = arrayOf("a", null, "b")
            array.shouldContainNull()
         }

         test("should fail when no elements are null") {
            val array = arrayOf("a", "b", "c")
            shouldThrow<AssertionError> { array.shouldContainNull() }
         }

         test("should fail for an empty array") {
            val array = emptyArray<String?>()
            shouldThrow<AssertionError> { array.shouldContainNull() }
         }
      }

      context("on Collection") {
         test("should pass when at least one element is null") {
            val collection: Collection<String?> = listOf("a", null, "b")
            collection.shouldContainNull()
         }

         test("should fail when no elements are null") {
            val collection: Collection<String?> = listOf("a", "b", "c")
            shouldThrow<AssertionError> { collection.shouldContainNull() }
         }

         test("should fail for an empty collection") {
            val collection: Collection<String?> = emptyList()
            shouldThrow<AssertionError> { collection.shouldContainNull() }
         }
      }
   }

   context("shouldNotContainNull matcher") {

      context("on Iterable") {
         test("should pass when no elements are null") {
            val iterable: Iterable<String?> = listOf("a", "b", "c")
            iterable.shouldNotContainNull()
         }

         test("should fail when at least one element is null") {
            val iterable: Iterable<String?> = listOf("a", null, "b")
            shouldThrow<AssertionError> { iterable.shouldNotContainNull() }
         }

         test("should pass for an empty iterable") {
            val iterable: Iterable<String?> = emptyList()
            iterable.shouldNotContainNull()
         }
      }

      context("on Array") {
         test("should pass when no elements are null") {
            val array = arrayOf("a", "b", "c")
            array.shouldNotContainNull()
         }

         test("should fail when at least one element is null") {
            val array = arrayOf("a", null, "b")
            shouldThrow<AssertionError> { array.shouldNotContainNull() }
         }

         test("should pass for an empty array") {
            val array = emptyArray<String?>()
            array.shouldNotContainNull()
         }
      }

      context("on Collection") {
         test("should pass when no elements are null") {
            val collection: Collection<String?> = listOf("a", "b", "c")
            collection.shouldNotContainNull()
         }

         test("should fail when at least one element is null") {
            val collection: Collection<String?> = listOf("a", null, "b")
            shouldThrow<AssertionError> { collection.shouldNotContainNull() }
         }

         test("should pass for an empty collection") {
            val collection: Collection<String?> = emptyList()
            collection.shouldNotContainNull()
         }
      }
   }

   context("containNull matcher") {

      test("should pass when the collection contains at least one null element") {
         val collection: Collection<String?> = listOf("a", null, "b")
         collection should containNull()
      }

      test("should pass when the collection contains at only null elements") {
         val collection: Collection<String?> = listOf(null, null, null)
         collection should containNull()
      }

      test("should fail when the collection contains no null elements") {
         val collection: Collection<String?> = listOf("a", "b", "c")
         shouldThrow<AssertionError> { collection should containNull() }
      }

      test("should fail when the collection is empty") {
         val collection: Collection<String?> = emptyList()
         shouldThrow<AssertionError> { collection should containNull() }
      }

      test("should pass when used with shouldNot and the collection contains no null elements") {
         val collection: Collection<String?> = listOf("a", "b", "c")
         collection shouldNot containNull()
      }

      test("should fail when used with shouldNot and the collection contains at least one null element") {
         val collection: Collection<String?> = listOf(null, "a", "b")
         shouldThrow<AssertionError> { collection shouldNot containNull() }
      }

      test("should pass when used with shouldNot and the collection is empty") {
         val collection: Collection<String?> = emptyList()
         collection shouldNot containNull()
      }
   }

   context("shouldContainNoNulls matcher") {

      context("on Iterable") {
         test("should pass when all elements are non-null") {
            val iterable = listOf("a", "b", "c")
            iterable.shouldContainNoNulls()
         }

         test("should fail when any element is null") {
            val iterable = listOf("a", null, "b")
            shouldThrow<AssertionError> {
               iterable.shouldContainNoNulls()
            }
         }

         test("should pass for an empty iterable") {
            val iterable = emptyList<String?>()
            iterable.shouldContainNoNulls()
         }
      }

      context("on Array") {
         test("should pass when all elements are non-null") {
            val array = arrayOf("a", "b", "c")
            array.shouldContainNoNulls()
         }

         test("should fail when any element is null") {
            val array = arrayOf("a", null, "b")
            shouldThrow<AssertionError> {
               array.shouldContainNoNulls()
            }
         }

         test("should pass for an empty array") {
            val array = emptyArray<String?>()
            array.shouldContainNoNulls()
         }
      }

      context("on Collection") {
         test("should pass when all elements are non-null") {
            val collection = listOf("a", "b", "c")
            collection.shouldContainNoNulls()
         }

         test("should fail when any element is null") {
            val collection = listOf("a", null, "b")
            shouldThrow<AssertionError> {
               collection.shouldContainNoNulls()
            }
         }

         test("should pass for an empty collection") {
            val collection = emptyList<String?>()
            collection.shouldContainNoNulls()
         }
      }
   }

   context("shouldNotContainNoNulls matcher") {

      context("on Iterable") {
         test("should pass when at least one element is null") {
            val iterable = listOf("a", null, "b")
            iterable.shouldNotContainNoNulls()
         }

         test("should fail when all elements are non-null") {
            val iterable = listOf("a", "b", "c")
            shouldThrow<AssertionError> {
               iterable.shouldNotContainNoNulls()
            }
         }

         test("should fail for an empty iterable") {
            val iterable = emptyList<String?>()
            shouldThrow<AssertionError> {
               iterable.shouldNotContainNoNulls()
            }
         }
      }

      context("on Array") {
         test("should pass when at least one element is null") {
            val array = arrayOf("a", null, "b")
            array.shouldNotContainNoNulls()
         }

         test("should fail when all elements are non-null") {
            val array = arrayOf("a", "b", "c")
            shouldThrow<AssertionError> {
               array.shouldNotContainNoNulls()
            }
         }

         test("should fail for an empty array") {
            val array = emptyArray<String?>()
            shouldThrow<AssertionError> {
               array.shouldNotContainNoNulls()
            }
         }
      }

      context("on Collection") {
         test("should pass when at least one element is null") {
            val collection = listOf("a", null, "b")
            collection.shouldNotContainNoNulls()
         }

         test("should fail when all elements are non-null") {
            val collection = listOf("a", "b", "c")
            shouldThrow<AssertionError> {
               collection.shouldNotContainNoNulls()
            }
         }

         test("should fail for an empty collection") {
            val collection = emptyList<String?>()
            shouldThrow<AssertionError> {
               collection.shouldNotContainNoNulls()
            }
         }
      }
   }

   context("containNoNulls matcher") {

      test("should pass when all elements in the collection are non-null") {
         val collection = listOf("a", "b", "c")
         collection should containNoNulls()
      }

      test("should fail when any element in the collection is null") {
         val collection = listOf("a", null, "b")
         shouldThrow<AssertionError> {
            collection should containNoNulls()
         }
      }

      test("should pass for an empty collection") {
         val collection = emptyList<String?>()
         collection should containNoNulls()
      }

      test("should pass for shouldNot when the collection contains null elements") {
         val collection = listOf("a", null, "b")
         collection shouldNot containNoNulls()
      }

      test("should fail for shouldNot when the collection contains no null elements") {
         val collection = listOf("a", "b", "c")
         shouldThrow<AssertionError> { collection shouldNot containNoNulls() }

      }

      test("should fail for shouldNot when the collection is empty") {
         val collection = emptyList<String?>()
         shouldThrow<AssertionError> { collection shouldNot containNoNulls() }
      }
   }

})
