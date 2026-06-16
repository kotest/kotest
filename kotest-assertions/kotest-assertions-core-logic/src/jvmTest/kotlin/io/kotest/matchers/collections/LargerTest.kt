package io.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.throwable.shouldHaveMessage

class LargerTest : FunSpec({

   context("Should Be Larger Than") {

      context("Primitive Arrays") {

         context("ByteArray") {
            test("ByteArray of size 3 should be larger than ByteArray of size 2") {
               val arr1 = byteArrayOf(1, 2, 3)
               val arr2 = byteArrayOf(1, 2)
               arr1 shouldBeLargerThan arr2
            }

            test("ByteArray of size 3 should fail to be larger than ByteArray of size 2") {
               val arr1 = byteArrayOf(1, 2, 3)
               val arr2 = byteArrayOf(1, 2)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("IntArray") {
            test("IntArray of size 3 should be larger than IntArray of size 2") {
               val arr1 = intArrayOf(1, 2, 3)
               val arr2 = intArrayOf(1, 2)
               arr1 shouldBeLargerThan arr2
            }

            test("IntArray of size 3 should fail to be larger than IntArray of size 2") {
               val arr1 = intArrayOf(1, 2, 3)
               val arr2 = intArrayOf(1, 2)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("ShortArray") {
            test("ShortArray of size 3 should be larger than ShortArray of size 2") {
               val arr1 = shortArrayOf(1, 2, 3)
               val arr2 = shortArrayOf(1, 2)
               arr1 shouldBeLargerThan arr2
            }

            test("ShortArray of size 3 should fail to be larger than ShortArray of size 2") {
               val arr1 = shortArrayOf(1, 2, 3)
               val arr2 = shortArrayOf(1, 2)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("LongArray") {
            test("LongArray of size 3 should be larger than LongArray of size 2") {
               val arr1 = longArrayOf(1L, 2L, 3L)
               val arr2 = longArrayOf(1L, 2L)
               arr1 shouldBeLargerThan arr2
            }

            test("LongArray of size 3 should fail to be larger than LongArray of size 2") {
               val arr1 = longArrayOf(1L, 2L, 3L)
               val arr2 = longArrayOf(1L, 2L)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("FloatArray") {
            test("FloatArray of size 3 should be larger than FloatArray of size 2") {
               val arr1 = floatArrayOf(1.1f, 2.2f, 3.3f)
               val arr2 = floatArrayOf(1.1f, 2.2f)
               arr1 shouldBeLargerThan arr2
            }

            test("FloatArray of size 3 should fail to be larger than FloatArray of size 2") {
               val arr1 = floatArrayOf(1.1f, 2.2f, 3.3f)
               val arr2 = floatArrayOf(1.1f, 2.2f)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("DoubleArray") {
            test("DoubleArray of size 3 should be larger than DoubleArray of size 2") {
               val arr1 = doubleArrayOf(1.1, 2.2, 3.3)
               val arr2 = doubleArrayOf(1.1, 2.2)
               arr1 shouldBeLargerThan arr2
            }

            test("DoubleArray of size 3 should fail to be larger than DoubleArray of size 2") {
               val arr1 = doubleArrayOf(1.1, 2.2, 3.3)
               val arr2 = doubleArrayOf(1.1, 2.2)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("CharArray") {
            test("CharArray of size 3 should be larger than CharArray of size 2") {
               val arr1 = charArrayOf('a', 'b', 'c')
               val arr2 = charArrayOf('a', 'b')
               arr1 shouldBeLargerThan arr2
            }

            test("CharArray of size 3 should fail to be larger than CharArray of size 2") {
               val arr1 = charArrayOf('a', 'b', 'c')
               val arr2 = charArrayOf('a', 'b')
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }

         context("BooleanArray") {
            test("BooleanArray of size 3 should be larger than BooleanArray of size 2") {
               val arr1 = booleanArrayOf(true, false, true)
               val arr2 = booleanArrayOf(true, false)
               arr1 shouldBeLargerThan arr2
            }

            test("BooleanArray of size 3 should fail to be larger than BooleanArray of size 2") {
               val arr1 = booleanArrayOf(true, false, true)
               val arr2 = booleanArrayOf(true, false)
               shouldThrow<AssertionError> {
                  arr2 shouldBeLargerThan arr1
               } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
            }
         }
      }

      context("Iterables") {
         test("Iterable of size 3 should be larger than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            col1 shouldBeLargerThan col2
         }

         test("Iterable of size 3 should fail to be larger than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            shouldThrow<AssertionError> {
               col2 shouldBeLargerThan col1
            } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
         }
      }

      context("Arrays") {
         test("Array of size 3 should be larger than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            arr1 shouldBeLargerThan arr2
         }

         test("Array of size 3 should fail to be larger than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            shouldThrow<AssertionError> {
               arr2 shouldBeLargerThan arr1
            } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
         }
      }
   }

   context("Should NOT Be Larger Than") {
      context("Iterables") {
         test("Iterable of size 2 should not be larger than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            col1 shouldNotBeLargerThan col2
         }

         test("Iterable of size 2 should fail to not be larger than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            shouldThrow<AssertionError> {
               col2 shouldNotBeLargerThan col1
            } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
         }
      }

      context("Arrays") {
         test("Array of size 2 should not be larger than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            arr1 shouldNotBeLargerThan arr2
         }

         test("Array of size 2 should fail to not be larger than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            shouldThrow<AssertionError> {
               arr2 shouldNotBeLargerThan arr1
            } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
         }
      }
   }

   context("beLargerThan Matchers") {
      context("Primitive Arrays") {

         context("ByteArray") {
            test("ByteArray of size 2 should not be larger than ByteArray of size 3") {
               val arr1 = byteArrayOf(1, 2)
               val arr2 = byteArrayOf(1, 2, 3)
               arr1 shouldNotBeLargerThan arr2
            }

            test("ByteArray of size 2 should fail to not be larger than ByteArray of size 3") {
               val arr1 = byteArrayOf(1, 2)
               val arr2 = byteArrayOf(1, 2, 3)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("IntArray") {
            test("IntArray of size 2 should not be larger than IntArray of size 3") {
               val arr1 = intArrayOf(1, 2)
               val arr2 = intArrayOf(1, 2, 3)
               arr1 shouldNotBeLargerThan arr2
            }

            test("IntArray of size 2 should fail to not be larger than IntArray of size 3") {
               val arr1 = intArrayOf(1, 2)
               val arr2 = intArrayOf(1, 2, 3)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("ShortArray") {
            test("ShortArray of size 2 should not be larger than ShortArray of size 3") {
               val arr1 = shortArrayOf(1, 2)
               val arr2 = shortArrayOf(1, 2, 3)
               arr1 shouldNotBeLargerThan arr2
            }

            test("ShortArray of size 2 should fail to not be larger than ShortArray of size 3") {
               val arr1 = shortArrayOf(1, 2)
               val arr2 = shortArrayOf(1, 2, 3)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("LongArray") {
            test("LongArray of size 2 should not be larger than LongArray of size 3") {
               val arr1 = longArrayOf(1L, 2L)
               val arr2 = longArrayOf(1L, 2L, 3L)
               arr1 shouldNotBeLargerThan arr2
            }

            test("LongArray of size 2 should fail to not be larger than LongArray of size 3") {
               val arr1 = longArrayOf(1L, 2L)
               val arr2 = longArrayOf(1L, 2L, 3L)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("FloatArray") {
            test("FloatArray of size 2 should not be larger than FloatArray of size 3") {
               val arr1 = floatArrayOf(1.1f, 2.2f)
               val arr2 = floatArrayOf(1.1f, 2.2f, 3.3f)
               arr1 shouldNotBeLargerThan arr2
            }

            test("FloatArray of size 2 should fail to not be larger than FloatArray of size 3") {
               val arr1 = floatArrayOf(1.1f, 2.2f)
               val arr2 = floatArrayOf(1.1f, 2.2f, 3.3f)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("DoubleArray") {
            test("DoubleArray of size 2 should not be larger than DoubleArray of size 3") {
               val arr1 = doubleArrayOf(1.1, 2.2)
               val arr2 = doubleArrayOf(1.1, 2.2, 3.3)
               arr1 shouldNotBeLargerThan arr2
            }

            test("DoubleArray of size 2 should fail to not be larger than DoubleArray of size 3") {
               val arr1 = doubleArrayOf(1.1, 2.2)
               val arr2 = doubleArrayOf(1.1, 2.2, 3.3)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("CharArray") {
            test("CharArray of size 2 should not be larger than CharArray of size 3") {
               val arr1 = charArrayOf('a', 'b')
               val arr2 = charArrayOf('a', 'b', 'c')
               arr1 shouldNotBeLargerThan arr2
            }

            test("CharArray of size 2 should fail to not be larger than CharArray of size 3") {
               val arr1 = charArrayOf('a', 'b')
               val arr2 = charArrayOf('a', 'b', 'c')
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }

         context("BooleanArray") {
            test("BooleanArray of size 2 should not be larger than BooleanArray of size 3") {
               val arr1 = booleanArrayOf(true, false)
               val arr2 = booleanArrayOf(true, false, true)
               arr1 shouldNotBeLargerThan arr2
            }

            test("BooleanArray of size 2 should fail to not be larger than BooleanArray of size 3") {
               val arr1 = booleanArrayOf(true, false)
               val arr2 = booleanArrayOf(true, false, true)
               shouldThrow<AssertionError> {
                  arr2 shouldNotBeLargerThan arr1
               } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
            }
         }
      }

      test("passes when the collection is larger") {
         val larger = listOf(1, 2, 3)
         val smaller = listOf(1, 2)
         larger should beLargerThan(smaller)
      }

      test("fails when the collection is not larger") {
         val smaller = listOf(1, 2)
         val larger = listOf(1, 2, 3)
         smaller shouldNot beLargerThan(larger)
      }

      test("fails when the collections are of the same size") {
         val collection1 = listOf(1, 2, 3)
         val collection2 = listOf(4, 5, 6)
         collection1 shouldNot beLargerThan(collection2)
      }

      test("works with empty collections") {
         val emptyCollection = emptyList<Int>()
         val nonEmptyCollection = listOf(1)
         nonEmptyCollection should beLargerThan(emptyCollection)
         emptyCollection shouldNot beLargerThan(nonEmptyCollection)
      }

      test("throws no exceptions with empty collections compared") {
         val empty1 = emptyList<Int>()
         val empty2 = emptyList<Int>()
         empty1 shouldNot beLargerThan(empty2)
      }
   }
})
