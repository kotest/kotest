package io.kotest.assertions

import io.kotest.mpp.sysprop

object AssertionsConfig {
   val showDataClassDiff: Boolean
      get() = sysprop("kotest.assertions.show-data-class-diffs", "true").toBoolean()

   val largeStringDiffMinSize: Int
      get() = sysprop("kotest.assertions.multi-line-diff-size", "50").toInt()

   val multiLineDiff: String
      get() = sysprop("kotest.assertions.multi-line-diff", "")

   val maxErrorsOutput: Int
      get() = (sysprop("kotest.assertions.output.max") ?: sysprop("kotlintest.assertions.output.max"))?.toInt() ?: 10
}
