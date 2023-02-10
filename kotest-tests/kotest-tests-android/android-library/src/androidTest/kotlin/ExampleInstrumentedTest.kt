package com.example.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.assertions.json.shouldMatchJson
import org.intellij.lang.annotations.Language
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
   @Test
   fun useAppContext() {
      // Context of the app under test.
      val appContext = InstrumentationRegistry.getInstrumentation().targetContext
      assertEquals("com.example.myapplication", appContext.packageName)
   }

   @Test
   fun testInjectLanguageAnnotation() {
      @Language("JSON")
      val expected = """
         {
            "foo": "hello"
         }
      """.trimIndent()

      InjectLanguageAnnotation().foo("hello") shouldMatchJson expected
   }
}
