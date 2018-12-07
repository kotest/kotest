package com.sksamuel.kt.matchers.doubles

import io.kotlintest.matchers.doubles.beGreaterThan
import io.kotlintest.matchers.doubles.beGreaterThanOrEqualTo
import io.kotlintest.matchers.doubles.beLessThan
import io.kotlintest.matchers.doubles.beLessThanOrEqualTo
import io.kotlintest.matchers.doubles.between
import io.kotlintest.matchers.doubles.exactly
import io.kotlintest.matchers.doubles.gt
import io.kotlintest.matchers.doubles.gte
import io.kotlintest.matchers.doubles.lt
import io.kotlintest.matchers.doubles.lte
import io.kotlintest.matchers.doubles.negative
import io.kotlintest.matchers.doubles.positive
import io.kotlintest.matchers.doubles.shouldBeBetween
import io.kotlintest.matchers.doubles.shouldBeExactly
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.doubles.shouldBeLessThan
import io.kotlintest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotlintest.matchers.doubles.shouldBeNegative
import io.kotlintest.matchers.doubles.shouldBePositive
import io.kotlintest.matchers.doubles.shouldNotBeBetween
import io.kotlintest.matchers.doubles.shouldNotBeExactly
import io.kotlintest.matchers.doubles.shouldNotBeGreaterThan
import io.kotlintest.matchers.doubles.shouldNotBeGreaterThanOrEqual
import io.kotlintest.matchers.doubles.shouldNotBeLessThan
import io.kotlintest.matchers.doubles.shouldNotBeLessThanOrEqual
import io.kotlintest.matchers.doubles.shouldNotBeNegative
import io.kotlintest.matchers.doubles.shouldNotBePositive
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import kotlin.Double.Companion.MAX_VALUE
import kotlin.Double.Companion.MIN_VALUE
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.NaN
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.absoluteValue
import kotlin.math.ulp

class DoubleMatchersTest : FreeSpec() {
  
  private val nonNumericDoubles = listOf(NaN, POSITIVE_INFINITY, NEGATIVE_INFINITY)
  
  private val numericDoubles = Gen.double().filterNot { it in nonNumericDoubles }
  private val nonMinNorMaxValueDoubles = numericDoubles.filterNot { it in listOf(MAX_VALUE, MIN_VALUE) }
  
  init {
    "Exactly Matcher" - {
      
      "Every numeric Double" - {
        
        "Should be exactly" - {
          
          "Itself" {
            assertAll(numericDoubles) {
              it shouldExactlyMatch it
            }
          }
        }
        
        
        "Should not be exactly" - {
          
          "Any number smaller than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchExactly it.slightlySmaller()
            }
          }
          
          "Any number bigger than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchExactly it.slightlyGreater()
            }
          }
          
          "Anything that's not numeric" {
            assertAll(numericDoubles) {
              nonNumericDoubles.forEach { nonNumeric ->
                it shouldNotMatchExactly nonNumeric
              }
            }
          }
        }
        
      }
      
      "The non-numeric double" - {
        "NaN" - {
          
          "Should not be exactly" - {
            
            "Any number" {
              assertAll(numericDoubles) {
                NaN shouldNotMatchExactly it
              }
            }
            
            "NaN" {
              NaN shouldNotMatchExactly NaN
            }
            
            "The infinities" {
              NaN shouldNotMatchExactly POSITIVE_INFINITY
              NaN shouldNotMatchExactly NEGATIVE_INFINITY
            }
          }
        }
        
        "Positive Infinity" - {
          
          "Should be exactly" - {
            "Itself" {
              POSITIVE_INFINITY shouldExactlyMatch POSITIVE_INFINITY
            }
          }
          
          "Should not be exactly" - {
            
            "Any numeric double" {
              assertAll(numericDoubles) {
                POSITIVE_INFINITY shouldNotMatchExactly it
              }
            }
            
            "Any other non-numeric double" {
              POSITIVE_INFINITY shouldNotMatchExactly NEGATIVE_INFINITY
              POSITIVE_INFINITY shouldNotMatchExactly NaN
            }
          }
          
        }
        
        "Negative Infinity" - {
          
          "Should be exactly" - {
            "Itself" {
              NEGATIVE_INFINITY shouldExactlyMatch NEGATIVE_INFINITY
            }
          }
          
          "Should not be exactly" - {
            
            "Any numeric double" {
              assertAll(numericDoubles) {
                NEGATIVE_INFINITY shouldNotMatchExactly it
              }
            }
            
            "Any other non-numeric double" {
              NEGATIVE_INFINITY shouldNotMatchExactly POSITIVE_INFINITY
              NEGATIVE_INFINITY shouldNotMatchExactly NaN
            }
          }
        }
      }
      
    }
    
    "Between matcher" - {
      
      "Every numeric double that is not Double.MAX_VALUE" - {
        
        "Should match between" - {
          
          "When it's equal to the first number of the range" - {
            
            "With tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it, it.slightlyGreater(), it.toleranceValue())
              }
            }
            
            "Without tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it, it.slightlyGreater(), 0.0)
                
              }
            }
          }
          
          "When it's between the first number of the range and the last one" - {
            
            "With tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it.slightlyGreater(), it.toleranceValue())
              }
            }
            
            "Without tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it.slightlyGreater(), 0.0)
              }
            }
          }
          
          "When it's equal to the last number of the range" - {
            
            "With tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it, it.toleranceValue())
              }
            }
            
            "Without tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldMatchBetween(it.slightlySmaller(), it, 0.0)
              }
            }
          }
        }
        
        "Should not match between" - {
          
          "When it's smaller than the first number of the range" - {
            
            "With tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.slightlyGreater(), it.muchGreater(), it.toleranceValue())
              }
            }
            
            "Without tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.slightlyGreater(), it.muchGreater(), 0.0)
              }
            }
          }
          
          "When it's bigger than the last number of the range" - {
            
            "With tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.muchSmaller(), it.slightlySmaller(), it.toleranceValue())
              }
            }
            
            "Without tolerance" {
              assertAll(nonMinNorMaxValueDoubles) {
                it.shouldNotMatchBetween(it.muchSmaller(), it.slightlySmaller(), 0.0)
              }
            }
          }
        }
        
        
      }
    }
    
    "Less than matcher" - {
      
      "Every numeric double" - {
        
        "Should be less than" - {
          
          "Numbers bigger than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchLessThan it.slightlyGreater()
              it shouldMatchLessThan it.muchGreater()
            }
          }
          
          "Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchLessThan POSITIVE_INFINITY
            }
          }
        }
        
        
        "Should not be less than" - {
          
          "Itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it
            }
          }
          
          "Numbers smaller than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it.slightlySmaller()
              it shouldNotMatchLessThan it.muchSmaller()
            }
          }
          
          "Negative Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan it
            }
          }
          
          "NaN" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThan NaN
            }
          }
        }
        
      }
      
      "The non-numeric double" - {
        
        "NaN" - {
          "Should not be less than" - {
            
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                NaN shouldNotMatchLessThan it
              }
            }
            
            "Any non-numeric double" {
              nonNumericDoubles.forEach {
                NaN shouldNotMatchLessThan it
              }
            }
          }
          
        }
        
        "Positive Infinity" - {
          "Should not be less than" - {
            
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                POSITIVE_INFINITY shouldNotMatchLessThan it
              }
            }
            
            "Any non-numeric double" {
              nonNumericDoubles.forEach {
                POSITIVE_INFINITY shouldNotMatchLessThan it
              }
            }
          }
          
        }
        
        "Negative Infinity" - {
          
          "Should be less than" - {
            
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                NEGATIVE_INFINITY shouldMatchLessThan it
              }
            }
            
            "Positive Infinity" {
              NEGATIVE_INFINITY shouldMatchLessThan POSITIVE_INFINITY
            }
          }
          
          "Should not be less than" - {
            
            "Itself" {
              NEGATIVE_INFINITY shouldNotMatchLessThan NEGATIVE_INFINITY
            }
            
            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchLessThan NaN
            }
          }
        }
      }
    }
    
    "Positive matcher" - {
      
      "Zero" - {
        "Should not be positive" {
          0.0.shouldNotMatchPositive()
        }
      }
      "Every positive number" - {
        
        "Should be positive" {
          assertAll(numericDoubles.filterNot { it == 0.0 }) {
            it.absoluteValue.shouldMatchPositive()
          }
        }
      }
      
      "Every non-positive number" - {
        "Should not be positive" {
          assertAll(numericDoubles) {
            (-it.absoluteValue).shouldNotMatchPositive()
          }
        }
      }
      
      "The non-numeric double" - {
        "Positive Infinity" - {
          "Should be positive" {
            POSITIVE_INFINITY.shouldMatchPositive()
          }
        }
        
        "Negative Infinity" - {
          "Should not be positive" {
            NEGATIVE_INFINITY.shouldNotMatchPositive()
          }
        }
        
        "NaN" - {
          "Should not be positive" {
            NaN.shouldNotMatchPositive()
          }
        }
      }
    }
    
    "Negative matcher" - {
      
      "Zero" - {
        "Should not be negative" {
          0.0.shouldNotMatchNegative()
        }
      }
      "Every negative number" - {
        "Should be negative" {
          assertAll(numericDoubles.filterNot { it == 0.0 }) {
            (-it.absoluteValue).shouldMatchNegative()
          }
        }
      }
      
      "Every non-negative number" - {
        "Should not be negative" {
          assertAll(numericDoubles) {
            it.absoluteValue.shouldNotMatchNegative()
          }
        }
      }
      
      "The non-numeric double" - {
        "Positive Infinity" - {
          "Should not be negative" {
            POSITIVE_INFINITY.shouldNotMatchNegative()
          }
        }
        
        "Negative Infinity" - {
          "Should be negative" {
            NEGATIVE_INFINITY.shouldMatchNegative()
          }
        }
        
        "NaN" - {
          "Should not be negative" {
            NaN.shouldNotMatchNegative()
          }
        }
      }
    }
    
    "Less than or equal matcher" - {
      "Every numeric double" - {
        "Should be less than or equal" - {
          
          "Itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual it
            }
          }
          
          "Numbers bigger than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual it.muchGreater()
              it shouldMatchLessThanOrEqual it.slightlyGreater()
            }
          }
          
          "Positive Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }
          }
        }
        
        "Should not be less than or equal" - {
          "Any number smaller than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual it.slightlySmaller()
              it shouldNotMatchLessThanOrEqual it.muchSmaller()
            }
          }
          
          "Negative Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }
          }
          
          "NaN" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
      }
      
      "The non-numeric double" - {
        "NaN" {
          "Should not be less than or equal" - {
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                NaN shouldNotMatchLessThanOrEqual it
              }
            }
            
            "Positive Infinity" {
              NaN shouldNotMatchLessThanOrEqual POSITIVE_INFINITY
            }
            
            "Negative Infinity" {
              NaN shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }
            
            "Itself" {
              NaN shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
        
        "Positive Infinity" - {
          
          "Should be less than or equal" - {
            "Positive Infinity" {
              POSITIVE_INFINITY shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }
          }
          "Should not be less than or equal" - {
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                POSITIVE_INFINITY shouldNotMatchLessThanOrEqual it
              }
            }
            
            "Negative Infinity" {
              POSITIVE_INFINITY shouldNotMatchLessThanOrEqual NEGATIVE_INFINITY
            }
            
            "NaN" {
              POSITIVE_INFINITY shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
        
        "Negative Infinity" - {
          "Should be less than or equal" - {
            "Any numeric double" {
              assertAll(nonMinNorMaxValueDoubles) {
                NEGATIVE_INFINITY shouldMatchLessThanOrEqual it
              }
            }
            
            "Positive Infinity" {
              NEGATIVE_INFINITY shouldMatchLessThanOrEqual POSITIVE_INFINITY
            }
            
            "Itself" {
              NEGATIVE_INFINITY shouldMatchLessThanOrEqual NEGATIVE_INFINITY
            }
          }
          
          "Should not be less than or equal" - {
            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchLessThanOrEqual NaN
            }
          }
        }
      }
    }
    
    "Greater than matcher" - {
      "Every numeric double" - {
        "Should be greater than" - {
          
          "Numbers smaller than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThan it.slightlySmaller()
              it shouldMatchGreaterThan it.muchSmaller()
            }
          }
          
          "Negative infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThan NEGATIVE_INFINITY
            }
          }
        }
        
        "Should not be greater than" - {
          
          "Itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan it
            }
          }
          
          "Numbers greater than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan it.slightlyGreater()
              it shouldNotMatchGreaterThan it.muchGreater()
            }
          }
          
          "NaN" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan NaN
            }
          }
          
          "Positive Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThan POSITIVE_INFINITY
            }
          }
        }
      }
      
      "The non-numeric double" - {
        "NaN" - {
          "Should not be greater than" - {
            
            "Itself" {
              NaN shouldNotMatchGreaterThan NaN
            }
            
            "Any numeric double" {
              assertAll(numericDoubles) {
                NaN shouldNotMatchGreaterThan it
              }
            }
            
            "Positive Infinity" {
              NaN shouldNotMatchGreaterThan POSITIVE_INFINITY
            }
            
            "Negative Infinity" {
              NaN shouldNotMatchGreaterThan NEGATIVE_INFINITY
            }
          }
        }
      }
    }
    
    "Greater than or equal matcher" - {
      "Every numeric double" - {
        "Should be greater than or equal to" - {
          
          "Itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual it
            }
          }
          
          "Numbers smaller than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual it.slightlySmaller()
              it shouldMatchGreaterThanOrEqual it.muchSmaller()
            }
          }
          
          "Negative Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }
        }
        
        "Should not be greater than or equal to" - {
          "Numbers bigger than itself" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual it.slightlyGreater()
              it shouldNotMatchGreaterThanOrEqual it.muchGreater()
              
            }
          }
          
          "Positive Infinity" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }
          }
          
          "NaN" {
            assertAll(nonMinNorMaxValueDoubles) {
              it shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }
        
      }
      
      "The non-numeric double" - {
        "NaN" - {
          "Should not be greater than or equal to" - {
            "Itself" {
              NaN shouldNotMatchGreaterThanOrEqual NaN
            }
            
            "Any numeric double" {
              assertAll(numericDoubles) {
                NaN shouldNotMatchGreaterThanOrEqual it
              }
            }
            
            "Positive Infinity" {
              NaN shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }
            
            "Negative Infinity" {
              NaN shouldNotMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }
        }
        
        "Positive Infinity" - {
          "Should be greater than or equal to" - {
            "Itself" {
              POSITIVE_INFINITY shouldMatchGreaterThanOrEqual POSITIVE_INFINITY
            }
            
            "Negative Infinity" {
              POSITIVE_INFINITY shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
            
            "Any numeric double" {
              assertAll(numericDoubles) {
                POSITIVE_INFINITY shouldMatchGreaterThanOrEqual it
              }
            }
          }
          
          "Should not be greater than or equal to" - {
            "NaN" {
              POSITIVE_INFINITY shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }
        
        "Negative Infinity" - {
          "Should be greater than or equal to" - {
            "Itself" {
              NEGATIVE_INFINITY shouldMatchGreaterThanOrEqual NEGATIVE_INFINITY
            }
          }
          
          "Should not be greater than or equal to" - {
            "Any numeric double" {
              assertAll(numericDoubles) {
                NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual it
              }
            }
            
            "Positive Infinity" {
              NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual POSITIVE_INFINITY
            }
            
            "NaN" {
              NEGATIVE_INFINITY shouldNotMatchGreaterThanOrEqual NaN
            }
          }
        }
      }
    }
    
    
  }
  
  
  private fun Double.toleranceValue(): Double {
    return ulp
  }
  
  private fun Double.slightlyGreater(): Double {
    return this + (2 * ulp)
  }
  
  private fun Double.muchGreater(): Double {
    return this + (3 * ulp)
  }
  
  private fun Double.slightlySmaller(): Double {
    return this - (2 * ulp)
  }
  
  private fun Double.muchSmaller(): Double {
    return this - (3 * ulp)
  }
  
  private fun shouldThrowAssertionError(message: String, vararg expression: () -> Any?) {
    expression.forEach {
      val exception = shouldThrow<AssertionError>(it)
      exception.message shouldBe message
    }
  }
  
  private infix fun Double.shouldExactlyMatch(other: Double) {
    this shouldBeExactly other
    this shouldBe exactly(other)
    this shouldThrowExceptionOnNotExactly other
  }
  
  private infix fun Double.shouldThrowExceptionOnNotExactly(other: Double) {
    shouldThrowAssertionError("$this should not equal $other",
                              { this shouldNotBeExactly other },
                              { this shouldNotBe exactly(other) }
    )
  }
  
  private infix fun Double.shouldNotMatchExactly(other: Double) {
    this shouldNotBe exactly(other)
    this shouldNotBeExactly other
    this shouldThrowExceptionOnExactly other
  }
  
  private infix fun Double.shouldThrowExceptionOnExactly(other: Double) {
    shouldThrowAssertionError("$this is not equal to expected value $other",
                              { this shouldBeExactly other },
                              { this shouldBe exactly(other) }
    )
  }
  
  private fun Double.shouldMatchBetween(a: Double, b: Double, tolerance: Double) {
    this.shouldBeBetween(a, b, tolerance)
    this shouldBe between(a, b, tolerance)
    
    this.shouldThrowExceptionOnNotBetween(a, b, tolerance)
  }
  
  private fun Double.shouldNotMatchBetween(a: Double, b: Double, tolerance: Double) {
    this.shouldNotBeBetween(a, b, tolerance)
    this shouldNotBe between(a, b, tolerance)
    
    this.shouldThrowExceptionOnBetween(a, b, tolerance)
  }
  
  private fun Double.shouldThrowExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    when {
      this < a -> this.shouldThrowMinimumExceptionOnBetween(a, b, tolerance)
      this > b -> this.shouldThrowMaximumExceptionOnBetween(a, b, tolerance)
      else     -> throw IllegalStateException()
    }
  }
  
  private fun Double.shouldThrowMinimumExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    val message = "$this should be bigger than $a"
    shouldThrowExceptionOnBetween(a, b, tolerance, message)
  }
  
  private fun Double.shouldThrowMaximumExceptionOnBetween(a: Double, b: Double, tolerance: Double) {
    val message = "$this should be smaller than $b"
    shouldThrowExceptionOnBetween(a, b, tolerance, message)
  }
  
  
  private fun Double.shouldThrowExceptionOnBetween(
    a: Double,
    b: Double,
    tolerance: Double,
    message: String = "$this should be smaller than $b and bigger than $a"
  ) {
    shouldThrowAssertionError(message,
                              { this.shouldBeBetween(a, b, tolerance) },
                              { this shouldBe between(a, b, tolerance) })
  }
  
  private fun Double.shouldThrowExceptionOnNotBetween(
    a: Double,
    b: Double,
    tolerance: Double,
    message: String = "$this should not be smaller than $b and should not be bigger than $a"
  ) {
    
    shouldThrowAssertionError(message,
                              { this.shouldNotBeBetween(a, b, tolerance) },
                              { this shouldNotBe between(a, b, tolerance) })
  }
  
  private infix fun Double.shouldMatchLessThan(x: Double) {
    this shouldBe lt(x)
    this shouldBeLessThan x
    this should beLessThan(x)
    
    this shouldThrowExceptionOnNotLessThan x
  }
  
  private infix fun Double.shouldThrowExceptionOnNotLessThan(x: Double) {
    shouldThrowAssertionError("$this should not be < $x",
                              { this shouldNotBe lt(x) },
                              { this shouldNotBeLessThan x },
                              { this shouldNot beLessThan(x) })
  }
  
  private infix fun Double.shouldNotMatchLessThan(x: Double) {
    this shouldNotBe lt(x)
    this shouldNotBeLessThan x
    this shouldNot beLessThan(x)
    
    this shouldThrowExceptionOnLessThan x
  }
  
  private infix fun Double.shouldThrowExceptionOnLessThan(x: Double) {
    shouldThrowAssertionError("$this should be < $x",
                              { this shouldBe lt(x) },
                              { this shouldBeLessThan x },
                              { this should beLessThan(x) }
    )
  }
  
  private fun Double.shouldMatchPositive() {
    this.shouldBePositive()
    this shouldBe positive()
    
    this.shouldThrowExceptionOnNotPositive()
  }
  
  private fun Double.shouldThrowExceptionOnNotPositive() {
    shouldThrowAssertionError("$this should not be > 0.0",
                              { this shouldNotBe positive() },
                              { this.shouldNotBePositive() }
    )
  }
  
  private fun Double.shouldNotMatchPositive() {
    this.shouldNotBePositive()
    this shouldNotBe positive()
    
    this.shouldThrowExceptionOnPositive()
  }
  
  private fun Double.shouldThrowExceptionOnPositive() {
    shouldThrowAssertionError("$this should be > 0.0",
                              { this shouldBe positive() },
                              { this.shouldBePositive() }
    )
  }
  
  private fun Double.shouldMatchNegative() {
    this.shouldBeNegative()
    this shouldBe negative()
    
    this.shouldThrowExceptionOnNotNegative()
  }
  
  private fun Double.shouldThrowExceptionOnNotNegative() {
    shouldThrowAssertionError("$this should not be < 0.0",
                              { this shouldNotBe negative() },
                              { this.shouldNotBeNegative() }
    )
  }
  
  private fun Double.shouldNotMatchNegative() {
    this.shouldNotBeNegative()
    this shouldNotBe negative()
    
    this.shouldThrowExceptionOnNegative()
  }
  
  private fun Double.shouldThrowExceptionOnNegative() {
    shouldThrowAssertionError("$this should be < 0.0",
                              { this shouldBe negative() },
                              { this.shouldBeNegative() }
    )
  }
  
  private infix fun Double.shouldMatchLessThanOrEqual(x: Double) {
    this should beLessThanOrEqualTo(x)
    this shouldBe lte(x)
    this shouldBeLessThanOrEqual x
    
    this shouldThrowExceptionOnNotLessThanOrEqual x
  }
  
  private infix fun Double.shouldThrowExceptionOnNotLessThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should not be <= $x",
                              { this shouldNot beLessThanOrEqualTo(x) },
                              { this shouldNotBe lte(x) },
                              { this shouldNotBeLessThanOrEqual x }
    )
  }
  
  private infix fun Double.shouldNotMatchLessThanOrEqual(x: Double) {
    this shouldNot beLessThanOrEqualTo(x)
    this shouldNotBe lte(x)
    this shouldNotBeLessThanOrEqual x
    
    this shouldThrowExceptionOnLessThanOrEqual x
  }
  
  private infix fun Double.shouldThrowExceptionOnLessThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should be <= $x",
                              { this should beLessThanOrEqualTo(x) },
                              { this shouldBe lte(x) },
                              { this shouldBeLessThanOrEqual x }
    )
  }
  
  private infix fun Double.shouldMatchGreaterThan(x: Double) {
    this should beGreaterThan(x)
    this shouldBe gt(x)
    this shouldBeGreaterThan x
    
    this shouldThrowExceptionOnNotGreaterThan x
  }
  
  private infix fun Double.shouldThrowExceptionOnNotGreaterThan(x: Double) {
    shouldThrowAssertionError("$this should not be > $x",
                              { this shouldNot beGreaterThan(x) },
                              { this shouldNotBeGreaterThan (x) },
                              { this shouldNotBe gt(x) })
  }
  
  private infix fun Double.shouldNotMatchGreaterThan(x: Double) {
    this shouldNot beGreaterThan(x)
    this shouldNotBe gt(x)
    this shouldNotBeGreaterThan x
    
    this shouldThrowExceptionOnGreaterThan (x)
  }
  
  private infix fun Double.shouldThrowExceptionOnGreaterThan(x: Double) {
    shouldThrowAssertionError("$this should be > $x",
                              { this should beGreaterThan(x) },
                              { this shouldBe gt(x) },
                              { this shouldBeGreaterThan x })
  }
  
  private infix fun Double.shouldMatchGreaterThanOrEqual(x: Double) {
    this should beGreaterThanOrEqualTo(x)
    this shouldBe gte(x)
    this shouldBeGreaterThanOrEqual x
    
    this shouldThrowExceptionOnNotGreaterThanOrEqual (x)
  }
  
  private infix fun Double.shouldThrowExceptionOnNotGreaterThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should not be >= $x",
                              { this shouldNot beGreaterThanOrEqualTo(x) },
                              { this shouldNotBe gte(x) },
                              { this shouldNotBeGreaterThanOrEqual x })
  }
  
  private infix fun Double.shouldNotMatchGreaterThanOrEqual(x: Double) {
    this shouldNot beGreaterThanOrEqualTo(x)
    this shouldNotBe gte(x)
    this shouldNotBeGreaterThanOrEqual x
    
    this shouldThrowExceptionOnGreaterThanOrEqual (x)
  }
  
  private infix fun Double.shouldThrowExceptionOnGreaterThanOrEqual(x: Double) {
    shouldThrowAssertionError("$this should be >= $x",
                              { this should beGreaterThanOrEqualTo(x) },
                              { this shouldBe gte(x) },
                              { this shouldBeGreaterThanOrEqual x })
  }
}