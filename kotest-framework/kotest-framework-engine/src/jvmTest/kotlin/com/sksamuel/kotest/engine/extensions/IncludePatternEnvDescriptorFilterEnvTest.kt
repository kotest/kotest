package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.IncludePatternEnvDescriptorFilter
import io.kotest.matchers.shouldBe

private fun withEnv(key: String, value: String, block: () -> Unit) {
   val systemEnv = System.getenv()
   @Suppress("UNCHECKED_CAST")
   val map = systemEnv.javaClass.getDeclaredField("m")
      .also { it.isAccessible = true }
      .get(systemEnv) as MutableMap<String, String>
   val prev = map[key]
   map[key] = value
   try {
      block()
   } finally {
      if (prev == null) map.remove(key) else map[key] = prev
   }
}

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class IncludePatternEnvDescriptorFilterEnvTest : FunSpec({

   val spec = IncludePatternEnvDescriptorFilterEnvTest::class.toDescriptor()
   val fqcn = "com.sksamuel.kotest.engine.extensions.IncludePatternEnvDescriptorFilterEnvTest"

   test("no env var set includes everything") {
      IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Include
   }

   test("single pattern matching spec is included") {
      withEnv(INCLUDE_PATTERN_ENV, "$fqcn") {
         IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Include
      }
   }

   test("single pattern not matching spec is excluded") {
      withEnv(INCLUDE_PATTERN_ENV, "com.other.SomeSpec") {
         IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   test("multiple patterns joined by semicolon - first pattern matches") {
      withEnv(INCLUDE_PATTERN_ENV, "$fqcn;com.other.SomeSpec") {
         IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Include
      }
   }

   test("multiple patterns joined by semicolon - second pattern matches") {
      withEnv(INCLUDE_PATTERN_ENV, "com.other.SomeSpec;$fqcn") {
         IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Include
      }
   }

   test("multiple patterns joined by semicolon - neither matches") {
      withEnv(INCLUDE_PATTERN_ENV, "com.other.SomeSpec;com.other.AnotherSpec") {
         IncludePatternEnvDescriptorFilter.filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   test("multiple patterns joined by semicolon - test descriptor matched by first pattern") {
      val test = spec.append("my test")
      withEnv(INCLUDE_PATTERN_ENV, "$fqcn.my test;com.other.AnotherSpec") {
         IncludePatternEnvDescriptorFilter.filter(test) shouldBe DescriptorFilterResult.Include
      }
   }

   test("multiple patterns joined by semicolon - test descriptor matched by second pattern") {
      val test = spec.append("my test")
      withEnv(INCLUDE_PATTERN_ENV, "com.other.AnotherSpec;$fqcn.my test") {
         IncludePatternEnvDescriptorFilter.filter(test) shouldBe DescriptorFilterResult.Include
      }
   }

   test("multiple patterns joined by semicolon - test descriptor not matched by any") {
      val test = spec.append("my test")
      withEnv(INCLUDE_PATTERN_ENV, "com.other.AnotherSpec;$fqcn.other test") {
         IncludePatternEnvDescriptorFilter.filter(test) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }
})
