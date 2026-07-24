plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.kamedevin.budget.backup.api"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Deliberately no Google/Drive dependencies here — this module is the swap point that lets
    // :backup:googledrive be replaced by a different backup provider without touching callers.
    // androidx.activity is a plain AndroidX artifact (not Google-specific), needed for the
    // generic sign-in-result bridge every provider's Activity-based auth flow needs.
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.ktx)
    implementation("javax.inject:javax.inject:1")
}
