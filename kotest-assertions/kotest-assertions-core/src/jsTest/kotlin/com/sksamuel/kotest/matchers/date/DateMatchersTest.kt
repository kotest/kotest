@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.shouldHaveDayOfMonth
import io.kotest.matchers.date.shouldHaveYear
import kotlin.js.Date

class DateMatchersTest : StringSpec({
   "Date shouldHaveYear (pins the corrected KDoc example)" {
      Date(2019, 2, 15, 12, 0, 0, 0).shouldHaveYear(2019)
      shouldFail {
         Date(2019, 2, 15, 12, 0, 0, 0).shouldHaveYear(2020)
      }
   }

   "Date shouldHaveDayOfMonth (pins the corrected KDoc example)" {
      Date(2019, 2, 15, 12, 0, 0, 0).shouldHaveDayOfMonth(15)
      shouldFail {
         Date(2019, 2, 15, 12, 0, 0, 0).shouldHaveDayOfMonth(16)
      }
   }
})
