package com.sksamuel.kotest.engine.test.names

import io.kotest.core.Tag
import io.kotest.core.annotation.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.config.TestCaseConfig
import io.kotest.core.test.TestType
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.matchers.shouldBe

@Isolate
class DefaultDisplayNameFormatterTest : FunSpec() {
   init {

      test("@DisplayName should be used for spec name") {
         DefaultDisplayNameFormatter(configuration).format(SpecWithDisplayName::class) shouldBe "ZZZZZ"
      }

      test("test name should use full path option") {
         val conf = Configuration()
         conf.displayFullTestPath = true
         val tc1 = TestCase(
            SpecWithTag::class.toDescriptor().append("test"),
            TestName("test"),
            SpecWithTag(),
            {},
            sourceRef(),
            TestType.Test,
         )
         val tc2 = TestCase(
            SpecWithTag::class.toDescriptor().append("test2"),
            TestName("test2"),
            SpecWithTag(),
            {},
            sourceRef(),
            TestType.Test,
            parent = tc1
         )
         DefaultDisplayNameFormatter(conf).format(tc2) shouldBe "test test2"
      }

      test("tags should be appended from spec when configuration is set") {
         configuration.testNameAppendTags = true

         val tc = TestCase(
            SpecWithTag::class.toDescriptor().append("test"),
            TestName("test"),
            SpecWithTag(),
            {},
            sourceRef(),
            TestType.Test,
         )
         DefaultDisplayNameFormatter(configuration).format(tc) shouldBe "test[tags = Foo]"
         configuration.testNameAppendTags = false
      }

      test("tags should be appended from test when configuration is set") {
         configuration.testNameAppendTags = true

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("test"),
            name = TestName("test"),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(tags = setOf(Dummy, NoUse))
         )
         DefaultDisplayNameFormatter(configuration).format(tc) shouldBe "test[tags = Dummy, NoUse]"
         configuration.testNameAppendTags = false
      }

      test("tags should be appended from test and spec when configuration is set") {
         configuration.testNameAppendTags = true

         val tc = TestCase(
            descriptor = SpecWithTag::class.toDescriptor().append("test"),
            name = TestName("test"),
            spec = SpecWithTag(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(tags = setOf(Dummy, NoUse))
         )
         DefaultDisplayNameFormatter(configuration).format(tc) shouldBe "test[tags = Dummy, NoUse, Foo]"
         configuration.testNameAppendTags = false
      }

      test("bang should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithTag::class.toDescriptor().append("!test"),
            name = TestName("!test"),
            spec = SpecWithTag(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(configuration).format(tc) shouldBe "test"
      }

      test("focus should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithTag::class.toDescriptor().append("f:test"),
            name = TestName("f:test"),
            spec = SpecWithTag(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(configuration).format(tc) shouldBe "test"
      }
   }
}

object Dummy : Tag()
object NoUse : Tag()

@DisplayName("ZZZZZ")
private class SpecWithDisplayName : FunSpec({
   test("a") { }
})

@Tags("Foo")
private class SpecWithTag : FunSpec({
   test("a") { }
})
