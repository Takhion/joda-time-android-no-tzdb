import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependenciesSpec

fun PluginDependenciesSpec.android(module: String) = id("com.android.$module")

val publishing = configuration<PublishingExtension>()
val bintray = configuration<BintrayExtension>()

val SourceSetContainer.main: SourceSet get() = this["main"]
val NamedDomainObjectContainer<AndroidSourceSet>.main: AndroidSourceSet get() = this["main"]

internal val Project.java get() = the<JavaPluginConvention>()
internal val Project.android get() = (extensions.getByName("android") as BaseExtension)
internal val Project.androidLibrary get() = android as LibraryExtension
