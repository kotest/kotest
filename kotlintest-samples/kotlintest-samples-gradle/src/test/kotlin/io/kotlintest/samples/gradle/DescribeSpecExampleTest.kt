package io.kotlintest.samples.gradle

import io.kotlintest.matchers.haveLength
import io.kotlintest.specs.DescribeSpec
import io.kotlintest.specs.ExpectSpec
import io.kotlintest.specs.WordSpec

class DescribeSpecExampleTest : DescribeSpec() {
    init {

        describe("some thing") {
            it("test name") {
                // test here
            }
            context("with some context") {
                it("test name") {
                    // test here
                }
                it("test name 2").config(invocations = 2) {
                    // test here
                }
                context("with some context") {
                    it("test name") {
                        // test here
                    }
                    it("test name 2").config(invocations = 2) {
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
                it("test name 2").config(invocations = 2) {
                    // test here
                }
                context("with some context") {
                    it("test name") {
                        // test here
                    }
                    it("test name 2").config(invocations = 2) {
                        // test here
                    }
                }
            }
        }
    }
}