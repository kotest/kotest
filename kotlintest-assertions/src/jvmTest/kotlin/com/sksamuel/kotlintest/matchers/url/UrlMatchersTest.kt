package com.sksamuel.kotlintest.matchers.url

import io.kotlintest.matchers.url.haveHost
import io.kotlintest.matchers.url.haveParameter
import io.kotlintest.matchers.url.haveParameterValue
import io.kotlintest.matchers.url.havePath
import io.kotlintest.matchers.url.havePort
import io.kotlintest.matchers.url.haveProtocol
import io.kotlintest.matchers.url.haveRef
import io.kotlintest.matchers.url.shouldBeOpaque
import io.kotlintest.matchers.url.shouldHaveParameter
import io.kotlintest.matchers.url.shouldHaveParameterValue
import io.kotlintest.matchers.url.shouldHavePort
import io.kotlintest.matchers.url.shouldHaveProtocol
import io.kotlintest.matchers.url.shouldHaveRef
import io.kotlintest.matchers.url.shouldNotBeOpaque
import io.kotlintest.matchers.url.shouldNotHaveParameter
import io.kotlintest.matchers.url.shouldNotHaveParameterValue
import io.kotlintest.matchers.url.shouldNotHavePort
import io.kotlintest.matchers.url.shouldNotHaveProtocol
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.WordSpec
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

    "haveHost" should {
      "test that a URL has the specified host" {
        URL("https://hostname:90") should haveHost("hostname")
        URL("https://wewqe") should haveHost("wewqe")
        URL("ftp://hostname:14") shouldNot haveHost("qweqwe")
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
        URL("https://hostname:90#") should haveRef("")
      }
    }
  }
}