package io.kotest.assertions.jsoup

import io.kotest.specs.FreeSpec
import org.jsoup.Jsoup

class ElementMatchersTest : FreeSpec() {
   init {
      val root = Jsoup.parse(ResourceLoader.getFileAsString("example.html"))
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
         "haveClass" {
            val data = root.getElementsByTag("body").first()
            data shouldHaveClass "someClass"
         }
         "haveAttribute" {
            val data = root.getElementsByTag("html").first()
            data shouldHaveAttribute "lang"
         }
         "haveId" {
            val data = root.getElementsByTag("header").first()
            data shouldHaveId "abc"
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
         "haveClass" {
            val data = root.getElementsByTag("body").first()
            data shouldNotHaveClass "someOtherClass"
         }
         "haveAttribute" {
            val data = root.getElementsByTag("h1").first()
            data shouldNotHaveAttribute "lang"
         }
         "haveId" {
            val data = root.getElementsByTag("header").first()
            data shouldNotHaveId "a1b2c3"
         }
      }
   }
}
