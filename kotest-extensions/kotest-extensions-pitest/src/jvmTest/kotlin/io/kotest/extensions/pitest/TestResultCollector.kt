package io.kotest.extensions.pitest

import org.pitest.testapi.Description
import org.pitest.testapi.ResultCollector

class TestResultCollector : ResultCollector {

  val skipped = mutableListOf<Description>()
  val started = mutableListOf<Description>()
  val ended = mutableListOf<Description>()
  val failures = mutableListOf<Throwable>()

  override fun notifyEnd(description: Description, t: Throwable) {
    failures.add(t)
    notifyEnd(description)
  }

  override fun notifyEnd(description: Description) {
    ended.add(description)
  }

  override fun notifyStart(description: Description) {
    started.add(description)
  }

  override fun notifySkipped(description: Description) {
    skipped.add(description)
  }

  override fun shouldExit(): Boolean {
    return false
  }
}
