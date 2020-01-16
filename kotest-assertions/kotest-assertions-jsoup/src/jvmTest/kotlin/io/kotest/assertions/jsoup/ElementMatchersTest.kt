package io.kotest.assertions.jsoup

import io.kotest.core.spec.style.FreeSpec
import org.jsoup.Jsoup

class ElementMatchersTest : FreeSpec() {
   init {
      val html = javaClass.classLoader.getResourceAsStream("example.html").bufferedReader().use { it.readText() }
      val root = Jsoup.parse(html)
      "should" - {
         "bePresent" {
            val data = root.getElementsByTag("p")
            data.shouldBePresent()
         }
         "bePresent N Times" {
            val data = root.getElementsByTag("p")
            data shouldBePresent 2
         }
         "haveText" {
            val data = root.getElementsByTag("h1").first()
            data shouldHaveText "i'm the headline"
         }
         "haveAttribute" {
            val data = root.getElementsByTag("html").first()
            data shouldHaveAttribute "lang"
         }
         "haveChildWithTag" {
            root shouldHaveChildWithTag "body"
         }
         "haveAttrValue" {
            val data = root.getElementsByTag("html").first()
            data.shouldHaveAttributeValue("lang", "en")
         }
      }
      "shouldNot" - {
         "bePresent" {
            val data = root.getElementsByTag("script")
            data.shouldNotBePresent()
         }
         "bePresent N Times" {
            val data = root.getElementsByTag("body")
            data shouldNotBePresent 2
         }
         "haveText" {
            val data = root.getElementsByTag("h1").first()
            data shouldNotHaveText "i'm not the headline"
         }
         "haveAttribute" {
            val data = root.getElementsByTag("h1").first()
            data shouldNotHaveAttribute "lang"
         }
         "haveChildWithTag" {
            root shouldNotHaveChildWithTag "foot"
         }
         "haveAttrValue" {
            val data = root.getElementsByTag("html").first()
            data.shouldNotHaveAttributeValue("lang", "es")
         }
      }
   }
}
