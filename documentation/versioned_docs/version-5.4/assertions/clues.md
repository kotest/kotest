---
title: Clues
slug: clues.html
---

:::note
Clues only work if you are using the Kotest assertions library or Kotest test framework
:::


Sometimes a failed assertion contains enough information in the error message to know what went wrong.

For example:

```kotlin
username shouldBe "sksamuel"
```

Might give an error like:

```
expected: "sksamuel" but was: "sam@myemailaddress.com"
```

And you would be able to see that you were populating the username field with an email address.

But let's say you had a test like this:

```kotlin
user.name shouldNotBe null
```

If this failed, you would simply get:

```
<null> should not equal <null>
```

Which isn't particularly helpful. This is where `withClue` comes into play.

The `withClue` and `asClue` helpers can add extra context to assertions so failures are self-explanatory:

For example, we can use `withClue` with a string message

```kotlin
withClue("Name should be present") {
  user.name shouldNotBe null
}
```

Would give an error like this:

```
Name should be present
<null> should not equal <null>
```

We can also use the `asClue` extension function to turn any object into the clue message.

For example:

```kotlin
data class HttpResponse(val status: Int, val body: String)

val response = HttpResponse(404, "the content")

response.asClue {
    it.status shouldBe 200
    it.body shouldBe "the content"
}
```

Would output:

```
HttpResponse(status=404, body=the content)
Expected :200
Actual   :404
```
