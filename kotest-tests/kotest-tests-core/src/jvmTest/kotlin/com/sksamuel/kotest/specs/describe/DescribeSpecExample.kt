package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class DescribeSpecExample : DescribeSpec() {
  init {

    describe("some thing") {
      it("test name") {
        // test here
      }
      context("with some context") {
        it("test name") {
          // test here
        }
        it("test name 2").config(enabled = false) {
          // test here
        }
        context("with some context") {
          it("test name") {
            // test here
          }
          it("test name 2").config(timeout = 1512.milliseconds) {
            // test here
          }
        }
      }
    }

    describe("some other thing") {
      context("with some context") {
        it("test name") {
          // test here
        }
        it("test name 2").config(enabled = true) {
          // test here
        }
        context("with some context") {
          it("test name") {
            // test here
          }
          it("test name 2").config(timeout = 1512.milliseconds) {
            // test here
          }
        }
      }
    }
  }
}

