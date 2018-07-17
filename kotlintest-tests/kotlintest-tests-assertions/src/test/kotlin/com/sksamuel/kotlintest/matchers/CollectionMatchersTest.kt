package com.sksamuel.kotlintest

import io.kotlintest.shouldFail
import io.kotlintest.specs.WordSpec
import io.kotlintest.matchers.collections.*

class CollectionMatchersTests : WordSpec() {
	val countdown = (10 downTo 0).toList()
	val asc = { a:Int, b:Int -> a - b }
	val desc = { a:Int, b:Int -> b - a }
	
	init {
		"a descending non-empty list" should {
			"fail to ascend" {
				shouldFail {
					countdown.shouldBeSortedWith(asc)
				}
			}
			
			"descend" {
				countdown.shouldBeSortedWith(desc)
			}
			
			"not ascend" {
				countdown.shouldNotBeSortedWith(asc)
			}

			"fail not to descend" {
				shouldFail {
					countdown.shouldNotBeSortedWith(desc)
				}
			}
		}
	}
}
