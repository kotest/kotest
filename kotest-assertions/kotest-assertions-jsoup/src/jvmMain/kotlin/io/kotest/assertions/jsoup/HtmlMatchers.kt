package io.kotest.assertions.jsoup

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.jsoup.nodes.Element

infix fun Element.shouldHaveId(id: String) = this should haveId(id)
infix fun Element.shouldNotHaveId(id: String) = this shouldNot haveId(id)
fun haveId(id: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.id() == id,
      "Element ${value.tagName()} should have id $id.",
      "Element ${value.tagName()} should not have id $id."
   )
}

infix fun Element.shouldHaveClass(className: String) = this should haveClass(className)
infix fun Element.shouldNotHaveClass(className: String) = this shouldNot haveClass(className)
fun haveClass(className: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.hasClass(className),
      "Element ${value.tagName()} should have class $className.",
      "Element ${value.tagName()} should not have text $className."
   )
}

infix fun Element.shouldHaveSrc(src: String) = this should haveAttrValue("src", src)
infix fun Element.shouldNotHaveSrc(src: String) = this shouldNot haveAttrValue("src", src)

infix fun Element.shouldHaveHref(src: String) = this should haveAttrValue("href", src)
infix fun Element.shouldNotHaveHref(src: String) = this shouldNot haveAttrValue("href", src)

infix fun Element.shouldHaveElementWithId(id: String) = this should haveElementWithId(id)
infix fun Element.shouldNotHaveElementWithId(id: String) = this shouldNot haveElementWithId(id)
fun haveElementWithId(id: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.getElementById(id) != null,
      "Element should have a child with id $id",
      "Element should not have a child with id $id"
   )
}

infix fun Element.shouldHaveChildWithClass(clazz: String) = this should haveChildWithClass(clazz)
infix fun Element.shouldNotHaveChildWithClass(clazz: String) = this shouldNot haveChildWithClass(clazz)
fun haveChildWithClass(clazz: String) = object : Matcher<Element> {
   override fun test(value: Element) = MatcherResult(
      value.getElementsByClass(clazz).isNotEmpty(),
      "Element should have at least one child with class $clazz",
      "Element should not have child with class $clazz"
   )
}
