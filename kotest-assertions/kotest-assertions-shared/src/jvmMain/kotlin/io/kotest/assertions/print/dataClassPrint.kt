package io.kotest.assertions.print

import kotlin.reflect.full.declaredMemberProperties

actual fun <A : Any> dataClassPrint(): Print<A> = object : Print<A> {

   override fun print(a: A): Printed = print(a, 0)

   override fun print(a: A, level: Int): Printed {
      if (level == 10) return Printed("<...>")
      require(a::class.isData) { "This instance of the Print typeclass only supports data classes" }
      if (a::class.declaredMemberProperties.size <= 4) return Printed(a.toString())
      val str = buildString {
         append(a::class.simpleName)
         append("(")
         a::class.declaredMemberProperties.forEach { property ->
            append(System.lineSeparator())
            append("- ${property.name}: ")
            val propertyValue = property.getter.call(a)
            when {
               propertyValue == null -> append(NullPrint.print(null, 0).value)
               propertyValue::class.isData -> append(propertyValue.print(level + 1).value)
               else -> append(property.getter.call(a).print(level + 1).value)
            }
         }
         append(System.lineSeparator())
         append(")")
      }
      return if (level > 0) {
         Printed(str.replaceIndent("  ").trim())
      } else {
         Printed(str)
      }
   }
}
