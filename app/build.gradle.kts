plugins { id("com.android.application") }

android {
    namespace = "ir.masirservat.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "ir.masirservat.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 7
        versionName = "0.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
