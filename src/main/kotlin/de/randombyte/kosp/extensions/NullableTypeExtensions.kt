package de.randombyte.kosp.extensions

import java.util.*

fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)