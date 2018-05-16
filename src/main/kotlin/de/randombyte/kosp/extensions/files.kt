package de.randombyte.kosp.extensions

import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

fun Path.toConfigurationLoader() = HoconConfigurationLoader.builder().setPath(this).build()