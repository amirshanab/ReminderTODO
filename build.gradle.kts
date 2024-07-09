plugins {
    alias(libs.plugins.android.application) apply false
}

// Remove the repositories block from here

allprojects {
    // No need to define repositories here
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("com.google.gms:google-services:4.3.10")
    }
}
