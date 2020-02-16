package com.sksamuel.kotest.matchers.uri

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.uri.haveFragment
import io.kotest.matchers.uri.haveHost
import io.kotest.matchers.uri.haveParameter
import io.kotest.matchers.uri.havePath
import io.kotest.matchers.uri.havePort
import io.kotest.matchers.uri.haveScheme
import io.kotest.matchers.uri.shouldBeOpaque
import io.kotest.matchers.uri.shouldHaveScheme
import io.kotest.matchers.uri.shouldNotBeOpaque
import io.kotest.matchers.uri.shouldNotHaveScheme
import java.net.URI

class UriMatchersTest : WordSpec() {

  init {

    "beOpaque" should {
      "test that a URI is opaque" {
        URI.create("https:hostname:8080").shouldBeOpaque()
        URI.create("hostname").shouldNotBeOpaque()
      }
    }

    "haveScheme" should {
      "test that a URI has the specified scheme" {
        URI.create("https://hostname").shouldHaveScheme("https")
        URI.create("https://hostname") should haveScheme("https")
        URI.create("ftp://hostname").shouldNotHaveScheme("https")
        URI.create("ftp://hostname") shouldNot haveScheme("https")
      }
    }

    "havePort" should {
      "test that a URI has the specified port" {
        URI.create("https://hostname:90") should havePort(90)
        URI.create("https://hostname") should havePort(-1)
        URI.create("ftp://hostname:14") shouldNot havePort(80)
      }
    }

    "haveHost" should {
      "test that a URI has the specified host" {
        URI.create("https://hostname:90") should haveHost("hostname")
        URI.create("https://wewqe") should haveHost("wewqe")
        URI.create("ftp://hostname:14") shouldNot haveHost("qweqwe")
      }
    }

    "haveParameter" should {
      "test that a URI has the specified host" {
        URI.create("https://hostname:90?a=b&c=d") should haveParameter("a")
        URI.create("https://hostname:90?a=b&c=d") should haveParameter("c")
        URI.create("https://hostname:90?a=b&c=d") shouldNot haveParameter("b")
      }
    }

    "havePath" should {
      "test that a URI has the specified path" {
        URI.create("https://hostname:90/index.html#qwerty") should havePath("/index.html")
      }
    }

    "haveFragment" should {
      "test that a URI has the specified host" {
        URI.create("https://hostname:90#qwerty") should haveFragment("qwerty")
        URI.create("https://hostname:90#") should haveFragment("")
      }
    }
  }
}
