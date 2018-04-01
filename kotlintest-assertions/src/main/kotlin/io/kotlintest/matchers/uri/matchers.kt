package  io.kotlintest.matchers.uri

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.net.URI

fun haveScheme(scheme: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.scheme == scheme,
      "Uri $value should have scheme $scheme",
      "Uri $value should not have scheme $scheme"
  )
}

fun havePort(port: Int) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.port == port,
      "Uri $value should have port $port",
      "Uri $value should not have port $port"
  )
}

fun haveHost(host: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.host == host,
      "Uri $value should have host $host",
      "Uri $value should not have host $host"
  )
}

fun haveParameter(key: String) = object : Matcher<URI> {
  override fun test(value: URI) = Result(
      value.query.split("&").any { it.split("=").first() == key },
      "Uri $value should have query parameter $key",
      "Uri $value should not have query parameter $key"
  )
}