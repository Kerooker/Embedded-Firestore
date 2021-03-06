import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "0.9.17"
    id("io.gitlab.arturbosch.detekt").version("1.1.1")
}

group = "top.colman.embeddedfirestore"
version = "0.1.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))


    // Cloud Firestore
    implementation("com.google.firebase:firebase-admin:6.11.0")
    
    // Bytebuddy
    implementation("net.bytebuddy:byte-buddy-dep:1.10.3")
    
    // Objenesis
    implementation("org.objenesis:objenesis:3.1")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.0.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.0.1")
    
    // Mockk
    testImplementation("io.mockk:mockk:1.9.3")
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    val javadoc = tasks["dokka"] as DokkaTask
    javadoc.outputFormat = "javadoc"
    javadoc.outputDirectory = "$buildDir/javadoc"
    dependsOn(javadoc)
    classifier = "javadoc"
    from(javadoc.outputDirectory)
}

publishing {
    repositories {
        
        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
            credentials {
                username = System.getProperty("OSSRH_USERNAME")
                password = System.getProperty("OSSRH_PASSWORD")
            }
        }
    }
    
    publications {
        
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            
            pom {
                name.set("EmbeddedFirestore")
                description.set("Embedded Firestore")
                url.set("https://github.com/Kerooker/Embedded-Firestore")
                
                
                scm {
                    connection.set("scm:git:http://www.github.com/Kerooker/Embedded-Firestore/")
                    developerConnection.set("scm:git:http://github.com/Kerooker/")
                    url.set("https://www.github.com/Kerooker/Embedded-Firestore")
                }
                
                licenses {
                    license {
                        name.set("The Apache 2.0 License")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                
                developers {
                    developer {
                        id.set("Kerooker")
                        name.set("Leonardo Colman Lopes")
                        email.set("leonardo@colman.top")
                    }
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}
