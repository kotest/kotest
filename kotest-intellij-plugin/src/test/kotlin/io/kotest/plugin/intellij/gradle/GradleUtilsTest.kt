package io.kotest.plugin.intellij.gradle

import io.kotest.matchers.shouldBe
import org.junit.Test

class GradleUtilsTest {

   @Test
   fun `isKotest614OrAbove returns false for major below 6`() {
      GradleUtils.isKotest614OrAbove(Version(5, 9, 9)) shouldBe false
      GradleUtils.isKotest614OrAbove(Version(4, 0, 0)) shouldBe false
   }

   @Test
   fun `isKotest614OrAbove returns false for 6_0_x`() {
      GradleUtils.isKotest614OrAbove(Version(6, 0, 0)) shouldBe false
      GradleUtils.isKotest614OrAbove(Version(6, 0, 9)) shouldBe false
   }

   @Test
   fun `isKotest614OrAbove returns false for 6_1_x below patch 4`() {
      GradleUtils.isKotest614OrAbove(Version(6, 1, 0)) shouldBe false
      GradleUtils.isKotest614OrAbove(Version(6, 1, 3)) shouldBe false
   }

   @Test
   fun `isKotest614OrAbove returns true for 6_1_4 and above`() {
      GradleUtils.isKotest614OrAbove(Version(6, 1, 4)) shouldBe true
      GradleUtils.isKotest614OrAbove(Version(6, 1, 9)) shouldBe true
   }

   @Test
   fun `isKotest614OrAbove returns true for 6_2 and above`() {
      GradleUtils.isKotest614OrAbove(Version(6, 2, 0)) shouldBe true
      GradleUtils.isKotest614OrAbove(Version(6, 9, 0)) shouldBe true
   }

   @Test
   fun `isKotest614OrAbove returns true for major 7 and above`() {
      GradleUtils.isKotest614OrAbove(Version(7, 0, 0)) shouldBe true
      GradleUtils.isKotest614OrAbove(Version(8, 0, 0)) shouldBe true
   }
}
