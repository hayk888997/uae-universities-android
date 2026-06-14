plugins {
    id("d3vly.android.library")
}

android {
    namespace = "com.d3vly.core.testing"
}

dependencies {
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
}
