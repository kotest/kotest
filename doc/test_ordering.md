                                                         
### Test Ordering

When running multiple tests from a Spec, there's a certain order on how to execute them.
 
 By default, a **sequential** order is used (order that tests are defined in the spec), but it's also possible to configure them to be executed in a **random** order.

This setting can be configured in either a `Spec` or in [ProjectConfig](/doc/reference.md#project-config), by overriding the `testCaseOrder` function. If both exist, the `Spec`'s configuration will have priority.

**Note**: Nested tests will always run in discovery order (sequential)

```kotlin
class SequentialSpec : StringSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential
    
    init {
      "This test runs first" {
        // Test
      }
      
      "This test runs second" {
        // Test
      }
    }
}

class RandomSpec : StringSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random
    
    init {
      "This test may run first or second" {
        // Test
      }
      
      "This test may run first or second" {
        // Test
      }
    }

}

```