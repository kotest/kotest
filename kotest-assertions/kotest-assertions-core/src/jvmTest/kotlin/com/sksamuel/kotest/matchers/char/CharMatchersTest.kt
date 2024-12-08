package com.sksamuel.kotest.matchers.char

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.char.shouldBeEqualToIgnoreCase
import io.kotest.matchers.char.shouldBeInRange
import io.kotest.matchers.char.shouldNotBeEqualToIgnoreCase
import io.kotest.matchers.char.shouldNotBeInRange
import io.kotest.matchers.comparables.shouldBeBetween
import io.kotest.matchers.comparables.shouldNotBeBetween

class CharMatchersTest : StringSpec() {
   init {
      "should be in char range" {
         'd' shouldBeInRange ('a'..'z')
         'E' shouldBeInRange ('A'..'z')
         ']' shouldBeInRange ('A'..'z')
      }

      "should not be in char range" {
         'd' shouldNotBeInRange ('e'..'z')
         'd' shouldNotBeInRange ('a'..'c')
      }

      "should be between from and to char" {
         'd'.shouldBeBetween('a', 'z')
         'E'.shouldBeBetween('A', 'z')
         ']'.shouldBeBetween('A', 'z')
      }

      "should be not between from and to char" {
         'd'.shouldNotBeBetween('e', 'z')
         'd'.shouldNotBeBetween('a', 'c')
      }

      "should be equal ignore case" {
         'c' shouldBeEqualToIgnoreCase 'C'
      }

      "should not be equal ignore case" {
         'c' shouldNotBeEqualToIgnoreCase 'D'
      }
   }
}
