package de.randombyte.kosp.config

import java.nio.file.Files
import java.nio.file.Path

/**
 * To be overwritten to have fields for various [ConfigHolder]s.
 */
abstract class ConfigAccessor(configPath: Path) {

    init {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath)
        }
    }

    abstract val holders: List<ConfigHolder<*>>

    fun reloadAll() = holders.forEach { it.reload() }
}