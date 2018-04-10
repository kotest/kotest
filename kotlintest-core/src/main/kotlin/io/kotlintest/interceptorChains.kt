@file:Suppress("DEPRECATION")

import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext

fun createSpecInterceptorChain(
    context: SpecInterceptContext,
    extensions: Iterable<SpecExtension>,
    initial: () -> Unit): () -> Unit {
  return extensions.fold(initial, { fn, extension ->
    {
      extension.intercept(context, fn)
    }
  })
}