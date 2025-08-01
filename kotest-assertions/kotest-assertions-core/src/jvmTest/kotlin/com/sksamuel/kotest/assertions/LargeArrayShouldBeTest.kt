package com.sksamuel.kotest.assertions

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// see Ever so slightly verbose output when comparing two arrays #1236 https://github.com/kotest/kotest/issues/1236
@EnabledIf(LinuxOnlyGithubCondition::class)
class LargeArrayShouldBeTest : FunSpec({
   test("comparing large arrays") {
      val a = listOf(
         -1,
         -40,
         -1,
         -32,
         0,
         16,
         74,
         70,
         73,
         70,
         0,
         1,
         2,
         0,
         0,
         1,
         0,
         1,
         0,
         0,
         -1,
         -37,
         0,
         67,
         0,
         8,
         6,
         6,
         7,
         6,
         5,
         8,
         7,
         7,
         7,
         9,
         9,
         8,
         10,
         12,
         20,
         13,
         12,
         11,
         11,
         12,
         25,
         18,
         19,
         15,
         20,
         29,
         26,
         31,
         30,
         29,
         26,
         28,
         28,
         32,
         36,
         46,
         39,
         32,
         34,
         44,
         35,
         28,
         28,
         40,
         55,
         41,
         44,
         48,
         49,
         52,
         52,
         52,
         31,
         39,
         57,
         61,
         56,
         50,
         60,
         46,
         51,
         52,
         50,
         -1,
         -37,
         0,
         67,
         1,
         9,
         9,
         9,
         12,
         11,
         12,
         24,
         13,
         13,
         24,
         50,
         33,
         28,
         33,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         -1,
         -62,
         0,
         17,
         8,
         2,
         88,
         1,
         -62,
         3,
         1,
         34,
         0,
         2,
         17,
         1,
         3,
         17,
         1,
         -1,
         -60,
         0,
         27,
         0,
         0,
         2,
         3,
         1,
         1,
         1,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         2,
         3,
         0,
         1,
         4,
         5,
         6,
         7,
         -1,
         -60,
         0,
         24,
         1,
         1,
         1,
         1,
         1,
         1,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         1,
         2,
         3,
         4,
         -1,
         -38,
         0,
         12,
         3,
         1,
         0,
         2,
         16,
         3,
         113,
         -102,
         58,
         35,
         125,
         -50,
         -51,
         -100,
         93,
         30,
         -117,
         61,
         -93,
         -78,
         69,
         -110,
         69,
         -110,
         67,
         52,
         -45,
         36,
         -8,
         -5,
         -61,
         103,
         29,
         90,
         -112,
         -3,
         73,
         86,
         -14,
         -13,
         -81,
         74,
         105,
         -47,
         -109,
         -95,
         -122,
         -121,
         -93,
         -87,
         -39,
         -125,
         -103,
         -77,
         -101,
         -112,
         83,
         27,
         -87,
         -40,
         -47,
         -62,
         -12,
         -101,
         106,
         -42,
         119,
         44,
         -110,
         44,
         -110,
         18,
         72,
         12,
         -85,
         51,
         -81,
         97,
         18,
         72,
         73,
         33,
         37,
         89,
         4,
         -88,
         -52,
         -44,
         -13,
         -45,
         -106,
         73,
         8,
         37,
         5,
         52,
         12,
         -126,
         80,
         -30,
         -15,
         61,
         23,
         -99,
         -111,
         123,
         112,
         20,
         47,
         -98,
         -16,
         77,
         123,
         120,
         -70,
         -21,
         -81,
         -111,
         68,
         -102,
         -79,
         118,
         -71,
         5
      )

      val b = listOf(
         0,
         1,
         0,
         0,
         -1,
         -37,
         0,
         67,
         0,
         8,
         6,
         6,
         7,
         6,
         5,
         8,
         7,
         7,
         7,
         9,
         9,
         8,
         10,
         12,
         20,
         13,
         12,
         11,
         11,
         12,
         25,
         18,
         19,
         15,
         20,
         29,
         26,
         31,
         30,
         29,
         26,
         28,
         28,
         32,
         36,
         46,
         39,
         32,
         34,
         44,
         35,
         28,
         28,
         40,
         55,
         41,
         44,
         48,
         49,
         52,
         52,
         52,
         31,
         39,
         57,
         61,
         56,
         50,
         60,
         46,
         51,
         52,
         50,
         -1,
         -37,
         0,
         67,
         1,
         9,
         9,
         9,
         12,
         11,
         12,
         24,
         13,
         13,
         24,
         50,
         33,
         28,
         33,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         50,
         -1,
         -62,
         0,
         17,
         8,
         2,
         88,
         1,
         -62,
         3,
         1,
         34,
         0,
         2,
         17,
         1,
         3,
         17,
         1,
         -1,
         -60,
         0,
         27,
         0,
         0,
         2,
         3,
         1,
         1,
         1,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         2,
         3,
         0,
         1,
         4,
         5,
         6,
         7,
         -1,
         -60,
         0,
         24,
         1,
         1,
         1,
         1,
         1,
         1,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         1,
         2,
         3,
         4,
         -1,
         -38,
         0,
         12,
         3,
         1,
         0,
         2,
         16,
         3,
         113,
         -102,
         58,
         35,
         125,
         -50,
         -51,
         -100,
         93,
         30,
         -117,
         61,
         -93,
         -78,
         69,
         -110,
         69,
         -110,
         67,
         52,
         -45,
         36,
         -8,
         -5,
         -61,
         103,
         29,
         90,
         -112,
         -3,
         73,
         86,
         -14,
         -13,
         -81,
         74,
         105,
         -47,
         -109,
         -95,
         -122,
         -121,
         -93,
         -87,
         -39,
         -125,
         -103,
         -77,
         -101,
         -112,
         83,
         27,
         -87,
         -40,
         -47,
         -62,
         -12,
         -101,
         106,
         -42,
         119,
         44,
         -110,
         44,
         -110,
         18,
         72,
         12,
         -85,
         51,
         -81,
         97,
         18,
         72,
         73,
         33,
         37,
         89,
         4,
         -88,
         -52,
         -44,
         -13,
         -45,
         -106,
         73,
         8,
         37,
         5,
         52,
         12,
         -126,
         80,
         -30,
         -15,
         61,
         23,
         -99,
         -111,
         123,
         112,
         20,
         47,
         -98,
         -16,
         77,
         123,
         120,
         -70,
         -21,
         -81,
         -111,
         68,
         -102,
         -79,
         118,
         -71,
         5
      )

      shouldThrowAny {
         a shouldBe b
      }.message shouldBe """Element differ at index: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, ...and 279 more (set 'kotest.assertions.collection.print.size' to see more / less items)]
                            |Unexpected elements from index 359
                            |expected:<[0, 1, 0, 0, -1, -37, 0, 67, 0, 8, 6, 6, 7, 6, 5, 8, 7, 7, 7, 9, ...and 324 more (set 'kotest.assertions.collection.print.size' to see more / less items)]> but was:<[-1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 2, 0, 0, 1, 0, 1, 0, 0, ...and 340 more (set 'kotest.assertions.collection.print.size' to see more / less items)]>""".trimMargin()
   }
})
