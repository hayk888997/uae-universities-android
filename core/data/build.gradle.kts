plugins {
    id("d3vly.android.library")
    id("d3vly.android.hilt")
    id("d3vly.android.room")
}

android {
    namespace = "com.d3vly.core.data"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "UNIVERSITIES_BASE_URL",
                "\"http://universities.hipolabs.com/\"",
            )
        }
        release {
            buildConfigField(
                "String",
                "UNIVERSITIES_BASE_URL",
                "\"https://universities.hipolabs.com/\"",
            )
        }
    }
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
