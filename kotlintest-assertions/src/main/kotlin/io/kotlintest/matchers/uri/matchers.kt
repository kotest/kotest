package io.kotlintest.matchers.uri

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.net.URI

fun URI.shouldBeOpaque() = this should io.kotlintest.matchers.uri.beOpaque()
fun URI.shouldNotBeOpaque() = this shouldNot io.kotlintest.matchers.uri.beOpaque()
fun beOpaque() = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.isOpaque,
      "Uri $value should be opaque",
      "Uri $value should not be opaque"
  )
}

infix fun URI.shouldHaveScheme(scheme: String) = this should io.kotlintest.matchers.uri.haveScheme(scheme)
infix fun URI.shouldNotHaveScheme(scheme: String) = this shouldNot io.kotlintest.matchers.uri.haveScheme(scheme)
fun haveScheme(scheme: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.scheme == scheme,
      "Uri $value should have scheme $scheme but was ${value.scheme}",
      "Uri $value should not have scheme $scheme"
  )
}

infix fun URI.shouldHavePort(port: Int) = this should io.kotlintest.matchers.uri.havePort(port)
infix fun URI.shouldNotHavePort(port: Int) = this shouldNot io.kotlintest.matchers.uri.havePort(port)
fun havePort(port: Int) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.port == port,
      "Uri $value should have port $port but was ${value.port}",
      "Uri $value should not have port $port"
  )
}

infix fun URI.shouldHaveHost(host: String) = this should io.kotlintest.matchers.uri.haveHost(host)
infix fun URI.shouldNotHaveHost(host: String) = this shouldNot io.kotlintest.matchers.uri.haveHost(host)
fun haveHost(host: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.host == host,
      "Uri $value should have host $host but was ${value.host}",
      "Uri $value should not have host $host"
  )
}

infix fun URI.shouldHaveQuery(q: String) = this should io.kotlintest.matchers.uri.haveQuery(q)
infix fun URI.shouldNotHaveQuery(q: String) = this shouldNot io.kotlintest.matchers.uri.haveQuery(q)
fun haveQuery(q: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.query == q,
      "Uri $value should have query $q but was ${value.query}",
      "Uri $value should not have query $q"
  )
}

infix fun URI.shouldHaveAuthority(authority: String) = this should io.kotlintest.matchers.uri.haveAuthority(authority)
infix fun URI.shouldNotHaveAuthority(authority: String) = this shouldNot io.kotlintest.matchers.uri.haveAuthority(authority)
fun haveAuthority(authority: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.authority == authority,
      "Uri $value should have authority $authority but was ${value.authority}",
      "Uri $value should not have authority $authority"
  )
}

infix fun URI.shouldHavePath(path: String) = this should io.kotlintest.matchers.uri.havePath(path)
infix fun URI.shouldNotHavePath(path: String) = this shouldNot io.kotlintest.matchers.uri.havePath(path)
fun havePath(path: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.path == path,
      "Uri $value should have path $path but was ${value.path}",
      "Uri $value should not have path $path"
  )
}

infix fun URI.shouldHaveParameter(key: String) = this should io.kotlintest.matchers.uri.haveParameter(key)
infix fun URI.shouldNotHaveParameter(key: String) = this shouldNot io.kotlintest.matchers.uri.haveParameter(key)
fun haveParameter(key: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.query.split("&").any { it.split("=").first() == key },
      "Uri $value should have query parameter $key",
      "Uri $value should not have query parameter $key"
  )
}

infix fun URI.shouldHaveFragment(fragment: String) = this should io.kotlintest.matchers.uri.haveFragment(fragment)
infix fun URI.shouldNotHaveFragment(fragment: String) = this shouldNot io.kotlintest.matchers.uri.haveFragment(fragment)
fun haveFragment(fragment: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.fragment == fragment,
      "Uri $value should have fragment $fragment",
      "Uri $value should not fragment $fragment"
  )
}
