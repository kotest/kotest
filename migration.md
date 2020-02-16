FeatureSpec: Nested `and` scope has been deprecated. Use nested `feature` scope itself.

`Project.registerProjectListeners` has been deprecated in favour of `Project.registerListeners`.

All Specs: All callback functions are now suspendable functions. Therefore you would need to replace `override fun beforeSpec(...` with `override suspend fun beforeSpec(...` and so on or switch to the new DSL style.

The little used `afterDiscovery` callback has been moved from `TestListener` to a new interface `DiscoveryListener`.

In project config, the way some settings are overridden has changed. So, instead of overriding a function, you override a val. For example, `override fun assertionMode() = AssertionMode.Error` is now `override val assertions = AssertionMode.Error`. The former methods are still present, but deprecated.

Spring and Koin extensions are now auto scanned, so you no longer need to manual add them to a project (although they won't be registered twice if you do). Simply adding those modules to your gradle or maven build is sufficient.

TestExtension now simplified to use a single callback

All duration/time based parameters are now using kotlinx.time duration.

until is now suspended
