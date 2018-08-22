rootProject.name = "joda-time-android-no-tzdb"
include("lib", "app")

gradle.rootProject {
    allprojects {
        repositories {
            google()
            jcenter()
        }
    }
}
