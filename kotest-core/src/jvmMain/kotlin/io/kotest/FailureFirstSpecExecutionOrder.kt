package io.kotest

import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * An implementation of [SpecExecutionOrder] which will run specs that
 * contained a failed test on a previous run first, before specs where
 * all the tests passed.
 */
object FailureFirstSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
    // try to locate a folder called .kotest which should contain a file called spec_failures
    // each line in this file is a failed spec and they should run first
    // if the file doesn't exist then we just execute in Lexico order
    val path = Paths.get(".kotest").resolve("spec_failures")
    return if (path.toFile().exists()) {
      val classnames = path.toFile().readLines()
      val (failed, passed) = classes.partition { classnames.contains(it.qualifiedName) }
      LexicographicSpecExecutionOrder.sort(failed) + LexicographicSpecExecutionOrder.sort(
        passed)
    } else {
      LexicographicSpecExecutionOrder.sort(classes)
    }
  }
}
