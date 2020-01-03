package io.kotest.androidtest

import androidx.test.core.app.launchActivity
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.android.synthetic.main.activity_main.textview

class MainActivityTest : StringSpec() {
    
    init {
        "Hello World test" {
            val scenario = launchActivity<MainActivity>()
            
            scenario.onActivity { 
                it.textview.text shouldBe "Kotest!"
            }
        }
    }
}