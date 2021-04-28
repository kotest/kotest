package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec

class FunSpecExampleTest : FunSpec({

   test("a test") {
   }

   test("a test with config").config(enabled = true) {
   }

   xtest("an xtest") {
   }

   xtest("an xtest with config").config(enabled = true) {
   }

   context("some context") {

      test("a nested test") {
      }

      test("a nested test with config").config(enabled = true) {
      }

      xtest("a nested xtest") {
      }

      xtest("a nested xtest with config").config(enabled = true) {
      }

      context("a nested context") {

         test("a test") {
         }

         xcontext("a nested xcontext") {
            test("a test") {
            }
            context("a nested context") {
               test("a test") {
               }
            }
         }
      }

      xcontext("an xcontext") {
         test("a test") {
         }
         xtest("an xtest") {

         }
         xcontext("a nested xcontext") {
            test("a test") {
            }
         }
      }
   }

   xcontext("an xcontext") {
      test("a test") {
      }
      context("a nested xcontext") {
         test("a test") {
         }
      }
   }

})
