package com.sksamuel.kotlintest

import io.kotlintest.shouldFail
import io.kotlintest.specs.WordSpec
import io.kotlintest.matchers.collections.*

class CollectionMatchersTests : WordSpec() {
	val countdown = (10 downTo 0).toList()
	
	init {
		"a descending non-empty list" should {
			"fail to ascend" {
				shouldFail {
					countdown.shouldBeSortedWith { a, b -> a - b }
				}
			}
			
			"descend" {
				countdown.shouldBeSortedWith { a, b -> b - a }
			}
			
			"not ascend" {
				countdown.shouldNotBeSortedWith { a, b -> a - b }
			}

			"fail not to descend" {
				shouldFail {
					countdown.shouldNotBeSortedWith { a, b -> b - a }
				}
			}
		}
	}
}
