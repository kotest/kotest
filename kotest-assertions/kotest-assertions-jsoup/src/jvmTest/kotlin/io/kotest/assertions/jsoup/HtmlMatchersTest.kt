package io.kotest.assertions.jsoup

import io.kotest.core.spec.style.FreeSpec
import org.jsoup.Jsoup

class HtmlMatchersTest : FreeSpec() {
   init {
      val html = javaClass.classLoader.getResourceAsStream("example.html").bufferedReader().use { it.readText() }
      val root = Jsoup.parse(html)
      "should" - {
         "haveId" {
            val data = root.getElementsByTag("header").first()
            data shouldHaveId "abc"
         }
         "haveClass" {
            val data = root.getElementsByTag("body").first()
            data shouldHaveClass "someClass"
         }
         "haveSrc" {
            val data = root.getElementsByTag("img").first()
            data shouldHaveSrc "http://image.url/test"
         }
         "haveHref" {
            val data = root.getElementsByTag("a").first()
            data shouldHaveHref "some.link"
         }
         "haveElementWithId" {
            root shouldHaveElementWithId "abc"
         }
         "haveChildWithClass" {
            root shouldHaveChildWithClass "someClass"
         }
      }
      "shouldNot" - {
         "haveId" {
            val data = root.getElementsByTag("header").first()
            data shouldNotHaveId "a1b2c3"
         }
         "haveClass" {
            val data = root.getElementsByTag("body").first()
            data shouldNotHaveClass "someOtherClass"
         }
         "haveSrc" {
            val data = root.getElementsByTag("img").first()
            data shouldNotHaveSrc "http://some.other-image.url/test"
         }
         "haveHref" {
            val data = root.getElementsByTag("a").first()
            data shouldNotHaveHref "http://some.other-image.url/test"
         }
         "haveElementWithId" {
            root shouldNotHaveElementWithId "def"
         }
         "haveChildWithClass" {
            root shouldNotHaveChildWithClass "someOtherClass"
         }
      }
   }
}
