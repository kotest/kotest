---
title: Clues
slug: clues.html
---

:::note
Clues only work if you are using the Kotest assertions library
:::

A rule of thumb is that a failing test should look like a good bug report.
In other words, it should tell you what went wrong, and ideally why it went wrong.

Sometimes a failed assertion contains enough information in the error message to know what went wrong.

For example:

```kotlin
username shouldBe "sksamuel"
```

Might give an error like:

```
expected: "sksamuel" but was: "sam@myemailaddress.com"
```

In this case, it looks like the system populates the username field with an email address.

But let's say you had a test like this:

```kotlin
user.name shouldNotBe null
```

If this failed, you would simply get:

```
<null> should not equal <null>
```

Which isn't particularly helpful. This is where `withClue` comes into play.

The `withClue` and `asClue` helpers can add extra context to assertions so failures are self explanatory:

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

The error message became much better, however, it is still not as good as it could be.
For instance, it might be helpful to know the user's id to check the database.

We can use `withClue` to add the user's id to the error message:

```kotlin
withClue({ "Name should be present (user_id=${user.id})" }) {
  user.name shouldNotBe null
}
```

We can also use the `asClue` extension function to turn any object into the clue message.

```kotlin
{ "Name should be present (user_id=${user.id})" }.asClue {
user.name shouldNotBe null
}
```

The message will be computed only in case the test fails, so it is safe to use it with expensive operations.

:::tip
Test failures include a failed assertion, test name, clues, and stacktrace.
Consider using them in such a way, so they answer both what has failed, and why it failed.
It will make the tests easier to maintain, especially when it comes to reverse-engineering the intention of the test author.
:::

:::tip
Every time you see a code comment above an assertion, consider using `asClue`, or `withClue` instead.
The comments are not visible in the test failures, especially on CI, while clues will be visible.
:::

You can use domain objects as clues as well:

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

:::note
Kotest considers all `() -> Any?` clues as lazy clues, and would compute them and use `.toString()` on the resulting value
instead of calling `.toString()` on the function itself.
In most cases, it should do exactly what you need, however, if clue object implements `() -> Any?`, and you want
using `clue.toString()`, then consider wrapping the clue manually as `{ clue.toString() }.asClue { ... }`.
:::

## Nested clues

Clues can be nested, and they all will be visible in the failed assertion messages:

```kotlin
{ "Verifying user_id=${user.name}" }.asClue {
  "email_confirmed should be false since we've just created the user".asClue {
    user.emailConfirmed shouldBe false
  }
  "login".asClue {
    user.login shouldBe "sksamuel"
  }
}
```

The failure might look like

```
Verifying user_id=42
email_confirmed should be false since we've just created the user
<true> should equal <false>
```
