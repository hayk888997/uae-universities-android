import com.android.build.api.dsl.CommonExtension

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

pluginManager.withPlugin("com.android.base") {
    extensions.configure<CommonExtension<*, *, *, *, *, *>>("android") {
        buildFeatures {
            compose = true
        }
    }
}
