package io.kotest.assertions.print

import kotlin.reflect.full.declaredMemberProperties

actual fun <A : Any> dataClassPrint(): Print<A> = DataClassPrintJvm()

class DataClassPrintJvm : Print<Any> {

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
            append(indent(level + 1))
            append(property.name.padEnd(maxNameLength, ' '))
            append("  =  ")
            val propertyValue = property.getter.call(a)
            when {
               propertyValue == null -> append(NullPrint.print(null, 0).value)
               propertyValue::class.isData -> append(DataClassPrintJvm().print(propertyValue, level + 1).value)
               else -> {
                  // remove leading/trailing whitespace, since we already applied indent on this line
                  append(property.getter.call(a).print(level + 1).value.trim())
               }
            }
         }
         append(System.lineSeparator())
         append(indent(level))
         append(")")
      }
      return if (level > 0) {
         Printed(str.trim())
      } else {
         Printed(str)
      }
   }
}
