Ktor Matchers
=============

## ⛔️ [This page has moved](https://kotest.io/docs/assertions/ktor-matchers.html) ⛔ ️

Matchers for [Ktor](https://ktor.io/) are provided by the `kotest-assertions-ktor` module.


### Test Application Response

The following matchers are used when testing via the ktor server testkit.

| Matcher | Description    |
| ---------- | --- |
| `shouldHaveStatus(HttpStatusCode)`        | Tests that the response had the given http status code    |
| `shouldHaveContent(content)`              | Tests that the response has the given body     |
| `shouldHaveContentType(ContentType)`       | Tests that the response has the given Content Type     |
| `shouldHaveHeader(name, value)`           | Tests that the response included the given name=value header     |
| `shouldHaveCookie(name, value)`           | Tests that the response included the given cookie     |

### HttpResponse

The following matchers can be used against responses from the ktor http client.

| Matcher | Description    |
| ---------- | --- |
| `shouldHaveStatus(HttpStatusCode)`        | Tests that the response had the given http status code    |
| `shouldHaveContentType(ContentType)`       | Tests that the response has the given Content Type     |
| `shouldHaveHeader(name, value)`           | Tests that the response included the given name=value header     |
| `shouldHaveVersion(HttpProtocolVersion)`  | Tests that the response used the given protocol version     |
