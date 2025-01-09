plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}


android {
    namespace = "com.lakshan.blinklabs.graphqlconnectorlibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate { // Ensures components are evaluated after the project is configured
                from(components["release"]) // Use 'release' for Android libraries
            }
            groupId = "com.github.LakshanOnGIT" // Replace with your GitHub username
            artifactId = "graphqlconnector"   // Replace with your artifact name
            version = "1.0.0"                 // Replace with your version
        }
    }
}

dependencies {
    implementation (libs.apollo.runtime)
    implementation (libs.apollo.normalized.cache.sqlite)
    implementation (libs.logging.interceptor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.apollo.testing)
    androidTestImplementation (libs.mockwebserver3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}