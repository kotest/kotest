package io.kotest.assertions.jsoup

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Elements.shouldBePresent() = this should bePresent()
fun Elements.shouldNotBePresent() = this shouldNot bePresent()
fun bePresent() = object : Matcher<Elements> {
   override fun test(value: Elements) = MatcherResult(
      value.isNotEmpty(),
      "Element should be present",
      "Element should not be present"
   )
}

infix fun Elements.shouldBePresent(times: Int) = this should bePresent(times)
infix fun Elements.shouldNotBePresent(times: Int) = this shouldNot bePresent(times)
fun bePresent(times: Int) = object : Matcher<Elements> {
   override fun test(value: Elements) = MatcherResult(
      value.size == times,
      "Element should be present $times times",
      "Element should not be present $times times"
   )
}

infix fun Element.shouldHaveChildWithTag(tag: String) = this should haveChildWithTag(tag)
infix fun Element.shouldNotHaveChildWithTag(tag: String) = this shouldNot haveChildWithTag(tag)
fun haveChildWithTag(tag: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.getElementsByTag(tag).isNotEmpty(),
      "Document should have at least one child with tag $tag",
      "Document should not have child with tag $tag"
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

infix fun Element.shouldHaveAttribute(attrName: String) = this should haveAttribute(attrName)
infix fun Element.shouldNotHaveAttribute(attrName: String) = this shouldNot haveAttribute(attrName)
fun haveAttribute(attrName: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.hasAttr(attrName),
      "Element ${value.tagName()} should have attribute $attrName.",
      "Element ${value.tagName()} should not have attribute $attrName."
   )
}

fun Element.shouldHaveAttributeValue(attr: String, expectedValue: String) =
   this should haveAttrValue(attr, expectedValue)
fun Element.shouldNotHaveAttributeValue(attr: String, expectedValue: String) =
   this shouldNot haveAttrValue(attr, expectedValue)
fun haveAttrValue(attr: String, expectedValue: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.attr(attr) == expectedValue,
      "Element should have attribute $attr with value $expectedValue",
      "Element should not have attribute $attr with value $expectedValue"
   )
}
