package io.kotlintest.properties

import java.util.concurrent.ConcurrentHashMap

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
class PropertyContext {

  private var attempts = 0
  private val counts = ConcurrentHashMap<String, Int>()

  fun attempts(): Int = attempts

  fun classificationCounts(): Map<String, Int> = counts.toMap()

  fun inc() {
    attempts++
  }

  fun classify(condition: Boolean, trueLabel: String) {
    if (condition) {
      val current = counts.getOrElse(trueLabel, { 0 })
      counts[trueLabel] = current + 1
    }
  }

  fun classify(condition: Boolean, trueLabel: String, falseLabel: String) {
    if (condition) {
      classify(condition, trueLabel)
    } else {
      classify(!condition, falseLabel)
    }
  }
}