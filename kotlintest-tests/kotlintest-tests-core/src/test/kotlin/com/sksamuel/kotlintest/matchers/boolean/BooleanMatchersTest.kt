package com.sksamuel.kotlintest.matchers.boolean

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import org.opentest4j.AssertionFailedError

@Suppress("SimplifyBooleanWithConstants")
class BooleanMatchersTest : FreeSpec() {

    
    init {
        "Boolean shouldBeTrue should not fail for true booleans" {
            true.shouldBeTrue()
            (3 + 3 == 6).shouldBeTrue()
        }
        
        "Boolean shouldBeTrue should fail for false booleans" - {
            val thrownException = shouldThrow<AssertionFailedError> { false.shouldBeTrue() }
            
            "Failure exception should expect true" {
                thrownException.expected.value shouldBe "true"
            }
            
            "Failure exception should have actual false" {
                thrownException.actual.value shouldBe "false"
            }
        }
        
        "Boolean shouldBeFalse should not fail for false booleans" {
            false.shouldBeFalse()
            (3 + 3 == 42).shouldBeFalse()
        }
        
        "Boolean shouldBeFalse should fail for true booleans" - {
            val thrownException = shouldThrow<AssertionFailedError> { true.shouldBeFalse() }
    
            "Failure exception should expect false" {
                thrownException.expected.value shouldBe "false"
            }
    
            "Failure exception should have actual true" {
                thrownException.actual.value shouldBe "true"
            }
        }
    }
}