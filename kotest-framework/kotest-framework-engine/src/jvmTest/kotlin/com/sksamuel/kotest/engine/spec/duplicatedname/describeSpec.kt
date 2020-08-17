package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

abstract class DescribeSpecDuplicateNameTest(iso: IsolationMode) : DescribeSpec() {
   init {
      isolationMode = iso
      describe("foo") {
         it("woo") {}
         shouldThrow<DuplicatedTestNameException> {
            it("woo") {}
         }
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
