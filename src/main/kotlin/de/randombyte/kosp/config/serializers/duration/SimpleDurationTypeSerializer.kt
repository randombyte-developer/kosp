package de.randombyte.kosp.config.serializers.duration

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.time.Duration

/**
 * A simple format for defining [Duration]s.
 *
 * 'd' -> days
 * 'h' -> hours
 * 'm' -> minutes
 * 's' -> seconds
 *
 * Examples: '1d4h30m20s', '30s', '2h', '12m3s'
 */
object SimpleDurationTypeSerializer : TypeSerializer<Duration> {
    private const val MINUTE = 60
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR

    val REGEX = "(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?".toRegex()

    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode): Duration {
        val string = node.string
        val result = REGEX.matchEntire(string) ?: throw RuntimeException("Couldn't parse duration '$string'!")

        val days = result.groupValues[1].toLongOrZero()
        val hours = result.groupValues[2].toLongOrZero()
        val minutes = result.groupValues[3].toLongOrZero()
        val seconds = result.groupValues[4].toLongOrZero()

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds)
    }

    override fun serialize(type: TypeToken<*>, duration: Duration, node: ConfigurationNode) {
        val days = duration.seconds / DAY
        val hours = duration.seconds % DAY / HOUR
        val minutes = duration.seconds % DAY % HOUR / MINUTE
        val seconds = duration.seconds % DAY % HOUR % MINUTE

        val sb = StringBuilder()
        days.apply { if (this != 0L) sb.append(this).append("d") }
        hours.apply { if (this != 0L) sb.append(this).append("h") }
        minutes.apply { if (this != 0L) sb.append(this).append("m") }
        seconds.apply { if (this != 0L) sb.append(this).append("s") }

        node.value = sb.toString()
    }

    private fun String.toLongOrZero() = if (isEmpty()) 0 else toLong()
}