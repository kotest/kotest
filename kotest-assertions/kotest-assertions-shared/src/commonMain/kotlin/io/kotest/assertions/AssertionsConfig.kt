package io.kotest.assertions

import io.kotest.mpp.sysprop

object AssertionsConfig {
   val showDataClassDiff: Boolean
      get() = sysprop("kotest.assertions.show-data-class-diffs","true").toBoolean()
}
