plugins {
    java
    maven
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("net.minecrell.plugin-yml.nukkit") version "0.3.0"
}

group = "net.ree_jp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(
        url = uri("https://repo.nukkitx.com/main/")
    )
}

dependencies {
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
    testCompileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

nukkit {
    name = "FloatingChat"
    main = "net.ree_jp.floatingchat.FloatingChatPlugin"
    api = listOf("1.0.0")
    author = "Ree-jp"
    description = "FloatingChatPlugin"
    website = "https://github.com/Ree-jp-minecraft/FloatingChat"
    version = "1.0.0"


    permissions.register("floatingChat.*")

    permissions {
        "floatingChat.*" {
            description = "FloatingChat permission"
            default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.TRUE
            children {
                "floatingChat.show" {
                    description = "FloatingChat show permission"
                    default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.TRUE
                }
            }
        }
    }
}