import com.android.build.gradle.api.BaseVariant
import com.jfrog.bintray.gradle.Artifact
import com.jfrog.bintray.gradle.BintrayPlugin
import digital.wup.android_maven_publish.AndroidMavenPublishPlugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register

fun Project.configurePublications(uploadTaskName: String = "upload") {

    applyPlugin<BintrayPlugin>()
    applyPlugin<MavenPublishPlugin>()
    applyPlugin<AndroidMavenPublishPlugin>()

    fun BaseVariant.javadocTaskName() = "${name}Javadoc"
    fun BaseVariant.javadocTask() = tasks.getByName(javadocTaskName(), Javadoc::class)
    fun BaseVariant.sources(): FileTree? = sourceSets.flatMap { it.javaDirectories }.asAggregateFileTree()

    androidLibrary.libraryVariants.all {
        tasks.register(javadocTaskName(), Javadoc::class) {
            description = "Generates Javadoc for ${name}."
            isFailOnError = false
            source = sources()!!
            classpath = files(getCompileClasspath(null), project.android.bootClasspath)
            exclude("**/BuildConfig.java")
            exclude("**/R.java")
        }
    }

    val androidPublishVariant by lazy {
        androidLibrary.libraryVariants.single { it.name == android.defaultPublishConfig }
    }

    val sourcesJar = tasks.register("sourcesJar", Jar::class) {
        val sources = androidPublishVariant.sources()
        from(sources)
        classifier = "sources"
    }

    val javadocJar = tasks.register("javadocJar", Jar::class) {
        val javadocTask = androidPublishVariant.javadocTask()
        dependsOn(javadocTask)
        from(javadocTask.destinationDir)
        classifier = "javadoc"
    }

    publishing {
        repositories.maven { url = uri(outputDir) }
        (publications) {
            register(publicationName, MavenPublication::class) {
                from(components["android"])
                artifact(sourcesJar.get())
                artifact(javadocJar.get())

                groupId = libGroupId
                artifactId = libArtifactId
                version = libVersion
                pom.buildXml {
                    "name"..libName
                    "description"..libDescription
                    "url"..libUrl
                    "licenses" {
                        "license" {
                            "name"..licenseName
                            "url"..licenseUrl
                        }
                    }
                    "issueManagement" {
                        "system"..issuesSystem
                        "url"..issuesUrl
                    }
                    "developers" {
                        "developer" {
                            "name"..authorName
                        }
                    }
                    "scm" {
                        "connection".."scm:git:git://$gitRepo"
                        "developerConnection".."scm:git:ssh://$gitRepo"
                        "tag"..gitTag
                        "url"..taggedRepoUrl
                    }
                }
            }
        }
    }

    bintray {
        publish = bintrayPublish
        override = bintrayOverride
        dryRun = bintrayDryRun
        user = bintrayUser
        key = bintrayKey
        filesSpec {
            fileUploads = fileTree(outputDir).map {
                Artifact().apply {
                    file = it
                    setPath(it.toRelativeString(outputDir))
                }
            }
        }
        pkg {
            repo = bintrayRepo
            name = libName
            desc = libDescription
            websiteUrl = libUrl
            issueTrackerUrl = issuesUrl
            githubRepo = "$gitHubUser/$gitHubRepo"
            vcsUrl = "https://$gitRepo"
            setLabels(*bintrayTags)
            setLicenses(licenseName)
            version {
                name = libVersion
                vcsTag = gitTag
                gpg.sign = bintrayGpgSign
                mavenCentralSync {
                    sync = bintrayMavenCentralSync
                    close = if (bintrayMavenCentralClose) "1" else "0"
                    user = sonatypeUser
                    password = sonatypePassword
                }
            }
        }
    }

    val uploadTask = tasks.register(uploadTaskName)
    val bintrayUploadTask = tasks.named("bintrayUpload")

    uploadTask.configure { dependsOn(bintrayUploadTask) }
    bintrayUploadTask.configure { dependsOn(publicationTaskName) }
}
