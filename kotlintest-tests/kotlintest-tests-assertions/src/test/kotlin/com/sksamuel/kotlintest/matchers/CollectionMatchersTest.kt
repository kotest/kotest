package com.sksamuel.kotlintest

import io.kotlintest.shouldFail
import io.kotlintest.specs.BehaviorSpec
import io.kotlintest.matchers.collections.*

class CollectionMatchersTests : BehaviorSpec() {
	val countdown = (10 downTo 0).toList()
	
	init {
		given("countdown") {
			`when`("asserted to be descending") {
				countdown.shouldBeSortedWith { a, b -> b - a }
			}
			
			`when`("asserted not to be ascending") {
				countdown.shouldNotBeSortedWith { a, b -> a - b }
			}

			`when`("asserted to be ascending") {
				shouldFail {
					countdown.shouldBeSortedWith { a, b -> a - b }
				}
			}
			
			`when`("asserted not to be descending") {
				shouldFail {
					countdown.shouldNotBeSortedWith { a, b -> b - a }
				}
			}
		}
	}
}
