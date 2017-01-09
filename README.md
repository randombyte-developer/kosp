# kosp
A Sponge plugin-lib: [Forum](https://forums.spongepowered.org/t/kosp-helpful-plugin-library-for-kotlin/16678)

Usage:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile "com.github.randombyte-developer.kosp:kosp:v0.3.6"
}
```

Note: The kotlin runtime and stdlib is not shaded into Kosp to reduce the .jar size. The plugin using Kosp is likely to be written in Kotlin and because of that shading Kotlin libs itself. See [build.gradle](https://github.com/randombyte-developer/kosp/blob/master/build.gradle).
