plugins {
    id("d3vly.android.library")
    id("d3vly.android.compose")
}

android {
    namespace = "com.d3vly.core.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
}
