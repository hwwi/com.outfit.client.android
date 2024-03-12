// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
	repositories {
		google()
		jcenter()
	}

	val kotlinVersion by extra("1.4.0")
	val navVersion by extra("2.3.1")

	dependencies {
		classpath("com.android.tools.build:gradle:4.1.1")
		classpath(kotlin("gradle-plugin", version = kotlinVersion))
		classpath(kotlin("allopen", version = kotlinVersion))
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
		classpath("com.google.gms:google-services:4.3.4")
		classpath("com.google.firebase:firebase-crashlytics-gradle:2.3.0")
		// NOTE: Do not place your application dependencies here; they belong
		// in the individual module build.gradle files
	}
}

allprojects {
	repositories {
		google()
		jcenter()
	}
}

tasks.register("clean", Delete::class) {
	delete(rootProject.buildDir)
}