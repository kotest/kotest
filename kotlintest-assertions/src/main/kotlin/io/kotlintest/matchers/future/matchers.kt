package io.kotlintest.matchers.future

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.util.concurrent.CompletableFuture

fun <T> completed() = object : Matcher<CompletableFuture<T>> {
  override fun test(value: CompletableFuture<T>): Result =
      Result(
          value.isDone,
          "Future should be completed",
          "Future should not be completed"
      )
}

fun <T> cancelled() = object : Matcher<CompletableFuture<T>> {
  override fun test(value: CompletableFuture<T>): Result =
      Result(
          value.isCancelled,
          "Future should be completed",
          "Future should not be completed"
      )
}