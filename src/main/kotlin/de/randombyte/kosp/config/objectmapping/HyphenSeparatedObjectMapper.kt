package de.randombyte.kosp.config.objectmapping

import com.google.common.base.CaseFormat
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
class HyphenSeparatedObjectMapper<T>(clazz: Class<T>) : KospObjectMapper<T>(clazz) {
    companion object {
        val EXCEPTIONS = mapOf("UUID" to "Uuid")

        /**
         * Corrects exceptions like "entityUUID" with "entityUuid", so it later becomes "entity-uuid"
         */
        private fun fixFieldNameCaps(name: String): String {
            var newName = name
            for ((incorrectName, correctName) in EXCEPTIONS) {
                newName = newName.replace(incorrectName, correctName)
            }
            return newName
        }
    }

    /**
     * Overrides the super method without calling it. This method does the conversion of
     * [CaseFormat.LOWER_CAMEL] to [CaseFormat.LOWER_HYPHEN].
     */
    override fun getFieldData(field: Field, setting: Setting): Pair<String, FixedFieldData> {
        val path = if (setting.value.isEmpty()) {
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, fixFieldNameCaps(field.name))
        } else setting.value
        val data = FixedFieldData(field, setting.comment)

        return Pair(path, data)
    }
}