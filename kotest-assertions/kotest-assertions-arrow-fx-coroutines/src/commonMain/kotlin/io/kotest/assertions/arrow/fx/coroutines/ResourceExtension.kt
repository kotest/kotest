package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.allocate
import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.extensions.LazyMountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.concurrent.atomics.ExperimentalAtomicApi

enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

/** Turns a [Resource] into a [LazyMountableExtension] as a [ResourceExtension] */
fun <A> Resource<A>.extension(lifecycleMode: LifecycleMode = LifecycleMode.Spec): ResourceExtension<A> =
   ResourceExtension(this, lifecycleMode)

/**
 * Ensures that all finalizers of [resource] are invoked for the specified [LifecycleMode].
 * Installing this [LazyMountableExtension] returns a [LazyMaterialized]
 * from which you can extract initialized [A] using [LazyMaterialized.get].
 *
 * ```kotlin
 * class Connection(val name: String) {
 *   fun open() = println("Open $name")
 *   fun compute() = "Result of $name"
 *   fun release(ex: ExitCase) = println("Closing $name with $ex")
 * }
 *
 * fun connection(name: String): Resource<Connection> =
 *   Resource({ Connection(name).also { it.open() } }, { conn, ex -> conn.release(ex) })
 *
 * class ResourceSpec : StringSpec({
 *   val conn: TestResource<Connection> =
 *     install(ResourceExtension(connection("DB")))
 *
 *   "get" {
 *     conn.get().compute() shouldBe "Result of DB"
 *   }
 *
 *   afterSpec {
 *     shouldThrow<IllegalStateException> {
 *       conn.get()
 *     }.message shouldBe "Resource already closed and cannot be re-opened."
 *   }
 * })
 * ```
 *
 * `conn` cannot be used during or after `afterSpec` since at that point the resource is already released,
 * and it will result in [IllegalStateException]
 *
 * @param lifecycleMode allows installing the [Resource] for other scopes besides the default [LifecycleMode.Spec].
 */
class ResourceExtension<A>(
   val resource: Resource<A>,
   val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : LazyMountableExtension<A, A>, TestListener, AfterSpecListener {

   private var underlying: ResourceLazyMaterialized<A>? = null

   override fun mount(configure: A.() -> Unit): LazyMaterialized<A> =
      ResourceLazyMaterialized(resource, configure).also {
         underlying = it
      }

   override suspend fun beforeSpec(spec: Spec) {
      super.beforeSpec(spec)
      if (lifecycleMode == LifecycleMode.Spec) {
         underlying?.init()
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (lifecycleMode == LifecycleMode.Spec) {
         underlying?.release()
      }
      underlying?.close()
   }

   override suspend fun beforeAny(testCase: TestCase) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         underlying?.init()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         underlying?.release()
      }
   }
}

@OptIn(ExperimentalAtomicApi::class)
internal class ResourceLazyMaterialized<A>(
   private val resource: Resource<A>,
   private val configure: A.() -> Unit = {},
) : LazyMaterialized<A> {

   sealed interface State<out A> {
      data object Empty : State<Nothing>
      data object Closed : State<Nothing>
      data class Loading<A>(
         val acquiring: CompletableDeferred<A> = CompletableDeferred(),
         val finalizers: CompletableDeferred<suspend (ExitCase) -> Unit> = CompletableDeferred(),
      ) : State<A>

      data class Done<A>(val value: A, val finalizers: suspend (ExitCase) -> Unit) : State<A>
   }

   private val state = kotlin.concurrent.atomics.AtomicReference<State<A>>(State.Empty)

   override suspend fun get(): A = init()

   @OptIn(DelicateCoroutinesApi::class)
   tailrec suspend fun init(): A = when (val current = state.load()) {
      is State.Done -> current.value
      is State.Loading -> current.acquiring.await()
      is State.Closed -> throw IllegalStateException("Resource already closed and cannot be re-opened.")
      State.Empty -> {
         val loading = State.Loading<A>()
         if (state.compareAndSet(State.Empty, loading)) {
            val (res, fin) = resource.allocate()
               .let { (a, finalizer) ->
                  @Suppress("NAME_SHADOWING")
                  val finalizer: suspend (ExitCase) -> Unit = { finalizer(it) }
                  Pair(a, finalizer)
               }
            state.store(State.Done(res, fin))
            loading.acquiring.complete(res.also(configure))
            loading.finalizers.complete(fin)
            res
         } else init()
      }
   }

   tailrec suspend fun release(): Unit = when (val current = state.load()) {
      State.Empty -> Unit
      is State.Done -> if (state.compareAndSet(current, State.Empty)) current.finalizers(ExitCase.Completed)
      else release()

      is State.Loading -> if (state.compareAndSet(current, State.Empty)) current.finalizers.await()
         .invoke(ExitCase.Completed)
      else release()

      State.Closed -> Unit
   }

   fun close() {
      state.store(State.Closed)
   }
}
