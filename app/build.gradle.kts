plugins {
    id("handwriting.java-conventions")
    application
}

dependencies {
    implementation(project(":capture"))
    implementation(project(":synth"))
    implementation(project(":render"))
    implementation(project(":page"))
}

application {
    mainClass = "com.handwriting.app.Main"
}
