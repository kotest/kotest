package com.sksamuel.kotest.runner.junit4

import android.app.Instrumentation
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.INSTRUMENTATION_INCLUDE_PATTERN_ARG
import io.kotest.runner.junit4.InstrumentationFilter
import io.mockk.every
import io.mockk.mockk

class InstrumentationFilterTest : FreeSpec() {
   init {
      "InstrumentationFilter should pick up args from the InstrumentationRegistry" {
         val pattern = "com.sksamuel.kotest.runner.junit4.InstrumentationFilterTest.a -- b"

         val i = Instrumentation()
         val bundle = mockk<Bundle>().also {
            every { it.getString(INSTRUMENTATION_INCLUDE_PATTERN_ARG) } returns pattern
         }
         InstrumentationRegistry.registerInstance(i, bundle)

         val spec = InstrumentationFilterTest::class.toDescriptor()
         val test1 = spec.append("a")
         val test2 = test1.append("b")
         val test3 = spec.append("c")
         val test4 = test1.append("d")

         InstrumentationFilter.filter(pattern, test1) shouldBe DescriptorFilterResult.Include
         InstrumentationFilter.filter(pattern, test2) shouldBe DescriptorFilterResult.Include
         InstrumentationFilter.filter(pattern, test3) shouldBe DescriptorFilterResult.Exclude(null)
         InstrumentationFilter.filter(pattern, test4) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }
}
