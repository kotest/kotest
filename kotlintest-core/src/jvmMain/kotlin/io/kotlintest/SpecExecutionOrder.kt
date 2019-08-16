package io.kotlintest

import java.nio.file.Paths
import kotlin.reflect.KClass

interface SpecExecutionOrder {
  fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.sortedBy { it.simpleName }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
object RandomSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.shuffled()
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs that
 * contained a failed test on a previous run first, before specs where
 * all the tests passed.
 */
object FailureFirstSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
    // try to locate a folder called .kotlintest which should contain a file called spec_failures
    // each line in this file is a failed spec and they should run first
    // if the file doesn't exist then we just execute in Lexico order
    val path = Paths.get(".kotlintest").resolve("spec_failures")
    return if (path.toFile().exists()) {
      val classnames = path.toFile().readLines()
      val (failed, passed) = classes.partition { classnames.contains(it.qualifiedName) }
      LexicographicSpecExecutionOrder.sort(failed) + LexicographicSpecExecutionOrder.sort(passed)
    } else {
      LexicographicSpecExecutionOrder.sort(classes)
    }
  }
}