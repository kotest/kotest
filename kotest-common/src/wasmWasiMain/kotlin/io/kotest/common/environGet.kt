/*
 * See discussion here: https://slack-chats.kotlinlang.org/t/16189827/just-for-fun-i-ve-added-environment-variables-support-in-the
 *
 * This file can be found here: https://github.com/kowasm/kowasm/blob/7f89e1b21883f42d0b0aafe34db63b72c048bd9e/wasi/src/wasmJsMain/kotlin/org/kowasm/wasi/internal/Env.kt
 * License can be found here: https://github.com/kowasm/kowasm/blob/7f89e1b21883f42d0b0aafe34db63b72c048bd9e/LICENSE
 *
 * Copyright 2023 the original author or authors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package io.kotest.common

import kotlin.wasm.unsafe.Pointer
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

/**
 * Read environment variable data.
 */
@OptIn(UnsafeWasmMemoryApi::class)
internal fun environGet(): Map<String, String> {
   val (numArgs, bufSize) = environSizesGet()
   val tmpByteArray = ByteArray(bufSize)
   withScopedMemoryAllocator { allocator ->
      val environ = allocator.allocate(numArgs * 4)
      val environBuffer = allocator.allocate(bufSize)
      val ret = wasmEnvironGet(environ.address.toInt(), environBuffer.address.toInt())
      if (ret != 0) {
         throw WasiException(ret)
      }
      val result = mutableMapOf<String, String>()
      repeat(numArgs) { idx ->
         val environPtr = environ + idx * 4
         val ptr = Pointer(environPtr.loadInt().toUInt())
         val endIdx = readZeroTerminatedByteArray(ptr, tmpByteArray)
         val str = tmpByteArray.decodeToString(endIndex = endIdx)
         val (key, value) = str.split("=", limit = 2)
         result[key] = value
      }
      return result
   }
}

@OptIn(UnsafeWasmMemoryApi::class)
private fun environSizesGet(): Pair<Int, Int> {
   withScopedMemoryAllocator { allocator ->
      val rp0 = allocator.allocate(4)
      val rp1 = allocator.allocate(4)
      val ret = wasmEnvironSizesGet(rp0.address.toInt(), rp1.address.toInt())
      return if (ret == 0) {
         Pair(
            (Pointer(rp0.address.toInt().toUInt())).loadInt(),
            (Pointer(rp1.address.toInt().toUInt())).loadInt()
         )
      } else {
         throw WasiException(ret)
      }
   }
}

@OptIn(ExperimentalWasmInterop::class)
@WasmImport("wasi_snapshot_preview1", "environ_get")
private external fun wasmEnvironGet(
   arg0: Int,
   arg1: Int,
): Int

@OptIn(ExperimentalWasmInterop::class)
@WasmImport("wasi_snapshot_preview1", "environ_sizes_get")
private external fun wasmEnvironSizesGet(
   arg0: Int,
   arg1: Int,
): Int

private class WasiException(errorCode: Int) : RuntimeException(message = "WASI call failed with $errorCode")

@OptIn(UnsafeWasmMemoryApi::class)
private fun readZeroTerminatedByteArray(ptr: Pointer, byteArray: ByteArray): Int {
   for (i in byteArray.indices) {
      val b = (ptr + i).loadByte()
      if (b.toInt() == 0) {
         return i
      }
      byteArray[i] = b
   }
   error("Zero-terminated array is out of bounds")
}
