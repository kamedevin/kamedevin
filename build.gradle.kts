plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

// Every module's Java compile tasks are pinned to 17 via `compileOptions`/`java { }`, but nothing
// pins the Kotlin compiler's own target — left alone it defaults to whatever JDK runs Gradle
// (e.g. 21 on newer Android Studio installs), which Kotlin's target-compatibility validation
// then rejects as a mismatch. Pin every module's Kotlin compile tasks to 17 here once, instead of
// repeating it in every module's build.gradle.kts.
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}
