## 5.0.0 November 2021

### _**Kotlin 1.6 is now the minimum supported version**_

See detailed post about 5.0 features and changes [here](blog/release_5.0.md)

### Breaking Changes and removed deprecated methods

* Javascript support has been reworked to use the IR compiler. The legacy compiler is no longer supported. If you are running tests on JS legacy then you will need to continue using Kotest 4.6.x or test only IR.
* `Arb.values` has been removed. This was deprecated in 4.3 in favour of `Arb.sample`. Any custom arbs that override this method should be updated. Any custom arbs that use the recommended `arbitrary` builders are not affected. [#2277](https://github.com/kotest/kotest/issues/2277)
* The Engine no longer logs config to the console during start **by default**. To enable, set the system property `kotest.framework.dump.config` to true. [#2276](https://github.com/kotest/kotest/issues/2276)
* `TextContext` has been renamed to `TestScope`. This is the receiver type used in test lambdas. This change will only affect you if you have custom extension functions that use `TestContext`.
* The experimental datatest functions added in 4.5 have moved to a new module `kotest-framework-datatest`.
* `equalJson` has an added parameter to support the new `shouldEqualSpecifiedJson` assertion
* Changed `PostInstantiationExtension` to be suspendable
* `ConstructorExtension` is now JVM only. Previously it was available on other targets but had no effect outside the JVM.
* When using inspectors, the deprecated `kotlintest.assertions.output.max` system property has been removed. This was replaced with `kotest.assertions.output.max` in 4.0.
* The deprecated `isolation` setting in Specs has been removed. Use `isolationMode`.
* Moved `assertionMode` from `TestCase` to test case config.
* The deprecated `RuntimeTagExtension` has been undeprecated but moved to a new package.
* Removed deprecated `shouldReceiveWithin` and `shouldReceiveNoElementsWithin` channel matchers.



### Fixes

#### Test Framework

* Support composed annotations plus caching of annotation lookups [#2279](https://github.com/kotest/kotest/issues/2279)
* Fix autoClose lazyness [#2395](https://github.com/kotest/kotest/issues/2395)
* Strip . from JS test names #[#2483](https://github.com/kotest/kotest/issues/2483)
* Escape colons for team city output [#2445](https://github.com/kotest/kotest/issues/2445)


#### Assertions

* String matchers now also work on CharSequence where applicable [#2278](https://github.com/kotest/kotest/issues/2278)
* `withEnvironment` fails with empty system environment variables on Linux [#2615](https://github.com/kotest/kotest/issues/2615)
* Fixes ShouldContainExactly for collection containing byte arrays [#2360](https://github.com/kotest/kotest/issues/2360)
* 2412 Makes Sequence.containExactly work for single pass sequences [#2413](https://github.com/kotest/kotest/issues/2413)
* Fix withClue and assertSoftly for coroutines switching threads [#2447](https://github.com/kotest/kotest/issues/2447)
* Allow clues to be added to timeouts [#2230](https://github.com/kotest/kotest/issues/2230)
* Possible confusion between shouldContainExactlyInAnyOrder overloads [#2587](https://github.com/kotest/kotest/issues/2587)
* shouldBeEqualToComparingFields handles arrays and computed properties  [#2475](https://github.com/kotest/kotest/issues/2475)


#### Property Testing

* Fix Arb.long/ulong producing values outside range [#2330](https://github.com/kotest/kotest/issues/2330)
* Fix Arb.localDate take into account the date/month portion of the specified minDate [#2370](https://github.com/kotest/kotest/issues/2370)
* updated FloatShrinker and DoubleShrinker to regard mantissa bit count as complexity measure [#2379](https://github.com/kotest/kotest/issues/2379)
* Bugfix/2380 arb filter shrinks [#2434](https://github.com/kotest/kotest/issues/2434)
* PropTestConfig's iterations parameter not being respected [#2428](https://github.com/kotest/kotest/issues/2428)
* Fix endless recursion in 2-arity checkAll [#2510](https://github.com/kotest/kotest/issues/2510)
* Fix wrong index in forAll4 [#2533](https://github.com/kotest/kotest/issues/2533)
* StackOverflow when using checkAll [#2513](https://github.com/kotest/kotest/issues/2513)
* fix Arb.uuid performance by caching RgxGen instances for reuse [#2479](https://github.com/kotest/kotest/issues/2479)
* Property Module: Mutation of global PropertyTesting fix for Kotlin/Native [#2469](https://github.com/kotest/kotest/issues/2469)
* Add BigDecimal edge case for equals vs compareTo discrepancy [#2403](https://github.com/kotest/kotest/issues/2403)
* Fix InstantRange random nanos selection when the seconds equal the ends of the range [#2441](https://github.com/kotest/kotest/issues/2441)



### Features


#### Test Framework

* Javascript IR support has been added.
* Native test support has been added.
* Config option to enable [coroutine debugging](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-debug)
* Config option to enable `TestCoroutineDispatcher`s in tests.
* Failfast option added [see docs] [#2243](https://github.com/kotest/kotest/issues/2243)
* Unfinished tests should error [#2281](https://github.com/kotest/kotest/issues/2281)
* Added option to fail test run if no tests were executed [#2287](https://github.com/kotest/kotest/issues/2287)
* Added @RequiresTag for improved spec exclude capability [#1820](https://github.com/kotest/kotest/issues/1820)
* Add fun interace to EnabledCondition [#2343](https://github.com/kotest/kotest/issues/2343)
* In Project Config, `beforeAll` / `afterAll` are now deprecated and `beforeProject` / `afterProject`, which are suspend functions, have been added [#2333](https://github.com/kotest/kotest/issues/2333)
* Delete temporary directories recursively when using `tempdir` [#2227](https://github.com/kotest/kotest/issues/2227)
* `projectContext` is now available as an extension value inside a test lamba to provide access to the runtime state of the test engine.
* Added standalone module that can be used by tool builders to launch Kotest [#2416](https://github.com/kotest/kotest/issues/2416)
* Datatest module is now published for all targets
* Framework now supports a project wide timeout [#2273](https://github.com/kotest/kotest/issues/2273)
* New `ProjectExtension` extension point has been added.
* Allow extensions to be registered via `@ApplyExtension` annotation [#2551](https://github.com/kotest/kotest/issues/2551)
* Add logging to test scopes [#2443](https://github.com/kotest/kotest/issues/2443)
* Added `DisplayNameFormatterExtension` extension point [#2507](https://github.com/kotest/kotest/issues/2507)
* Add configuration option to send full test name paths to junit 5 [#2525](https://github.com/kotest/kotest/issues/2525)
* Added support for @Nested in AnnotationSpec [#2367](https://github.com/kotest/kotest/issues/2367)
* Added system properties for filtering tests and specs [#2547](https://github.com/kotest/kotest/issues/2547)
* Should error when Container tests do not contain a nested test [#2383](https://github.com/kotest/kotest/issues/2383)


#### Assertions

* Return the resulting value of the function block from shouldCompleteWithin [#2309](https://github.com/kotest/kotest/issues/2309)
* Added `shouldEqualSpecifiedJson` to match a JSON structure on a subset of (specified) keys. [#2298](https://github.com/kotest/kotest/issues/2298)
* `shouldEqualJson` now supports high-precision numbers [#2458](https://github.com/kotest/kotest/issues/2458)
* Added `shouldHaveSameStructureAs` to file matchers
* Added `shouldHaveSameStructureAndContentAs` to file matchers
* Inspectors are now inline so can now contain suspendable functions [#2657](https://github.com/kotest/kotest/issues/2657)
* String.shouldHaveLengthBetween should accept ranges [#2643](https://github.com/kotest/kotest/issues/2643)
* beOneOf assertion now tells you what the missing value was [#2624](https://github.com/kotest/kotest/issues/2624)
* String matchers have been updated to work with any `CharSequence` [#2278](https://github.com/kotest/kotest/issues/2278)
* Add shouldThrowMessage matcher [#2376](https://github.com/kotest/kotest/issues/2376)
* Add Percentage tolerance matchers [#2404](https://github.com/kotest/kotest/issues/2404)
* Add NaN matchers to Float [#2419](https://github.com/kotest/kotest/issues/2419)
* Replaced eager matchers with lazy counterpart [#2454](https://github.com/kotest/kotest/issues/2454)
* Compare JSON literals in Strict mode [#2464](https://github.com/kotest/kotest/issues/2464)
* Added matchers for empty json [#2543](https://github.com/kotest/kotest/issues/2543)
* Added comparable matcher result and applied to shouldContainExactly [#2559](https://github.com/kotest/kotest/issues/2559)
* Updated iterables.shouldContain to return the receiver for chaining
* Added aliases for inspectors [#2578](https://github.com/kotest/kotest/issues/2578)
* Inspectors should return the collection to allow chaining [#2588](https://github.com/kotest/kotest/issues/2588)
* Disable string diff in intellij [#1999](https://github.com/kotest/kotest/issues/1999)
* CompareJsonOptions [#2523](https://github.com/kotest/kotest/issues/2523)
* Support for ignoring unknown keys in JSON asserts [#2303](https://github.com/kotest/kotest/issues/2303)
* Add support for Linux ARM64 and macOS ARM64 (Silicon) targets. [#2449](https://github.com/kotest/kotest/issues/2449)


#### Property Testing

* Change usages of Char.toInt() to Char.code since Kotlin 1.5. Migrate codepoints to Codepoint companion object. [#2283](https://github.com/kotest/kotest/issues/2283)
* Generex has been replaced with Rgxgen [#2323](https://github.com/kotest/kotest/issues/2323)
* Improve Arb function naming [#2310](https://github.com/kotest/kotest/issues/2310)
* Improve Arb.primitive consistency [#2299](https://github.com/kotest/kotest/issues/2299)
* Add Arb.ints zero inclusive variants [#2294](https://github.com/kotest/kotest/issues/2294)
* Add unsigned types for Arb [#2290](https://github.com/kotest/kotest/issues/2290)
* Added arb for ip addresses V4 [#2407](https://github.com/kotest/kotest/issues/2407)
* Added arb for hexidecimal codepoints [#2409](https://github.com/kotest/kotest/issues/2409)
* Added continuation arbs builder that allow arbs to be used in a similar fashion to for comprehensions. [#2494](https://github.com/kotest/kotest/issues/2494)
* Added Arb.zip as an alias for Arb.bind [#2644](https://github.com/kotest/kotest/issues/2644)
* Add primitive arrays to Arb [#2301](https://github.com/kotest/kotest/issues/2301)
* improved geo location generator [#2390](https://github.com/kotest/kotest/issues/2390)
* Fix LocalDate arb generating wrong dates outside constraints [#2405](https://github.com/kotest/kotest/issues/2405)
* Add zip for Exhaustive [#2415](https://github.com/kotest/kotest/issues/2415)
* Add cartesian pairs helpers for Exhaustive [#2415](https://github.com/kotest/kotest/issues/2415)
* Add Arb.distinct that will terminate [#2262](https://github.com/kotest/kotest/issues/2262)
* Add arb for timezone [#2421](https://github.com/kotest/kotest/issues/2421)
* Added auto classifiers [#2267](https://github.com/kotest/kotest/issues/2267)
* Added arity8 and arity9 forall for table testing [#2444](https://github.com/kotest/kotest/issues/2444)
* Property Module: Allow global seed configuration. Synchronize defaults. [#2439](https://github.com/kotest/kotest/issues/2439)
* support Arb.bind for more complex data classes [#2532](https://github.com/kotest/kotest/issues/2532)
* Shrink when using `Arb.bind` [#2542](https://github.com/kotest/kotest/issues/2542)
* Introduce constraints for property testing [#2492](https://github.com/kotest/kotest/issues/2492)
* Property testing should use bind as default for data class [#2355](https://github.com/kotest/kotest/issues/2355)
* Platform independent double shrinker [#2517](https://github.com/kotest/kotest/issues/2517)
* Arb.pair should return Arb<Pair<K, V>> [#2563](https://github.com/kotest/kotest/issues/2563)
* Add support for Linux ARM64 and macOS ARM64 (Silicon) targets. [#2449](https://github.com/kotest/kotest/issues/2449)





### Deprecations

* `CompareMode` /`CompareOrder` for `shouldEqualJson` has been deprecated in favor of `compareJsonOptions { }`
* `TestStatus` has been deprecated and `TestResult` reworked to be an ADT. If you were pattern matching on `TestResult.status` you can now match on the result instance itself.
* `val name` inside `Listener` has been deprecated. This was used so that multiple errors from multiple before/after spec callbacks could appear with customized unique names. The framework now takes care of making sure the names are unique so this val is no longer needed and is now ignored.
* `SpecExtension.intercept(KClass)` has been deprecated in favor of `SpecRefExtension` and `SpecExtension.intercept(spec)`. The deprecated method had ambigious behavior when used with an IsolationMode that created multiple instances of a spec. The new methods have precise guarantees of when they will execute.
* The global `configuration` object has been deprecated as the first step to removing this global var. To configure the project, the preferred method remains [ProjectConfig](/), which is detected on all three platforms (JVM, JS and Native).
* `SpecInstantiationListener` has been deprecated in favour of `InstantiationListener` and `InstantiationErrorListener`, both of which support coroutines in the callbacks. `SpecInstantiationListener` is a hold-over from before coroutines existed and will be removed in a future version.
* The `listeners` method to add listeners to a Spec has been deprecated. When adding listeners to specs directly, you should now prefer `fun extensions()` rather than `fun listeners()`.
* `SpecIgnoredListner` (note the typo) has been renamed to `InactiveSpecListener`.


### Contributors
Thanks to all authors who contributed to this huge release. In alphabetical order (all commits since 4.6.0)

AJ Alt, Ali Khaleqi Yekta, Alphonse Bendt, Andrew Tasso, Ashish Kumar, Ashish Kumar Joy, Bart van Helvert, Charles Korn, Christoph Pickl, Cory Thomas, dave08, Derek Chen-Becker, dimsuz, Emil Kantis, Federico Aloi, Hugo Martins, IgorTs2004, Imran Settuba, Ing. Jan Kaláb, IvanPavlov1995, Javier Segovia Córdoba, Jean-Michel Fayard, Jerry Preissler, Jim Schneidereit, Leonardo Colman, Marcono1234, Marvin Sielenkemper, Mervyn McCreight, Michael Werner, Mikhail Pogorelov, Mitchell Yuwono, Nico Richard, niqdev, OliverO2, Rustam Musin, Scott Busche, Sebastian Schuberth, Simon Vergauwen, sksamuel, Srki Rakic, SuhyeonPark, Tobie Wee



## 4.6.3 September 2021

### Fixes

* StackOverflow when using checkAll with certain arity functions [#2513](https://github.com/kotest/kotest/issues/2513)
* Added arity8 and arity9 forall for table testing [#2444](https://github.com/kotest/kotest/issues/2444)


## 4.6.2 August 2021

### Fixes

* Reverted use of 1.5 API introduced erroneously in 4.6.1
* autoClose breaks lazy [#2388](https://github.com/kotest/kotest/issues/2388)
* minDate not respected in Arb.localDate [#2369](https://github.com/kotest/kotest/issues/2369)
* Sequence.containExactly should work for single pass sequences [#2412](https://github.com/kotest/kotest/issues/2412)
* BigDecimal edge case for equals vs compareTo discrepancy [#2403](https://github.com/kotest/kotest/issues/2403)
* PropTestConfig's iterations parameter is not respected. [#2428](https://github.com/kotest/kotest/issues/2428)
* tempfile and tempdir should fail test when deletion fails [#2351](https://github.com/kotest/kotest/issues/2351)


## 4.6.1 July 2021

### Fixes

* HTMLReporter - css not loading (href of the file is absolute, not relative) [#2342](https://github.com/kotest/kotest/issues/2342)
* Annotations such as @Ignore and @Isolate now work when composed [#2279](https://github.com/kotest/kotest/issues/2279)
* Finalize spec is now properly called in all situations [#2272](https://github.com/kotest/kotest/issues/2272)
* Arb.bigDecimal bounds are not being honored [#2357](https://github.com/kotest/kotest/issues/2357)
* Fix for running individual test using WordSpec inside intellij [#2319](https://github.com/kotest/kotest/issues/2319)

## 4.6.0 May 2021

This is a small release which **adds support for Kotlin 1.5** while remaining compatible with Kotlin 1.4.x

### Bugfixes.

* All internal logging now uses lazy functions which offers a significant speed up on large test suites.
  Thanks to [Łukasz Wasylkowski](https://github.com/lwasyl) who spent considerable time tracking down this performance issue.
* Fixed false negative results by Inspectors when used inside assertSoftly. [#2245](https://github.com/kotest/kotest/issues/2245)

### Features / Improvement

* Test config can now be specified at the test container level in addition to the leaf level [#1370](https://github.com/kotest/kotest/issues/1370) [#2050](https://github.com/kotest/kotest/issues/2050) [#2065](https://github.com/kotest/kotest/issues/2065)
* In data driven tests, added `IsStableType` annotation which when use on a type, kotest will call `toString` method on
  that type for creating test name. See [updated docs](https://kotest.io/docs/framework/data-driven-testing.html) [#2248](https://github.com/kotest/kotest/issues/2248)
* In data driven tests, added `WithDataTestName` interface which allow a type to modify the test name generated.
  See [updated docs](https://kotest.io/docs/framework/data-driven-testing.html) [#2248](https://github.com/kotest/kotest/issues/2248)
* Reflection methods are cached to avoid slow reflection calls.
* Added experimental versions of `eventually`, `until`, and `continually` that don't use `kotlin.time` internally.
  See [updated docs](https://kotest.io/docs/framework/concurrency/eventually.html) [#2149](https://github.com/kotest/kotest/issues/2149)
* Coroutines upgraded to 1.5 which also allows us to release assertions/property tests for watchosX64
* WatchosX64 artifacts released for assertions and property tests.

### Contributors

Ashish Kumar Joy, Jim Schneidereit, Łukasz Wasylkowski, sksamuel
