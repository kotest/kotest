package com.sksamuel.kotest.property

import io.kotest.properties.Gen
import io.kotest.properties.string
import io.kotest.property.*
import io.kotest.specs.FreeSpec
import kotlinx.coroutines.delay

class ForAllPropertyTest : FreeSpec() {
   init {
      "forAll" - {
         "two type parameters" - {
            //            "explicit type parameter" {
//               forAll<String, String> { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "explicit test count" {
//               forAll<String, String>(100) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "explicit test count and min successes" {
//               forAll<String, String>(iterations = 100, minSuccess = 70) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "explicit seed" {
//               forAll<String, String>(seed = 15879178243L) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "inferred type parameter" {
//               forAll { a: String, b: String ->
//                  a + b == "$a$b"
//               }
//            }
//            "inferred type parameter and explicit test count" {
//               forAll(iterations = 100) { a: String, b: String ->
//                  a + b == "$a$b"
//               }
//            }
//            "inferred type parameters and explicit shrinking mode" {
//               forAll(iterations = 100, shrinking = ShrinkingMode.Off) { a: String, b: String ->
//                  a + b == "$a$b"
//               }
//            }
//            "function mode shrinking" {
//               shrinking<String, String>(ShrinkingMode.Unbounded) {
//                  forAll<String, String> { a: String, b: String ->
//                     a + b == "$a$b"
//                  }
//               }
//            }
//            "property builder with explicit types and implicit parameters" {
//               property {
//                  forAll<String, String> { a, b ->
//                     a + b == "$a$b"
//                  }
//                  forAll { a: String, b: String ->
//                     b + a == "$a$b"
//                  }
//               }
//            }
//            "property builder with implicit parameters and gens" {
//               property {
//                  forAll(Gen.string(), Gen.string()) { a, b ->
//                     a + b == "$a$b"
//                  }
//               }
//            }
//            "property builder with explicit parameters" {
//               val props = props {
//                  iterations = 1000
//                  minSuccess = 700
//                  seed = 1000
//                  shrinking = ShrinkingMode.Unbounded
//                  generation = Exhaustivity.Auto
//               }
//               property(props) {
//                  forAll<String, String> { a, b ->
//                     a + b == "$a$b"
//                  }
//                  forAll { a: String, b: String ->
//                     b + a == "$a$b"
//                  }
//               }
//               property(shrinking = ShrinkingMode.Off) {
//                  forAll<String, String> { a, b ->
//                     a + b == "$a$b"
//                  }
//                  forAll { a: String, b: String ->
//                     b + a == "$a$b"
//                  }
//               }
//            }
//            "property builder with explicit parameters and gens" {
//               property(iterations = 1000, minSuccess = 700, seed = 1000, shrinking = ShrinkingMode.Unbounded) {
//                  forAll(Gen.string(), Gen.string()) { a, b ->
//                     a + b == "$a$b"
//                  }
//               }
//            }
//            "property builder with explicit parameters and gens and suspend functions" {
//               property(iterations = 1000, minSuccess = 700, seed = 1000, shrinking = ShrinkingMode.Unbounded) {
//                  forAll(Gen.string(), Gen.string()) { a, b ->
//                     delay(100)
//                     a + b == "$a$b"
//                  }
//               }
//            }
//            "explicit generators and implicit types" {
//               forAll(
//                  Gen.string(),
//                  Gen.string(),
//                  ) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "explicit generators and implicit types and explicit iterations" {
//               forAll(
//                  Gen.string(),
//                  Gen.string(),
//                  1000
//               ) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
//            "explicit generators and implicit types and explicit iterations and seed" {
//               forAll(
//                  Gen.string(),
//                  Gen.string(),
//                  1000,
//                  seed = 989128312L
//               ) { a, b ->
//                  a + b == "$a$b"
//               }
//            }
         }
      }
   }
}
