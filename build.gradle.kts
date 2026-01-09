@Suppress("DSL_SCOPE_VIOLATION") // Layout alias suppress
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}
true // Needed for some Kotlin DSL quirks in older versions