package  io.kotlintest.matchers.uri

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.net.URI

fun URI.shouldHaveScheme(scheme: String) = this should io.kotlintest.matchers.uri.haveScheme(scheme)
fun URI.shouldNotHaveScheme(scheme: String) = this shouldNot io.kotlintest.matchers.uri.haveScheme(scheme)
fun haveScheme(scheme: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.scheme == scheme,
      "Uri $value should have scheme $scheme but was ${value.scheme}",
      "Uri $value should not have scheme $scheme"
  )
}

fun URI.shouldHavePort(port: Int) = this should io.kotlintest.matchers.uri.havePort(port)
fun URI.shouldNotHavePort(port: Int) = this shouldNot io.kotlintest.matchers.uri.havePort(port)
fun havePort(port: Int) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.port == port,
      "Uri $value should have port $port but was ${value.port}",
      "Uri $value should not have port $port"
  )
}

fun URI.shouldHaveHost(host: String) = this should io.kotlintest.matchers.uri.haveHost(host)
fun URI.shouldNotHaveHost(host: String) = this shouldNot io.kotlintest.matchers.uri.haveHost(host)
fun haveHost(host: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.host == host,
      "Uri $value should have host $host but was ${value.host}",
      "Uri $value should not have host $host"
  )
}

fun URI.shouldHavePath(path: String) = this should io.kotlintest.matchers.uri.haveScheme(path)
fun URI.shouldNotHavePath(path: String) = this shouldNot io.kotlintest.matchers.uri.haveScheme(path)
fun havePath(path: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.path == path,
      "Uri $value should have path $path but was ${value.path}",
      "Uri $value should not have path $path"
  )
}

fun URI.shouldHaveParameter(key: String) = this should io.kotlintest.matchers.uri.haveParameter(key)
fun URI.shouldNotHaveParameter(key: String) = this shouldNot io.kotlintest.matchers.uri.haveScheme(key)
fun haveParameter(key: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.query.split("&").any { it.split("=").first() == key },
      "Uri $value should have query parameter $key",
      "Uri $value should not have query parameter $key"
  )
}

fun URI.shouldHaveFragment(fragment: String) = this should io.kotlintest.matchers.uri.haveFragment(fragment)
fun URI.shouldNotHaveFragment(fragment: String) = this shouldNot io.kotlintest.matchers.uri.haveFragment(fragment)
fun haveFragment(fragment: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.fragment == fragment,
      "Uri $value should have fragment $fragment",
      "Uri $value should not fragment $fragment"
  )
}