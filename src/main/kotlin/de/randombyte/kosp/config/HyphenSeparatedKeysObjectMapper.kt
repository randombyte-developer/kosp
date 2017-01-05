package de.randombyte.kosp.config

import com.google.common.base.CaseFormat
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting
import java.lang.reflect.Field

/**
 * Sets the path of a [Setting] to its lowercase-hyphen-separated target field name.
 *
 * Example, both lines of code produce the same config:
 * ```kotlin
 * @Setting("test-number") val testNumber: Int = 42
 * @Setting val testNumber: Int = 42
 * ```
 */
class HyphenSeparatedKeysObjectMapper<T>(clazz: Class<T>) : ObjectMapper<T>(clazz) {

    companion object {
        val EXCEPTIONS = mapOf("UUID" to "Uuid")

        /**
         * For example corrects exceptions like "entityUUID" with "entityUuid", so it later becomes "entity-uuid"
         */
        private fun fixFieldNameCaps(name: String): String {
            var newName = name
            for ((incorrectName, correctName) in EXCEPTIONS) {
                newName = newName.replace(incorrectName, correctName)
            }
            return newName
        }
    }

    override fun collectFields(cachedFields: MutableMap<String, FieldData>, clazz: Class<in T>) {
        clazz.declaredFields
                .filter { it.isAnnotationPresent(Setting::class.java) }
                .forEach { field ->
                    val setting = field.getAnnotation(Setting::class.java)

                    val path = if (setting.value.isEmpty()) {
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, fixFieldNameCaps(field.name))
                    } else setting.value

                    val data = FixedFieldData(field, setting.comment)
                    field.isAccessible = true
                    if (!cachedFields.containsKey(path)) cachedFields.put(path, data)
                }
    }

    /**
     * Fix/Patch:
     * First set comment, then serialize(by calling super) -> allows TypeSerializer to react to/process comment
     */
    private class FixedFieldData(field : Field, val comment: String?) : ObjectMapper.FieldData(field, comment) {
        override fun serializeTo(instance: Any, node: ConfigurationNode) {
            if (node is CommentedConfigurationNode && comment != null && comment.isNotEmpty()) {
                if (!node.comment.isPresent) node.setComment(this.comment)
            }
            super.serializeTo(instance, node)
        }
    }
}