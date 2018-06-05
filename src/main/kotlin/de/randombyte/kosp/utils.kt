package de.randombyte.kosp

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate

/**
 * Use this instead of TextTemplate.of(...) to workaround [this issue](https://github.com/SpongePowered/SpongeCommon/issues/1152).
 */
fun fixedTextTemplateOf(vararg elements: Any): TextTemplate = TextTemplate.of(Text.EMPTY, *elements)