package com.sksamuel.kotest.example.allure

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeExactly

@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
class SquareTest : FunSpec({
   test("positive square") {
      Math.pow(2.0, 2.0) shouldBeExactly 4.0
      Math.pow(4.0, 2.0) shouldBeExactly 16.0
      Math.pow(9.0, 2.0) shouldBeExactly 81.0
   }
   test("negative square") {
      Math.pow(-2.0, 2.0) shouldBeExactly 4.0
      Math.pow(-4.0, 2.0) shouldBeExactly 16.0
      Math.pow(-9.0, 2.0) shouldBeExactly 81.0
   }
})

class CubeTest : DescribeSpec({
   describe("when cubing a number") {
      it("should be the cube value") {
         Math.pow(2.0, 3.0) shouldBeExactly 8.0
         Math.pow(-4.0, 3.0) shouldBeExactly -64.0
         Math.pow(9.0, 3.0) shouldBeExactly 729.0
      }
   }
})
