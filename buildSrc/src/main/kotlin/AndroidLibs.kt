@file:Suppress("SpellCheckingInspection")

object AndroidLibs {
    const val robolectric = "org.robolectric:robolectric:4.4"

    object KotlinX {
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.3.1"
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.1"

        object ArchCore {
            private const val version = "2.1.0"

            const val common = "androidx.arch.core:core-common:$version"
            const val runtime = "androidx.arch.core:core-runtime:$version"
            const val testing = "androidx.arch.core:core-testing:$version"
        }

        object Fragment {
            private const val version = "1.2.5"

            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
            const val testing = "androidx.fragment:fragment-testing:$version"
        }

        object Navigation {
            private const val version = "2.3.0"

            const val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:$version"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object Lifecycle {
            private const val version = "2.2.0"

            const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Espresso {
            const val core = "androidx.test.espresso:espresso-core:3.3.0"
        }

        object Testing {
            private const val version = "1.3.0"

            const val extJUnit = "androidx.test.ext:junit:1.1.2"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"
            const val monitor = "androidx.test:monitor:$version"
        }
    }

    object Google {
        const val materialComponents = "com.google.android.material:material:1.2.1"
    }

    const val desugarJdk = "com.android.tools:desugar_jdk_libs:1.0.10"
    const val liveDataTestingKtx = "com.jraska.livedata:testing-ktx:1.1.2"
}
