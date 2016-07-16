package io.kotlintest


/**
 * Base class for all tags. The simple name (without packages) of the singleton derived from this
 * class will be used as name. Two tag object with the same simple name (same object names in
 * different packages) are treated as a single tag.
 */
abstract class Tag() {

  /**
   * Simple name of the singleton/class derived from this class.
   */
  val name: String = javaClass.simpleName

  /**
   * Same as [name].
   */
  override fun toString() = name
}