package  io.kotlintest.matchers.uri

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.net.URI

fun haveScheme(scheme: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.scheme == scheme,
      "Uri $value should have scheme $scheme but was ${value.scheme}",
      "Uri $value should not have scheme $scheme"
  )
}

fun havePort(port: Int) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.port == port,
      "Uri $value should have port $port but was ${value.port}",
      "Uri $value should not have port $port"
  )
}

fun haveHost(host: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.host == host,
      "Uri $value should have host $host but was ${value.host}",
      "Uri $value should not have host $host"
  )
}

fun havePath(path: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.path == path,
      "Uri $value should have path $path but was ${value.path}",
      "Uri $value should not have path $path"
  )
}

fun haveParameter(key: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.query.split("&").any { it.split("=").first() == key },
      "Uri $value should have query parameter $key",
      "Uri $value should not have query parameter $key"
  )
}

fun haveFragment(fragment: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.fragment == fragment,
      "Uri $value should have fragment $fragment",
      "Uri $value should not fragment $fragment"
  )
}