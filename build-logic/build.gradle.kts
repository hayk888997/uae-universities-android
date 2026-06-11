plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:8.13.2")
    implementation("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:2.0.21")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.0.21")
    implementation("org.jetbrains.kotlin.plugin.parcelize:org.jetbrains.kotlin.plugin.parcelize.gradle.plugin:2.0.21")
    implementation("com.google.dagger.hilt.android:com.google.dagger.hilt.android.gradle.plugin:2.57.2")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.28")
}
