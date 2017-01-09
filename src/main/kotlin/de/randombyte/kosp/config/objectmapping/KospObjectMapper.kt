package de.randombyte.kosp.config.objectmapping

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting
import java.lang.reflect.Field

open class KospObjectMapper<T>(clazz : Class<T>) : ObjectMapper<T>(clazz) {
    /**
     * Overrides the super method without calling it, so it is completely replaced. In general,
     * this method does the same as the super one, except it calls the abstract method [getFieldData]
     * to get the data to be inserted in the [cachedFields] map. This makes it possible for classes
     * implementing this one to manipulate the path and the [ObjectMapper.FieldData] of a node.
     */
    override fun collectFields(cachedFields: MutableMap<String, FieldData>, clazz: Class<in T>) {
        clazz.declaredFields
                .filter { it.isAnnotationPresent(Setting::class.java) }
                .forEach { field ->
                    val setting = field.getAnnotation(Setting::class.java)
                    val (path, fieldData) = getFieldData(field, setting)
                    field.isAccessible = true // Don't know why, the super method does the same
                    if (!cachedFields.containsKey(path)) cachedFields.put(path, fieldData)
                }
    }

    /**
     * The [field] is guaranteed to be annotated with [setting]. This method is an excerpt of the
     * super class in the [ObjectMapper.collectFields] method. It is open to allow overriding
     * the behaviour.
     *
     * @return The path and [ObjectMapper.FieldData] of the node.
     */
    open protected fun getFieldData(field : Field, setting : Setting): Pair<String, FixedFieldData> {
        val path = if (setting.value.isEmpty()) {
            field.name
        } else setting.value
        val data = FixedFieldData(field, setting.comment)

        return Pair(path, data)
    }

    /**
     * Fixed: First set comment, then serialize(by calling super) -> allows TypeSerializer
     * to process/react to a comment.
     */
    protected class FixedFieldData(field : Field, val comment: String?) : FieldData(field, comment) {
        override fun serializeTo(instance: Any, node: ConfigurationNode) {
            if (node is CommentedConfigurationNode && comment != null && comment.isNotEmpty()) {
                if (!node.comment.isPresent) node.setComment(this.comment)
            }
            super.serializeTo(instance, node)
        }
    }
}