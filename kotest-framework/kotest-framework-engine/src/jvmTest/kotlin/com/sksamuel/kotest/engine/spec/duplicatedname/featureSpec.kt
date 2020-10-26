package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

abstract class FeatureSpecDuplicateNameTest(iso: IsolationMode) : FeatureSpec() {
   init {
      isolationMode = iso
      feature("foo") {
         scenario("woo") {}
         shouldThrow<DuplicatedTestNameException> {
            scenario("woo") {}
         }.message shouldBe "Cannot create test with duplicated name woo"
      }
      shouldThrow<DuplicatedTestNameException> {
         feature("foo") {}
      }
   }
}

class FeatureSpecSingleInstanceDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.SingleInstance)
class FeatureSpecInstancePerLeafDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerLeaf)
class FeatureSpecInstancePerTestDuplicateNameTest : FeatureSpecDuplicateNameTest(IsolationMode.InstancePerTest)
