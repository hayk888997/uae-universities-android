plugins {
    id("d3vly.android.library")
}

android {
    namespace = "com.d3vly.core.domain"
}

dependencies {
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
