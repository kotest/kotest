package io.kotlintest.runner.junit4.samples

import io.kotlintest.*
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.matchers.*
import io.kotlintest.matchers.types.*
import io.kotlintest.specs.*

/**
* This file contains Specs that are used to test the runner itself. These
* specs are ignored in Gradle in order to avoid false build failures.
*/

class SomeBehaviourSpec: BehaviorSpec({
	Given("I have a 1") {
		val one = 1

		When("I add a 2") {
			val two = 2

			Then("I get a 3") {
				one + two shouldBe 3
			}
		}
	}

	Given("Big Brother says 2 + 2 = 5") {
		When("I add 2 + 2") {
			Then("I should get 5") {
				2 + 2 shouldBe 5
			}
		}
	}
})
