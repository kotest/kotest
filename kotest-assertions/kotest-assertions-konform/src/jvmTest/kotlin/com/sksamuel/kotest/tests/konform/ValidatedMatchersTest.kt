package com.sksamuel.kotest.tests.konform

import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.maximum
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.minimum
import io.kotest.assertions.konform.shouldBeInvalid
import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.konform.shouldContainError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class ValidatedMatchersTest : StringSpec({
   val validateUser = Validation<UserProfile> {
      UserProfile::fullName {
         minLength(2)
         maxLength(100)
      }

      UserProfile::age ifPresent {
         minimum(0)
         maximum(150)
      }
   }

   "shouldBeValid" {
      val validUser = UserProfile("Alice", 25)

      validateUser shouldBeValid validUser
      shouldThrow<AssertionError> {
         validateUser shouldBeInvalid validUser
      }
   }

   "shouldNotBeValid" {
      val invalidUser = UserProfile("A", -1)

      validateUser.shouldBeInvalid(invalidUser) {
         it.shouldContainError(UserProfile::fullName, "must have at least 2 characters")
         it.shouldContainError(UserProfile::age, "must be at least '0'")
      }
      shouldThrow<AssertionError> {
         validateUser shouldBeValid invalidUser
      }
   }

   "shouldNotBeValidBook_MissedAssertion" {
      val invalidUser = UserProfile("Alice", -1)

      validateUser shouldBeInvalid invalidUser

      validateUser.shouldBeInvalid(invalidUser) {
         println(it)
         it.shouldContainError(UserProfile::age, "must be at least '0'")
         // original implementation of shouldContainError() fails to
         // spot this and complain that this error is missing
         shouldThrow<AssertionError> {
            it.shouldContainError(UserProfile::fullName, "must have at least 2 characters")
         }
      }
   }

   val validateBook = Validation<Book> {
      Book::title {
         maxLength(30)
      }

      Book::author {
         UserProfile::fullName {
            minLength(2)
            maxLength(100)
         }

         UserProfile::age ifPresent {
            minimum(0)
            maximum(150)
         }
      }
   }

   "shouldNotBeValidBook_DeepField" {
      val invalidBook = Book(
         title = "Wonderland",
         author = UserProfile("", 25)
      )

      validateBook.shouldBeInvalid(invalidBook) {
         println(it)
         // the following assertion just doesn't work because of the propertyPath
         // is too shallow:
         // it.shouldContainError(UserProfile::fullName, "must have at least 2 characters")
         // so I propose an overloaded implementation that takes a collection of propertyPaths:
         it.shouldContainError(listOf(Book::author, UserProfile::fullName), "must have at least 2 characters")
      }
   }

})

private data class UserProfile(
   val fullName: String,
   val age: Int?
)

private data class Book(
   val title: String,
   val author: UserProfile,
)
