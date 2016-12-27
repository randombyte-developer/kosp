package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject
import org.slf4j.Logger


abstract class CustomChart<V>(protected val chartId: String) : ValueProvider<V> {
    fun getRequestedJsonObject(logger: Logger, logFailedRequests: Boolean): JsonObject? {
        val data = getChartData() ?: return null
        return JsonObject().apply {
            addProperty("chartId", chartId)
            add("data", data)
        }
    }

    protected abstract fun getChartData(): JsonObject?
}