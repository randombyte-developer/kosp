package de.randombyte.kosp.bstats.charts

interface ValueProvider<V> {
    fun getValue(): V
}