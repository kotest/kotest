---
title: Why Kotest
sidebar_label: Why Kotest
slug: why-kotest.html
---

## vs Junit

* Powerful coroutine integration: Every test is a coroutine, therefore, you can invoke suspension methods without requiring `runBlocking` or other boilerplate
  * One line configuration to enable coroutine debugging for a test or for all tests
  * Callbacks allow modifying the coroutine context for child coroutines
  * Customize the coroutine dispatcher used
  * Launch multiple tests in parallel that do not block each other if they suspend

* Multiplatform support
  * Native and Javascript support
  * Same test structure across all targets

* Flexible test layout styles
  * Simple DSL avoids needing to wrap test names in backticks
  * Layout tests like Javascript frameworks - `describe`/`it`
  * Or like Scalatest with `"my test" should "do foo"`
  * Or in a BDD style with `given` / `when` / `then`.
