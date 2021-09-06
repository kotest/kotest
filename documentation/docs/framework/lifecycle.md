## lifecycle

### Spec Execution Lifecycle

* ref = received SpecRef from scheduler
* SpecExecutor.execute(ref)
* TestEngineListener.specEnter(kclass)
  * If ref is disabled (@Ignored or @EnabledIf annotations):
    * TestEngineListener.specIgnored(kclass)
    * IgnoredSpecListener.specIgnored(kclass, reason)
  * If ref is enabled:
    * spec = create instance of ref
    * SpecInterceptExtension.intercept(spec)
    * If spec is inactive (no enabled root tests):
      * TestEngineListener.specInactive(kclass)
      * InactiveSpecListener.inactive(spec, results)
    * If spec is active (has enabled root tests):
      * TestEngineListener.specStarted(kclass)
      * StartSpecListener.specStarted(kclass)
      * For each isolated spec: create new instance
        * BeforeSpecListener.beforeSpec(spec)
        * Execute tests
        * AfterSpecListener.afterSpec(spec)
      * FinishSpecListener.finishSpec(kclass)
      * TestEngineListener.specFinished(kclass, results)
* TestEngineListener.specExit(kclass, throwable)

### Spec Instantiation Lifecycle

* spec = create instance of ref
* If spec was created via reflection successfully:
  * spec = PostInstantiationExtension.process(spec)
  * SpecInstantiationListener.specInstantiated(spec)
* If spec reflective instantiation failed:
  * SpecInstantiationListener.specInstantiationError(KClass, Throwable)

### Test Lifecycle
