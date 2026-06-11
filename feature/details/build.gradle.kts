plugins {
    id("d3vly.android.library.parcelize")
    id("d3vly.android.hilt")
}

android {
    namespace = "com.d3vly.feature.details"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(project(":core:domain"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
