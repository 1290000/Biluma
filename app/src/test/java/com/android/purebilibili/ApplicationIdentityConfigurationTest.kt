package com.android.purebilibili

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationIdentityConfigurationTest {

    private val appDirectory = listOf(File("app"), File(".")).first { directory ->
        File(directory, "src/main/AndroidManifest.xml").isFile
    }

    @Test
    fun releaseAndDevUseBilumaIdentityWithStableSourceNamespace() {
        val buildFile = File(appDirectory, "build.gradle.kts").readText()
        val devConfiguration = buildFile.substringAfter("create(\"dev\") {")
            .substringBefore("\n        }")

        assertTrue(buildFile.contains("applicationId = \"com.biluma.app\""))
        assertTrue(buildFile.contains("namespace = \"com.android.purebilibili\""))
        assertTrue(devConfiguration.contains("applicationIdSuffix = \".dev\""))
        assertTrue(devConfiguration.contains("resValue(\"string\", \"app_name\", \"Biluma Dev\")"))
    }

    @Test
    fun localizedResourcesDoNotOverrideEnglishVariantNames() {
        val resources = File(appDirectory, "src/main/res")
        val defaultStrings = File(resources, "values/strings.xml").readText()

        assertTrue(defaultStrings.contains("<string name=\"app_name\" translatable=\"false\">Biluma</string>"))
        resources.listFiles().orEmpty()
            .filter { directory -> directory.isDirectory && directory.name.startsWith("values-") }
            .flatMap { directory -> directory.listFiles().orEmpty().filter { file -> file.extension == "xml" } }
            .forEach { file ->
                assertFalse(file.readText().contains("name=\"app_name\""), file.path)
            }
    }

    @Test
    fun launcherShortcutsAndFileSharingUseTheInstalledVariant() {
        val buildFile = File(appDirectory, "build.gradle.kts").readText()
        val shortcuts = File(appDirectory, "src/main/res/xml/shortcuts.xml").readText()
        val manifest = File(appDirectory, "src/main/AndroidManifest.xml").readText()
        val targetPackages = Regex("android:targetPackage=\"([^\"]+)\"")
            .findAll(shortcuts).map { match -> match.groupValues[1] }.toList()

        assertTrue(targetPackages.isNotEmpty())
        assertTrue(targetPackages.all { target -> target == "@string/shortcut_target_package" })
        assertTrue(buildFile.contains("variant.makeResValueKey(\"string\", \"shortcut_target_package\")"))
        assertTrue(buildFile.contains("variant.applicationId.map"))
        assertTrue(shortcuts.contains("android:targetClass=\"com.android.purebilibili.MainActivity\""))
        assertTrue(manifest.contains("android:authorities=\"\${applicationId}.fileprovider\""))
    }
}
