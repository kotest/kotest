package io.kotest.core

/**
 * Base class for all tags.
 *
 * All tags have a "name" parameter which is used as the tag's value when evaluating
 * tag expressions at runtime.
 *
 * To create a tag, we can use the [NamedTag] class or subclass [Tag] with an object singleton.
 *
 * When subclassing [Tag], the simple name (without packages) of the singleton derived from this
 * class will be used as name. Two tag objects with the same simple name (same class name in
 * different packages) are treated as the same single tag.
 *
 * For example, if you create a Tag `com.sksamuel.kotest.SuperTag` then the tag name will
 * simply be `SuperTag`.
 *
 * Therefore, the tags `com.sksamuel.kotest.SuperTag` and `io.kotest.SuperTag` would be
 * considered equal.
 * */
abstract class Tag {

   /**
    * Simple name of the singleton/class derived from this class.
    */
   open val name: String = this::class.simpleName ?: error("Cannot derive name of class for tag")

   /**
    * Same as [name].
    */
   override fun toString() = name

   companion object {
      operator fun invoke(name: String): NamedTag =
         NamedTag(name)
   }
}

/**
 * Creates a tag using the given parameter as the name.
 */
data class NamedTag(override val name: String) : Tag() {
   // Don't use toString from `data class`
   override fun toString() = super.toString()
}
