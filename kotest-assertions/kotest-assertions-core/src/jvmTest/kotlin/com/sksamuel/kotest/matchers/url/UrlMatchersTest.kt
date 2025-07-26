package com.sksamuel.kotest.matchers.url

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.url.haveParameter
import io.kotest.matchers.url.haveParameterValue
import io.kotest.matchers.url.havePath
import io.kotest.matchers.url.havePort
import io.kotest.matchers.url.haveProtocol
import io.kotest.matchers.url.haveRef
import io.kotest.matchers.url.shouldBeOpaque
import io.kotest.matchers.url.shouldHaveAuthority
import io.kotest.matchers.url.shouldHaveHost
import io.kotest.matchers.url.shouldHaveParameter
import io.kotest.matchers.url.shouldHaveParameterValue
import io.kotest.matchers.url.shouldHavePath
import io.kotest.matchers.url.shouldHavePort
import io.kotest.matchers.url.shouldHaveProtocol
import io.kotest.matchers.url.shouldHaveQuery
import io.kotest.matchers.url.shouldHaveRef
import io.kotest.matchers.url.shouldNotBeOpaque
import io.kotest.matchers.url.shouldNotHaveAuthority
import io.kotest.matchers.url.shouldNotHaveHost
import io.kotest.matchers.url.shouldNotHaveParameter
import io.kotest.matchers.url.shouldNotHaveParameterValue
import io.kotest.matchers.url.shouldNotHavePath
import io.kotest.matchers.url.shouldNotHavePort
import io.kotest.matchers.url.shouldNotHaveProtocol
import io.kotest.matchers.url.shouldNotHaveQuery
import io.kotest.matchers.url.shouldNotHaveRef
import java.net.URI
import java.net.URL

class UrlMatchersTest : WordSpec() {

   init {

      "beOpaque" should {
         "test that a URL is opaque" {
            URL("https:hostname:8080").shouldBeOpaque()
            URL("https://path").shouldNotBeOpaque()
         }
      }

      "haveProtocol" should {
         "test that a URL has the specified protocol" {
            URL("https://hostname").shouldHaveProtocol("https")
            URL("https://hostname") should haveProtocol("https")
            URL("ftp://hostname").shouldNotHaveProtocol("https")
            URL("ftp://hostname") shouldNot haveProtocol("https")
         }
      }

      "havePort" should {
         "test that a URL has the specified port" {
            URL("https://hostname:90") should havePort(90)
            URL("https://hostname:90").shouldHavePort(90)
            URL("https://hostname") should havePort(-1)
            URL("ftp://hostname:14") shouldNot havePort(80)
            URL("ftp://hostname:14").shouldNotHavePort(80)
         }
      }

      "haveQuery" should {
         "test that a URL has a specified query" {
            URL("https://hostname:90?f=t") shouldHaveQuery ("f=t")
            URL("https://hostname:90?f=q") shouldNotHaveQuery ("f=t")
         }
      }

      "haveHost" should {
         "test that a URL has the specified host" {
            URI.create("https://hostname:90").toURL() shouldHaveHost ("hostname")
            URL("https://wewqe") shouldHaveHost ("wewqe")
            URL("ftp://hostname:14") shouldNotHaveHost ("qweqwe")
         }
      }

      "shouldHavePath" should {
         "test that a URL has the specified path" {
            URI.create("https://hostname:90/pathy").toURL() shouldHavePath ("/pathy")
            URI.create("https://hostname:90/pathy2").toURL() shouldNotHavePath ("/pathy")
         }
      }

      "shouldHaveAuthority" should {
         "test that a URL has the specified path" {
            URI.create("https://hostname:90/pathy").toURL() shouldHaveAuthority ("hostname:90")
            URI.create("https://hostname:91/pathy2").toURL() shouldNotHaveAuthority ("hostname:90")
            URI.create("https://hostname:91/pathy2").toURL() shouldNotHaveAuthority ("h!stname:91")
         }
      }

      "haveParameter" should {
         "test that a URL has the specified host" {
            URL("https://hostname:90?a=b&c=d") should haveParameter("a")
            URL("https://hostname:90?a=b&c=d").shouldHaveParameter("a")
            URL("https://hostname:90?a=b&c=d") should haveParameter("c")
            URL("https://hostname:90?a=b&c=d") shouldNot haveParameter("b")
            URL("https://hostname:90?a=b&c=d").shouldNotHaveParameter("b")
         }
         "support testing for the value" {
            URL("https://hostname:90?key=value").shouldHaveParameterValue("key", "value")
            URL("https://hostname:90?key=value") should haveParameterValue("key", "value")
            URL("https://hostname:90?key=value").shouldNotHaveParameterValue("key", "wibble")
         }
      }

      "havePath" should {
         "test that a URL has the specified path" {
            URL("https://hostname:90/index.html#qwerty") should havePath("/index.html")
         }
      }

      "haveRef" should {
         "test that a URL has the specified host" {
            URL("https://hostname:90#qwerty") should haveRef("qwerty")
            URL("https://hostname:90#qwerty").shouldHaveRef("qwerty")
            URL("https://hostname:90#") shouldHaveRef ("")
            URL("https://hostname:90#") shouldNotHaveRef ("qwerty")
            URL("https://hostname:90#qwerty") shouldNotHaveRef ("q")
         }
      }
   }
}
