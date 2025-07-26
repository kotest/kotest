package io.kotest.matchers.url

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.net.URL

fun URL.shouldBeOpaque() = this should beOpaque()
fun URL.shouldNotBeOpaque() = this shouldNot beOpaque()
fun beOpaque() = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.toURI().isOpaque,
      { "URL $value should be opaque" },
      { "URL $value should not be opaque" }
   )
}

infix fun URL.shouldHaveProtocol(protocol: String) = this should haveProtocol(protocol)
infix fun URL.shouldNotHaveProtocol(protocol: String) = this shouldNot haveProtocol(protocol)
fun haveProtocol(protocol: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.protocol == protocol,
      { "URL $value should have protocol $protocol but was ${value.protocol}" },
      { "URL $value should not have protocol $protocol" }
   )
}

infix fun URL.shouldHavePort(port: Int) = this should havePort(port)
infix fun URL.shouldNotHavePort(port: Int) = this shouldNot havePort(port)
fun havePort(port: Int) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.port == port,
      { "URL $value should have port $port but was ${value.port}" },
      { "URL $value should not have port $port" }
   )
}

infix fun URL.shouldHaveHost(host: String) = this should haveHost(host)
infix fun URL.shouldNotHaveHost(host: String) = this shouldNot haveHost(host)
fun haveHost(host: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.host == host,
      { "URL $value should have host $host but was ${value.host}" },
      { "URL $value should not have host $host" }
   )
}

infix fun URL.shouldHaveQuery(q: String) = this should haveQuery(q)
infix fun URL.shouldNotHaveQuery(q: String) = this shouldNot haveQuery(q)
fun haveQuery(q: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.query == q,
      { "URL $value should have query $q but was ${value.query}" },
      { "URL $value should not have query $q" }
   )
}

infix fun URL.shouldHaveAuthority(authority: String) = this should haveAuthority(authority)
infix fun URL.shouldNotHaveAuthority(authority: String) = this shouldNot haveAuthority(authority)
fun haveAuthority(authority: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.authority == authority,
      { "URL $value should have authority $authority but was ${value.authority}" },
      { "URL $value should not have authority $authority" }
   )
}

infix fun URL.shouldHavePath(path: String) = this should havePath(path)
infix fun URL.shouldNotHavePath(path: String) = this shouldNot havePath(path)
fun havePath(path: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.path == path,
      { "URL $value should have path $path but was ${value.path}" },
      { "URL $value should not have path $path" }
   )
}

infix fun URL.shouldHaveParameter(key: String) = this should haveParameter(key)
infix fun URL.shouldNotHaveParameter(key: String) = this shouldNot haveParameter(key)
fun haveParameter(key: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.query.split("&").any { it.split("=").first() == key },
      { "URL $value should have query parameter $key" },
      { "URL $value should not have query parameter $key" }
   )
}

fun URL.shouldHaveParameterValue(key: String, value: String) = this should haveParameterValue(key, value)
fun URL.shouldNotHaveParameterValue(key: String, value: String) = this shouldNot haveParameterValue(key, value)

fun haveParameterValue(key: String, v: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.query.split("&").find { it.split("=").first() == key } == "$key=$v",
      { "URL $value should have query parameter $key=$v" },
      { "URL $value should not have query parameter $key=$v" }
   )
}

infix fun URL.shouldHaveRef(ref: String) = this should haveRef(ref)
infix fun URL.shouldNotHaveRef(ref: String) = this shouldNot haveRef(ref)
fun haveRef(ref: String) = object : Matcher<URL> {
   override fun test(value: URL) = MatcherResult(
      value.ref == ref,
      { "URL $value should have ref $ref" },
      { "URL $value should not ref $ref" }
   )
}
