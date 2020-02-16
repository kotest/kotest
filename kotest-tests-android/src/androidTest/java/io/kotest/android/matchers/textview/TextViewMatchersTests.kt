package io.kotest.android.matchers.textview

import android.graphics.Color
import android.widget.TextView
import androidx.test.core.app.launchActivity
import io.kotest.androidtest.MainActivity
import io.kotlintest.assertSoftly
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import kotlinx.android.synthetic.main.activity_main.textview_kotest
import kotlinx.android.synthetic.main.activity_main.textview_kotest_aligned_end
import kotlinx.android.synthetic.main.activity_main.textview_kotest_allcaps
import kotlinx.android.synthetic.main.activity_main.textview_kotest_colored

class TextViewMatchersTests : FreeSpec() {
    
    init {
        "Have Text" {
            val scenario = launchActivity<MainActivity>()
            
            scenario.onActivity {
                assertSoftly { 
                    it.textview_kotest.also { tv ->
                        tv shouldHaveText "kotest"
                        tv shouldNotHaveText "a"
                        tv shouldNotHaveText ""
                        
                        shouldThrow<AssertionError> { tv shouldHaveText "a" }
                        shouldThrow<AssertionError> { tv shouldHaveText "" }
                        shouldThrow<AssertionError> { tv shouldNotHaveText  "kotest" }
                    }
                }
            }
        }
        
        "Have Color" {
            val scenario = launchActivity<MainActivity>()
    
            scenario.onActivity {
                assertSoftly {
                    it.textview_kotest_colored.also { tv ->
                        tv shouldHaveTextColor Color.BLACK
                        tv shouldNotHaveTextColor Color.RED
                        tv shouldHaveTextColorId android.R.color.black
                        tv shouldNotHaveTextColorId android.R.color.white
    
    
                        shouldThrow<AssertionError> { tv shouldNotHaveTextColor Color.BLACK }
                        shouldThrow<AssertionError> { tv shouldHaveTextColor Color.RED }
                        shouldThrow<AssertionError> { tv shouldNotHaveTextColorId android.R.color.black }
                        shouldThrow<AssertionError> { tv shouldHaveTextColorId android.R.color.white }
                    }
                }
            }
        }
        
        "All Caps" {
            val scenario = launchActivity<MainActivity>()
    
            scenario.onActivity {
                it.textview_kotest_allcaps.shouldBeAllCaps()
                shouldThrow<AssertionError> { it.textview_kotest_allcaps.shouldNotBeAllCaps() }
                
                it.textview_kotest.shouldNotBeAllCaps()
                shouldThrow<AssertionError> { it.textview_kotest.shouldBeAllCaps() }
            }
        }
        
        "Alignment" {
            val scenario = launchActivity<MainActivity>()
    
            scenario.onActivity {
                it.textview_kotest_aligned_end shouldHaveTextAlignment TextView.TEXT_ALIGNMENT_TEXT_END
                it.textview_kotest_aligned_end shouldNotHaveTextAlignment TextView.TEXT_ALIGNMENT_CENTER
                shouldThrow<AssertionError> { it.textview_kotest_aligned_end shouldHaveTextAlignment TextView.TEXT_ALIGNMENT_CENTER }
                shouldThrow<AssertionError> { it.textview_kotest_aligned_end shouldNotHaveTextAlignment TextView.TEXT_ALIGNMENT_TEXT_END }
        
                it.textview_kotest.shouldNotBeAllCaps()
                shouldThrow<AssertionError> { it.textview_kotest.shouldBeAllCaps() }
            } 
        }
    }
}
