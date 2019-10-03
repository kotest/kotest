package io.kotest.assertions.jsoup

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Elements.shouldBePresent() = this should bePresent()
fun Elements.shouldNotBePresent() = this shouldNot bePresent()
fun bePresent() = object: Matcher<Elements> {
   override fun test(value: Elements) = MatcherResult(
      value.isNotEmpty(),
      "Element should be present",
      "Element should not be present"
   )
}

infix fun Elements.shouldBePresent(times: Int) = this should bePresent(times)
infix fun Elements.shouldNotBePresent(times: Int) = this shouldNot bePresent(times)
fun bePresent(times: Int) = object: Matcher<Elements> {
   override fun test(value: Elements) = MatcherResult(
      value.size == times,
      "Element should be present $times times",
      "Element should not be present $times times"
   )
}

infix fun Element.shouldHaveText(expectedText: String) = this should haveText(expectedText)
infix fun Element.shouldNotHaveText(expectedText: String) = this shouldNot haveText(expectedText)
fun haveText(expectedText: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.text() == expectedText,
      "Element ${value.tagName()} should have text $expectedText. But instead was $expectedText",
      "Element ${value.tagName()} should not have text $expectedText"
   )
}

infix fun Element.shouldHaveClass(className: String) = this should haveClass(className)
infix fun Element.shouldNotHaveClass(className: String) = this shouldNot haveClass(className)
fun haveClass(className: String) = object: Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.hasClass(className),
      "Element ${value.tagName()} should have class $className.",
      "Element ${value.tagName()} should not have text $className."
   )
}

infix fun Element.shouldHaveAttribute(attrName: String) = this should haveAttribute(attrName)
infix fun Element.shouldNotHaveAttribute(attrName: String) = this shouldNot haveAttribute(attrName)
fun haveAttribute(attrName: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.hasAttr(attrName),
      "Element ${value.tagName()} should have attribute $attrName.",
      "Element ${value.tagName()} should not have attribute $attrName."
   )
}

infix fun Element.shouldHaveId(id: String) = this should haveId(id)
infix fun Element.shouldNotHaveId(id: String) = this shouldNot haveId(id)
fun haveId(id: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.id() == id,
      "Element ${value.tagName()} should have id $id.",
      "Element ${value.tagName()} should not have id $id."
   )
}
