package io.kotest.core.annotation.requirestag

import io.kotest.core.annotation.RequiresTag

actual val RequiresTag.wrapper: Array<out String>
   get() = values
