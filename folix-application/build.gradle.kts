// folix-application: 유즈케이스 모듈
// domain만 의존

dependencies {
    implementation(project(":folix-domain"))

    testImplementation("io.mockk:mockk:1.13.13")
}
