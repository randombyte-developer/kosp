package de.randombyte.kosp

import java.util.Optional

/**
 * Converts the Java [Optional] into the Kotlin equivalent.
 */
fun <T> Optional<T>.value(): T? = orElse(null)