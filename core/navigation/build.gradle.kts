plugins {
    id("d3vly.android.library")
}

android {
    namespace = "com.d3vly.core.navigation"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
}
