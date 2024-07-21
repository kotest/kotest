package io.kotest.assertions.print

import kotlin.reflect.full.declaredMemberProperties

actual fun <A : Any> dataClassPrint(): Print<A> = DataClassPrintJvm()

class DataClassPrintJvm : Print<Any> {

   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: Any): Printed = print(a, 0)

   override fun print(a: Any, level: Int): Printed {
      if (level == 10) return Printed("<...>")
      require(a::class.isData) { "This instance of the Print typeclass only supports data classes" }

      if (a::class.declaredMemberProperties.size <= 2) return Printed(a.toString())
      val props = a::class.declaredMemberProperties
      val maxNameLength = props.maxOfOrNull { it.name.length } ?: 0

      val str = buildString {
         append(a::class.simpleName)
         append("(")
         props.forEach { property ->
            append(System.lineSeparator())
            append("  ")
            append("".padEnd(level * 2, ' '))
            append(property.name.padEnd(maxNameLength, ' '))
            append("  =  ")
            val propertyValue = property.getter.call(a)
            when {
               propertyValue == null -> append(NullPrint.print(null, 0).value)
               propertyValue::class.isData -> append(DataClassPrintJvm().print(propertyValue, level + 1).value)
               else -> append(property.getter.call(a).print(level + 1).value)
            }
         }
         append(System.lineSeparator())
         append("".padEnd(level * 2, ' '))
         append(")")
      }
      return if (level > 0) {
         Printed(str.trim())
      } else {
         Printed(str)
      }
   }
}
