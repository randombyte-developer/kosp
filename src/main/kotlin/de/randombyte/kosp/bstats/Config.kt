package de.randombyte.kosp.bstats

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
class Config(
        @Setting(comment = ENABLED_COMMENT) val enabled: Boolean = true,
        @Setting val serverUuid: UUID = UUID.randomUUID(),
        @Setting val logFailedRequests: Boolean = true) {
    companion object {
        const private val ENABLED_COMMENT =
                "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                        "To honor their work, you should not disable it.\n" +
                        "This has nearly no effect on the server performance!\n" +
                        "Check out https://bStats.org/ to learn more :)"
    }
}
