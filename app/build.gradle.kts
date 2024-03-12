import org.jetbrains.kotlin.config.KotlinCompilerVersion
import java.util.Properties
import java.io.FileInputStream

plugins {
	id("com.android.application")
	kotlin("android")
	kotlin("android.extensions")
	kotlin("kapt")
	kotlin("plugin.allopen")
	id("androidx.navigation.safeargs.kotlin")
	id("com.google.gms.google-services")
	id("com.google.firebase.crashlytics")
}

allOpen {
	annotation("com.airbnb.epoxy.EpoxyModelClass")
	annotation("com.outfit.client.android.data.AllOpen")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
	compileSdkVersion(30)
	signingConfigs {
		create("upload") {
			keyAlias = keystoreProperties["keyAlias"] as String
			keyPassword = keystoreProperties["keyPassword"] as String
			storeFile = file(keystoreProperties["storeFile"] as String)
			storePassword = keystoreProperties["storePassword"] as String
		}
	}
	defaultConfig {
		applicationId = "com.outfit.client.android"
		minSdkVersion(19)
		targetSdkVersion(30)
		// AppUpdateInfo.updatePriority 를 Play Console 에서 지정 불가능
		// -> VersionCode 를 XXX_XXX_XXX 형식으로 SemVer 로 사용하여 비교
		// https://issuetracker.google.com/issues/133299031#comment14
		versionCode = 35
		versionName = "0.0.35"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		multiDexEnabled = true
		proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		vectorDrawables.useSupportLibrary = true
		javaCompileOptions {
			annotationProcessorOptions {
				arguments["room.schemaLocation"] = "$projectDir/schemas"
				// Epoxy
				arguments["enableParallelEpoxyProcessing"] = "true"
				arguments["logEpoxyTimings"] = "true"
			}
		}
		setProperty("archivesBaseName", "${applicationId}-${versionName}(${versionCode})")
	}
	buildTypes {
		getByName("debug") {
			applicationIdSuffix = ".debug"
			isDebuggable = true

			buildConfigField(
				"String",
				"URL_API_OUTFIT",
				"\"http://10.0.2.2:4903\""
			)
		}
		create("staging") {
			applicationIdSuffix = ".staging"
			isDebuggable = true
			signingConfig = signingConfigs.getByName("debug")

			buildConfigField(
				"String",
				"URL_API_OUTFIT",
				"\"https://api.outfit.photos\""
			)
		}
		getByName("release") {
			signingConfig = signingConfigs["upload"]
			isDebuggable = false
			isMinifyEnabled = true

			buildConfigField(
				"String",
				"URL_API_OUTFIT",
				"\"https://api.outfit.photos\""
			)
		}
	}
	compileOptions {
//		coreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	androidExtensions {
		isExperimental = true
	}
	buildFeatures {
		viewBinding = true
		dataBinding = true
	}
}
kapt {
	correctErrorTypes = true
}

val navVersion: String by rootProject.extra

val retrofitVersion = "2.9.0"
val okHttpVersion = "4.9.0"
val daggerVersion = "2.29.1"
val glideVersion = "4.11.0"
val autoDisposeVersion = "1.2.0"
val leakCanaryVersion = "2.3"
val rxBindingVersion = "3.0.0-alpha1"
val kotprefVersion = "2.11.0"
val gsonVersion = "2.8.5"
val roomVersion = "2.2.5"
val pagingVersion = "2.1.2"
val epoxyVersion = "4.1.0"
val lifecycleVersion = "2.2.0"
val work_version = "2.4.0"
dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
	implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	implementation("com.google.android.play:core:1.8.3")
	implementation("com.google.android.play:core-ktx:1.8.1")

	implementation("com.google.firebase:firebase-analytics-ktx:18.0.0")
	implementation("com.google.firebase:firebase-messaging-ktx:21.0.0")
	implementation("com.google.firebase:firebase-crashlytics-ktx:17.2.2")

	implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
	implementation("com.squareup.moshi:moshi-adapters:1.11.0")

	implementation("androidx.multidex:multidex:2.0.1")

	implementation("com.googlecode.libphonenumber:libphonenumber:8.12.12")

	implementation("com.chibatching.kotpref:kotpref:$kotprefVersion")
	implementation("com.chibatching.kotpref:initializer:$kotprefVersion")
	implementation("com.chibatching.kotpref:livedata-support:$kotprefVersion")
	implementation("com.chibatching.kotpref:enum-support:$kotprefVersion")
//	implementation("com.chibatching.kotpref:gson-support:$kotprefVersion")
//	implementation("com.google.code.gson:gson:$gsonVersion")

	implementation("com.airbnb.android:epoxy:$epoxyVersion")
	implementation("com.airbnb.android:epoxy-databinding:$epoxyVersion")
	implementation("com.airbnb.android:epoxy-paging:$epoxyVersion")
	implementation("com.airbnb.android:epoxy-glide-preloading:$epoxyVersion")
	kapt("com.airbnb.android:epoxy-processor:$epoxyVersion")

	// object mapper
	implementation("org.mapstruct:mapstruct:1.4.1.Final")
	kapt("org.mapstruct:mapstruct-processor:1.4.1.Final")

	//db
	implementation("androidx.room:room-runtime:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	kapt("androidx.room:room-compiler:$roomVersion")

	//paging
	implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
	testImplementation("androidx.paging:paging-common-ktx:$pagingVersion")

	kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION)
	kotlin("reflect", KotlinCompilerVersion.VERSION)

	implementation("androidx.appcompat:appcompat:1.2.0")
	implementation("androidx.constraintlayout:constraintlayout:2.0.4")
	implementation("com.google.android.material:material:1.3.0-alpha03")

	// worker
	implementation("androidx.work:work-runtime-ktx:$work_version")

	// Lifecycle : https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

	//view
	implementation("androidx.fragment:fragment:1.2.5")
	implementation("androidx.fragment:fragment-ktx:1.2.5")
	implementation("androidx.viewpager2:viewpager2:1.0.0")
	implementation("androidx.recyclerview:recyclerview:1.1.0")
	implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
	implementation("androidx.exifinterface:exifinterface:1.3.1")
	// permissions
	implementation("com.karumi:dexter:6.2.1")
	//network
	implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
	implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
//    implementation "com.squareup.retrofit2:adapter-rxjava2:$retrofit_version"
	implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
	implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
	debugImplementation("com.github.chuckerteam.chucker:library:3.4.0")
	"stagingImplementation"("com.github.chuckerteam.chucker:library:3.4.0")
	releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.4.0")

	//debug
	implementation("com.jakewharton.timber:timber:4.7.1")
	implementation("com.facebook.stetho:stetho:1.5.1")
	implementation("com.facebook.stetho:stetho-okhttp3:1.5.1")
	implementation("com.facebook.stetho:stetho-timber:1.5.1")
//	debugImplementation("com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion")

	implementation("com.github.bumptech.glide:glide:$glideVersion")
	implementation("com.github.bumptech.glide:recyclerview-integration:$glideVersion") {
		isTransitive = false
	}
	kapt("com.github.bumptech.glide:compiler:$glideVersion")

	// dagger
	kapt("com.google.dagger:dagger-compiler:$daggerVersion")
	kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
	implementation("com.google.dagger:dagger:$daggerVersion")
	implementation("com.google.dagger:dagger-android:$daggerVersion")
	implementation("com.google.dagger:dagger-android-support:$daggerVersion")
	compileOnly("com.squareup.inject:assisted-inject-annotations-dagger2:0.6.0")
	kapt("com.squareup.inject:assisted-inject-processor-dagger2:0.6.0")

	testImplementation("junit:junit:4.13.1")
	androidTestImplementation("androidx.test:runner:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
	implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
	implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
}
