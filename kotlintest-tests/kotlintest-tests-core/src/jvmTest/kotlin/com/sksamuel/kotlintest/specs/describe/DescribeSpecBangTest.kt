package com.sksamuel.kotlintest.specs.describe

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.DescribeSpec

class DescribeSpecBangTest : DescribeSpec() {

  init {

    describe("!BangedDescribe") {
      attemptToFail()
    }

    describe("NonBangedDescribe") {
      it("!BangedIt") {
        attemptToFail()
      }

      context("!BangedContext") {
        attemptToFail()
      }

      context("NonBangedContext") {
        it("!BangedIt") {
          attemptToFail()
        }
      }
    }

  }

}