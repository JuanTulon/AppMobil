// Archivo: build.gradle.kts (Raíz)
plugins {
    id("com.android.application") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false
}

// ESTO ES LO QUE FALTABA:
tasks.named<Wrapper>("wrapper") {
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.BIN
}