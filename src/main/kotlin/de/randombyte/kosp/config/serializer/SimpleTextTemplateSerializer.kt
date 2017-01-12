package de.randombyte.kosp.config.serializer

import de.randombyte.kosp.config.serializer.SimpleTextTemplateTypeSerializer.toFullArgumentName
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.serializer.TextSerializers

object SimpleTextTemplateSerializer {
    fun serialize(textTemplate : TextTemplate, node: ConfigurationNode) {
        textTemplate.firstOptionalArgument()?.apply {
            throw ObjectMappingException("TextTemplate '${node.key}': Argument '$key' is optional!")
        }
        textTemplate.firstNonSingleWordArgument()?.apply {
            throw ObjectMappingException("TextTemplate '${node.key}': Argument '$key' contains a space!")
        }

        // value
        node.value = serializeTextTemplate(textTemplate)

        // comment
        if (node is CommentedConfigurationNode) {
            val (additionalArgs, realComment) = if (node.comment.isPresent) {
                // A comment was set by @Setting(comment = "...")
                val comment = node.comment.get()
                if (!commentNeedsProcessing(comment)) return
                val plainComment = comment.removePrefix(COMMENT_NEEDS_PROCESSING_PREFIX)
                parseExistingComment(plainComment)
            } else Pair(emptyList(), null)

            val allArgs = textTemplate.arguments.keys + additionalArgs
            val safeRealComment = if (realComment != null) "$realComment\n" else ""
            val comment = safeRealComment + "Available arguments: " + allArgs.joinToString()
            node.setComment(comment)
        }
    }

    /**
     * Serializes the [TextTemplate] but only its formatting and [ClickAction.RunCommand] and
     * [ClickAction.OpenUrl] clickactions in a Markdown style, e.g. '[Good weather](/weather clear)'
     * or '[Click to vote](https://vote.url)'.
     */
    private fun serializeTextTemplate(textTemplate : TextTemplate): String {
        val pseudoArguments = textTemplate.arguments.map { it.key to it.key.toFullArgumentName() }.toMap()
        val text = textTemplate.apply(pseudoArguments).build()

        val children = text.getOnlyChildren()

        val strings = children.map { child ->
            val contentWithFormatting = child.serializeToString()
            val finalText = if (child.clickAction.isPresent) {
                val clickAction = child.clickAction.get()
                if (clickAction is ClickAction.RunCommand) {
                    val command = clickAction.result
                    "[$contentWithFormatting]($command)"
                } else if (clickAction is ClickAction.SuggestCommand) {
                    val suggestedCommand = clickAction.result
                    "[$contentWithFormatting](&$suggestedCommand)"
                } else if (clickAction is ClickAction.OpenUrl) {
                    val urlString = clickAction.result.toExternalForm()
                    "[$contentWithFormatting]($urlString)"
                } else throw ObjectMappingException("This TextTemplate is unsupported: '$textTemplate'")
            } else contentWithFormatting

            return@map finalText
        }

        return strings.joinToString(separator = "")
    }

    private const val COMMENT_NEEDS_PROCESSING_PREFIX = "%"
    private fun commentNeedsProcessing(comment : String) = comment.startsWith(COMMENT_NEEDS_PROCESSING_PREFIX)

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

    private fun TextTemplate.firstOptionalArgument() = arguments.entries.firstOrNull { it.value.isOptional }
    private fun TextTemplate.firstNonSingleWordArgument() = arguments.entries.firstOrNull { !it.key.isOneWord() }
    private fun String.isOneWord() = !contains(" ")

    private fun Text.serializeToString() = TextSerializers.FORMATTING_CODE.serialize(this)

    private fun Text.getOnlyChildren(): List<Text> {
        return if (children.isEmpty()) listOf(this) else {
            val thisTextWithoutChildren = toBuilder().removeAll().build()
            val childrenOfThisText = children.map { it.getOnlyChildren() }.flatten()
            listOf(thisTextWithoutChildren) + childrenOfThisText
        }
    }
}