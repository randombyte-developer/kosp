package de.randombyte.kosp.extensions

import java.util.Optional

/**
 * Converts the Java [Optional] into the Kotlin equivalent.
 */
fun <T> Optional<T>.orNull(): T? = orElse(null)