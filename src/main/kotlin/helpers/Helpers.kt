@file:Suppress("unused")

package helpers

import java.util.*

private const val VOLUME_1GB = 1L shl 30
private const val VOLUME_1MB = 1L shl 20
private const val VOLUME_1KB = 1L shl 10

internal fun Long.toHumanReadableVolume(): String {
    val number = this
    return when {
        number >= 10 * VOLUME_1GB -> {
            "${(this / VOLUME_1GB.toDouble()).formatForExcel(digits = 0)}GB"
        }
        number >= VOLUME_1GB -> {
            "${(this / VOLUME_1GB.toDouble()).formatForExcel(digits = 1)}GB"
        }
        number >= 10 * VOLUME_1MB -> {
            "${(this / VOLUME_1MB.toDouble()).formatForExcel(digits = 0)}MB"
        }
        number >= VOLUME_1MB -> {
            "${(this / VOLUME_1MB.toDouble()).formatForExcel(digits = 1)}MB"
        }
        number >= 10 * VOLUME_1KB -> {
            "${(this / VOLUME_1KB.toDouble()).formatForExcel(digits = 0)}KB"
        }
        number >= VOLUME_1KB -> {
            "${(this / VOLUME_1KB.toDouble()).formatForExcel(digits = 1)}KB"
        }
        else -> "${number}B"
    }
}

internal fun Int.toHumanReadableNumber(): String {
    return this.toLong().toHumanReadableNumber()
}

internal fun Long.toHumanReadableNumber(): String {
    val number = this
    return when {
        number >= 10_000_000_000L -> {
            "${(this / 1_000_000_000.0).formatForExcel(digits = 0)}B"
        }
        number >= 1_000_000_000L -> {
            "${(this / 1_000_000_000.0).formatForExcel(digits = 1)}B"
        }
        number >= 10_000_000 -> {
            "${(this / 1_000_000.0).formatForExcel(digits = 0)}M"
        }
        number >= 1_000_000 -> {
            "${(this / 1_000_000.0).formatForExcel(digits = 1)}M"
        }
        number >= 10_000 -> {
            "${(this / 1_000.0).formatForExcel(digits = 0)}K"
        }
        number >= 1_000 -> {
            "${(this / 1_000.0).formatForExcel(digits = 1)}K"
        }
        else -> number.toString()
    }
}

internal fun IntArray.usedMemory(): String {
    return (size * Int.SIZE_BYTES).toLong().toHumanReadableVolume()
}

internal fun Double.formatForExcel(digits: Int = 2): String {
    return String.format(currentLocale, "%.${digits}f", this)
}

@Suppress("unused")
val ru: Locale = Locale.of("ru")
@Suppress("unused")
val en: Locale = Locale.of("en")

val currentLocale = en
//val currentLocale = ru

fun IntRange.length(): Int {
    require(last >= first) {
        "Не поддерживаются обратные диапазоны: $this"
    }
    return last - first + 1
}

fun LongRange.length(): Long {
    require(last >= first) {
        "Не поддерживаются обратные диапазоны: $this"
    }
    return last - first + 1
}

inline fun repeat(times: Long, action: (Long) -> Unit) {
    for (index in 0L until times) {
        action(index)
    }
}

/**
 * Разбить на чанки близкого размера - для равномерного распределения работ по потокам
 */
fun LongRange.uniformlyChunked(
    chunksCount: Int,
): List<LongRange> {

    val range = this

    require(chunksCount >= 1) {
        "`chunksCount` должен быть >= 1, а было: $chunksCount"
    }

    val itemsCount = range.length()

    check(chunksCount <= itemsCount) {
        "Количество чанков не должно быть больше " +
                "количества элементов $itemsCount, а было: $chunksCount"
    }

    val smallChunkSize = itemsCount / chunksCount
    val bigChunkSize = smallChunkSize + 1 // гарантия близости размеров чанков

    val initialMinChunksCount = chunksCount
    val bigChunksCount = itemsCount - initialMinChunksCount * smallChunkSize
    val smallChunksCount = (itemsCount - (bigChunksCount * bigChunkSize)) / smallChunkSize

    /*
        Алгоритм:
            - если набирать только маленькими чанками, то не хватит N = bigChunksCount элементов
            - поэтому первые N маленьких чанков заменяем на большие чанки
     */

    val chunksSizes = List(bigChunksCount.toInt()) { bigChunkSize } +
            List(smallChunksCount.toInt()) { smallChunkSize }

    val chunksFirstIndices = chunksSizes
        .runningFold(range.first) { acc, current ->
            acc + current
        } // включая последний индекс = range.last + 1


    return chunksFirstIndices
        .zipWithNext()
        .map { (first, lastExclusive) ->
            first until lastExclusive
        }
        .toList()
}
