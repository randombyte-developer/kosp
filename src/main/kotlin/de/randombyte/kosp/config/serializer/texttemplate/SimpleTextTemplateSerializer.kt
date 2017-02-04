package de.randombyte.kosp.config.serializer.texttemplate

import de.randombyte.kosp.config.serializer.text.SimpleTextSerializer
import de.randombyte.kosp.config.serializer.texttemplate.SimpleTextTemplateTypeSerializer.COMMENT_NEEDS_PROCESSING_PREFIX
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import org.spongepowered.api.text.TextTemplate

object SimpleTextTemplateSerializer {
    internal fun serialize(textTemplate : TextTemplate, node: ConfigurationNode) {
        checkTextTemplate(textTemplate, node)

        // value
        node.value = serializeTextTemplate(textTemplate)

        // comment
        if (node is CommentedConfigurationNode) {
            val (additionalArgs, realComment) = if (node.comment.isPresent) {
                // A comment was set by @Setting(comment = "...")
                val comment = node.comment.get()

                val doesCommentNeedProcessing = comment.startsWith(COMMENT_NEEDS_PROCESSING_PREFIX)
                if (!doesCommentNeedProcessing) return // Already processed comments get kicked out here
                val plainComment = comment.removePrefix(COMMENT_NEEDS_PROCESSING_PREFIX)

                parseExistingComment(plainComment)
            } else Pair(emptyList(), null)

            val allArgs = textTemplate.arguments.keys + additionalArgs
            val safeRealComment = if (realComment != null) "$realComment\n" else ""
            // 'Available parameters' is used instead of the wrong term 'arguments' to avoid confusing the user
            val comment = safeRealComment + "Available parameters: " + allArgs.joinToString()
            node.setComment(comment)
        }
    }

    private fun serializeTextTemplate(textTemplate : TextTemplate): String {
        val pseudoArguments = textTemplate.arguments.map { it.key to it.key.toFullArgumentName() }.toMap()
        val text = textTemplate.apply(pseudoArguments).build()
        return SimpleTextSerializer.serialize(text)
    }

    private fun String.toFullArgumentName() = TextTemplate.DEFAULT_OPEN_ARG + this + TextTemplate.DEFAULT_CLOSE_ARG

    /**
     * Parses an already existing comment. The format is as follows:
     * `"<comma separated list of additional args>;<real comment>"`
     * Example: `"currencyName,currencySymbol;Message that appears when doing..."`
     *
     * @return list of additional args and the real comment
     */
    private fun parseExistingComment(comment: String): Pair<List<String>, String?> {
        val semicolonSplits = comment.split(";", limit = 2)
        if (semicolonSplits.size != 2) throw IllegalArgumentException("Comment not in the correct format for processing!")

        val additionalArgsString = semicolonSplits[0]
        val realComment = if (semicolonSplits[1].isEmpty()) null else semicolonSplits[1]

        val additionalArgs = additionalArgsString.split(",").filter(String::isNotEmpty)
        return Pair(additionalArgs, realComment)
    }

    private fun checkTextTemplate(textTemplate : TextTemplate, node : ConfigurationNode) {
        textTemplate.firstOptionalArgument()?.apply {
            throw ObjectMappingException("TextTemplate '${node.key}': Argument '$key' is optional!")
        }
        textTemplate.firstNonSingleWordArgument()?.apply {
            throw ObjectMappingException("TextTemplate '${node.key}': Argument '$key' contains a space!")
        }
    }

    private fun TextTemplate.firstOptionalArgument() = arguments.entries.firstOrNull { it.value.isOptional }
    private fun TextTemplate.firstNonSingleWordArgument() = arguments.entries.firstOrNull { !it.key.isOneWord() }
    private fun String.isOneWord() = !contains(" ")
}