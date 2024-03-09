package com.sksamuel.kotest

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.string.shouldContain
import javax.xml.bind.JAXBElement
import javax.xml.namespace.QName

class JaxbElementTest : FunSpec({
   context("Comparing JAXBElement reflectively works as expected") {
      val jaxbElement = JAXBElement(QName.valueOf("name"), Int::class.java, 123)
      val otherJaxbElement = JAXBElement(QName.valueOf("name"), Int::class.java, 124)

      test("Should pass when comparing equal elements") {
         jaxbElement.shouldBeEqualToComparingFields(jaxbElement)
      }

      test("Should fail when comparing different elements") {
         shouldFail {
            jaxbElement.shouldBeEqualToComparingFields(otherJaxbElement)
         }.message.shouldContain(
            """Value differ at:\s+1\) value:\s+expected:<124> but was:<123>""".toRegex()
         )
      }
   }
})
