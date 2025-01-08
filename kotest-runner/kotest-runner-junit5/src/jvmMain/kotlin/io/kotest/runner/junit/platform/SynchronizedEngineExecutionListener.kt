package io.kotest.runner.junit.platform

import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry

internal class SynchronizedEngineExecutionListener(val listener: EngineExecutionListener) : EngineExecutionListener {

  override fun executionFinished(testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult?) {
    synchronized(listener) {
      listener.executionFinished(testDescriptor, testExecutionResult)
    }
  }

  override fun reportingEntryPublished(testDescriptor: TestDescriptor, entry: ReportEntry?) {
    synchronized(listener) {
      listener.reportingEntryPublished(testDescriptor, entry)
    }
  }

  override fun executionSkipped(testDescriptor: TestDescriptor, reason: String?) {
    synchronized(listener) {
      listener.executionSkipped(testDescriptor, reason)
    }
  }

  override fun executionStarted(testDescriptor: TestDescriptor) {
    synchronized(listener) {
      listener.executionStarted(testDescriptor)
    }
  }

  override fun dynamicTestRegistered(testDescriptor: TestDescriptor) {
    synchronized(listener) {
      listener.dynamicTestRegistered(testDescriptor)
    }
  }
}
