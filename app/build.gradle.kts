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


    implementation ("com.apollographql.apollo3:apollo-runtime:3.7.2")
    implementation ("com.apollographql.apollo3:apollo-normalized-cache-sqlite:3.7.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation("com.apollographql.apollo3:apollo-testing-support:3.7.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}