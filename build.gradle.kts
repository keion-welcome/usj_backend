plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	// --- 基本的なWebアプリケーションに必要なライブラリ ---
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.postgresql:postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// --- 認証・認可に必要なライブラリ ---
	// 1. Spring Security本体 (認証やアクセス制御の仕組みを提供)
	implementation("org.springframework.boot:spring-boot-starter-security")

	// 2. JWTを簡単に作成・検証するためのライブラリ (3点セット)
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// --- OpenAPI/Swagger ドキュメント生成 ---
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	// --- WebSocket ---
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// --- テスト用のライブラリ ---
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	
	// --- Mockito Kotlin: Kotlinサポート付きモックライブラリ ---
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	
	// --- DB-Rider: データベーステスト用ライブラリ ---
	testImplementation("com.github.database-rider:rider-core:1.40.0")
	testImplementation("com.github.database-rider:rider-junit5:1.40.0")
	
	// --- H2: インメモリデータベース（テスト用） ---
	testRuntimeOnly("com.h2database:h2")

	// 修正点：LombokはKotlinでは一般的に不要なため削除しました。
	// Kotlinのdata classが同様の機能を提供します。
}

kotlin {
	jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// カスタムテストタスクの追加
tasks.register<Test>("unitTest") {
	description = "Run unit tests"
	group = "verification"
	include("**/unit/**")
	useJUnitPlatform()
}

tasks.register<Test>("integrationTest") {
	description = "Run integration tests"
	group = "verification"
	include("**/integration/**")
	useJUnitPlatform()
	shouldRunAfter("unitTest")
}

tasks.register<Test>("allTests") {
	description = "Run all tests"
	group = "verification"
	dependsOn("unitTest", "integrationTest")
}
