package de.randombyte.kosp.extensions

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate

fun TextTemplate.apply(vararg args: Pair<String, Any>): Text = apply(args.toMap()).build()

fun List<TextTemplate>.apply(vararg args: Pair<String, Any>): List<Text> = map { it.apply(*args) }