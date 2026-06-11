plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    add("implementation", "androidx.room:room-runtime:2.7.2")
    add("implementation", "androidx.room:room-ktx:2.7.2")
    add("ksp", "androidx.room:room-compiler:2.7.2")
}
