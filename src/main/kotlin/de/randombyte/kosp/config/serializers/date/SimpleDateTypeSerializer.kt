package de.randombyte.kosp.config.serializers.date

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SimpleDateTypeSerializer : TypeSerializer<Date> {

    private val dateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Date {
        val string = value.string
        try {
            return dateFormat.parse(string)
        } catch (exception: ParseException) {
            throw ObjectMappingException("Invalid input value '$string' for a date like this: '21:18:25 28.03.2017'", exception)
        }

    }

    override fun serialize(type: TypeToken<*>, date: Date, value: ConfigurationNode) {
        value.value = dateFormat.format(date)
    }
}