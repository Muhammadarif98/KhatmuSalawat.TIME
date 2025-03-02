// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Убедись, что версия Kotlin актуальна
        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.9.0") // Если используешь Kotlin Serialization
        classpath ("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0") // Для Compose Compiler
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}