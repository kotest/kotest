## lifecycle

### Spec Execution Lifecycle

* ref = received SpecRef from scheduler
* SpecExecutor.execute(ref)
* TestEngineListener.specEnter(kclass)
  * If ref is disabled (@Ignored or @EnabledIf annotations):
    * TestEngineListener.specIgnored(kclass)
    * SpecDisabledListener.specDisabled(kclass, reason)
  * If ref is enabled:
    * spec = create instance of ref
    * SpecInterceptExtension.intercept(spec)
    * If spec is inactive (no enabled root tests):
      * TestEngineListener.specIgnored(kclass)
      * SpecInactiveListener.specInactive(spec, results)
    * If spec is active (has enabled root tests):
      * TestEngineListener.specStarted(kclass)
      * PrepareSpecListener.prepareSpec(kclass)
      * For each isolated spec: create new instance
        * BeforeSpecListener.beforeSpec(spec)
        * Execute tests
        * AfterSpecListener.afterSpec(spec)
      * FinishSpecListener.finishSpec(kclass)
      * TestEngineListener.specFinished(kclass)
* TestEngineListener.specExit(kclass)

### Spec Instantiation Lifecycle

* spec = create instance of ref
* If spec was created via reflection successfully:
  * spec = PostInstantiationExtension.process(spec)
  * SpecInstantiationListener.specInstantiated(spec)
* If spec reflective instantiation failed:
  * SpecInstantiationListener.specInstantiationError(KClass, Throwable)

### Test Lifecycle
