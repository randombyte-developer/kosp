package de.randombyte.kosp.extensions

import com.flowpowered.math.vector.Vector3i

operator fun Vector3i.rangeTo(that: Vector3i) = Vector3iRange(this, that)

/**
 * Iterates over all positions between [start] and [endInclusive].
 */
class Vector3iRange(override val start: Vector3i, override val endInclusive: Vector3i) : ClosedRange<Vector3i>, Iterable<Vector3i> {

    override fun contains(value: Vector3i) = (start.x..endInclusive.x).contains(value.x) &&
            (start.y..endInclusive.y).contains(value.y) &&
            (start.z..endInclusive.z).contains(value.z)

    override fun iterator(): Iterator<Vector3i> = Vector3iProgressionIterator(start, endInclusive)

    internal class Vector3iProgressionIterator(val start: Vector3i, private val end: Vector3i): Iterator<Vector3i> {
        private var next = start

        override fun hasNext() = next.x <= end.x && next.y <= end.y && next.z <= end.z

        override fun next(): Vector3i {
            val value = next
            if (hasNext()) {
                next = next.add(1, 0, 0)
                if (next.x > end.x) next = Vector3i(start.x, next.y + 1, next.z)
                if (next.y > end.y) next = Vector3i(next.x, start.y, next.z + 1)
            }
            return value
        }
    }
}

fun Vector3i.copy(newX: Int = x, newY: Int = y, newZ: Int = z) = Vector3i(newX, newY, newZ)