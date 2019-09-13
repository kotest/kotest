package com.sksamuel.kotest.specs.describe

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.DescribeSpec

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