package io.kotlintest

/**
 * Base class for all tags. If you don't provide a string representation explitely, the name of the
 * derived class will be used.
 */
abstract class Tag(representation: String? = null) {

  val representation: String = representation ?: javaClass.simpleName

  override fun toString() = representation
}