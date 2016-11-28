package de.randombyte.kosp

import java.util.*

object OptionalExtensions {
    /**
     * Converts the Java [Optional] into the Kotlin equivalent.
     */
    fun <T> Optional<T>.value(): T? = orElse(null)
}