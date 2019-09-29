package io.kotest

import io.kotest.assertions.classname


/**
 * Base class for all tags. The simple name (without packages) of the singleton derived from this
 * class will be used as name. Two tag object with the same simple name (same object names in
 * different packages) are treated as a single tag.
 *
 * For example, if you create a Tag `com.sksamuel.kotest.SuperTag` then the tag name will
 * simply be SuperTag.
 *
 * Therefore, the tags `com.sksamuel.kotest.SuperTag` and `io.kotest.SuperTag` are
 * considered equal.
 */
abstract class Tag {

   /**
    * Simple name of the singleton/class derived from this class.
    */
   open val name: String = this::class.classname()

   /**
    * Same as [name].
    */
   override fun toString() = name

   companion object {
      operator fun invoke(name: String): StringTag = StringTag(name)
   }
}


class StringTag(override val name: String) : Tag()
