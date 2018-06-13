package de.randombyte.kosp.config.serializers.date

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SimpleDateTypeSerializer : TypeSerializer<Date> {

    private val legacyDateFormat = SimpleDateFormat("HH:mm:ss.SSS dd.MM.yyyy")
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS-dd.MM.yyyy")

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Date = deserialize(value.string)

    override fun serialize(type: TypeToken<*>, date: Date, value: ConfigurationNode) {
        value.value = serialize(date)
    }

    fun deserialize(string: String): Date {
        try {
            return dateFormat.parse(string)
        } catch (exception: ParseException) {
            try {
                return deserializeLegacy(string)
            } catch (exception: ParseException) {
                // Will be handled further down
            }

            throw ObjectMappingException("Invalid input value '$string' for a date like this: '21:18:25.300-28.03.2017'", exception)
        }
    }

    fun serialize(date: Date): String = dateFormat.format(date)

    private fun deserializeLegacy(string: String) = legacyDateFormat.parse(string)
}