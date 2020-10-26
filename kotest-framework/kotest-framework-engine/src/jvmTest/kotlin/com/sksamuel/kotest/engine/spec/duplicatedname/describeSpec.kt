package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

abstract class DescribeSpecDuplicateNameTest(iso: IsolationMode) : DescribeSpec() {
   init {
      isolationMode = iso
      describe("foo") {
         it("woo") {}
         shouldThrow<DuplicatedTestNameException> {
            it("woo") {}
         }.message shouldBe "Cannot create test with duplicated name woo"
      }
      shouldThrow<DuplicatedTestNameException> {
         describe("foo") {}
      }
      context("goo") {}
      shouldThrow<DuplicatedTestNameException> {
         context("goo") {}
      }
   }
}

class DescribeSpecSingleInstanceDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.SingleInstance)
class DescribeSpecInstancePerLeafDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class DescribeSpecInstancePerTestDuplicateNameTest : DescribeSpecDuplicateNameTest(IsolationMode.InstancePerTest)
