plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

tasks.named<Wrapper>("wrapper") {
    // Define wrapper values here so as to not have to always do so when updating gradlew.properties.
    // Switching this to Wrapper.DistributionType.ALL will download the full gradle sources that comes with
    // documentation attached on cursor hover of gradle classes and methods. However, this comes with increased
    // file size for Gradle. If you do switch this to ALL, run the Gradle wrapper task twice afterwards.
    // (Verify by checking gradle/wrapper/gradle-wrapper.properties to see if distributionUrl now points to `-all`)
    distributionType = Wrapper.DistributionType.BIN
}

version = project.property("mod_version") as String
group = project.property("mod_group_id") as String

repositories {
    /* CC: Tweaked */
    maven {
        url = uri("https://maven.squiddev.cc")
        content {
            includeGroup("cc.tweaked")
        }
    }

    /* Modrinth */
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
//        forRepositories(fg.repository) // Only add this if you're using ForgeGradle, otherwise remove this line
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    /* CC: Tweaked */
    compileOnly("cc.tweaked:cc-tweaked-${property("minecraft_version")}-forge-api:${property("cct_version")}")
    runtimeOnly("cc.tweaked:cc-tweaked-${property("minecraft_version")}-forge:${property("cct_version")}")

    /* Starcatcher */
    implementation("maven.modrinth:starcatcher:${property("starcatcher_version")}-NEOFORGE-${property("minecraft_version")}")
    //implementation(files(".lib/starcatcher-2.3-PREVIEW-3-NEOFORGE-1.21.1.jar"))
}


base {
    archivesName = project.property("mod_id") as String
}

// Mojang ships Java 21 to end users in 1.21.1, so mods should target Java 21.
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    // Specify the version of NeoForge to use.
    version = project.property("neo_version") as String

    parchment {
        mappingsVersion = project.property("parchment_mappings_version") as String
        minecraftVersion = project.property("parchment_minecraft_version") as String
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers = project.files("src/main/resources/META-INF/accesstransformer.cfg")

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        val client by creating {
            client()

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
        }

        val server by creating {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        val gameTestServer by creating {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
        }

        val data by creating {
            data()

            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file("run-data")

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                "--mod", project.property("mod_id") as String,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        // applies to all the run configs above
        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // multi mod projects should define one per mod
        create(project.property("mod_id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Include resources generated by data generators.
sourceSets.main {
    resources.srcDir("src/generated/resources")
}

// Sets up a dependency configuration called 'localRuntime'.
// This configuration should be used instead of 'runtimeOnly' to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
val localRuntime by configurations.creating
configurations.runtimeClasspath {
    extendsFrom(localRuntime)
}


// This block of code expands all declared replace properties in the specified resource targets.
// A missing property will result in an error. Properties are expanded using ${} Groovy notation.
val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version"       to project.property("minecraft_version"),
        "minecraft_version_range" to project.property("minecraft_version_range"),
        "neo_version"             to project.property("neo_version"),
        "loader_version_range"    to project.property("loader_version_range"),
        "mod_id"                  to project.property("mod_id"),
        "mod_name"                to project.property("mod_name"),
        "mod_license"             to project.property("mod_license"),
        "mod_version"             to project.property("mod_version"),

        "starcatcher_version"     to project.property("starcatcher_version"),
        "cct_version"             to project.property("cct_version")
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}
// Include the output of "generateModMetadata" as an input directory for the build
// this works with both building through Gradle and the IDE.
sourceSets.main {
    resources.srcDir(generateModMetadata)
}
// To avoid having to run "generateModMetadata" manually, make it run on every project reload
neoForge.ideSyncTask(generateModMetadata)

// Example configuration to allow publishing using the maven-publish plugin
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file:///${project.projectDir.toURI()}/repo")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}