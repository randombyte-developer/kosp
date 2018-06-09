package de.randombyte.kosp.extensions

import java.util.*

fun <T> List<T>.getRandomElement(random: Random): T = get(random.nextInt(size))