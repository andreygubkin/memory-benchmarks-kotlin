package helpers

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.matchers.shouldBe
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class HelpersKtTest : StringSpec({
    "Преобразование чисел в человеко-читаемый формат" {
        12_000_000_000L.toHumanReadableNumber() shouldBe "12B"
        1_200_000_000L.toHumanReadableNumber() shouldBe "1.2B"

        13_000_000L.toHumanReadableNumber() shouldBe "13M"
        1_300_000L.toHumanReadableNumber() shouldBe "1.3M"

        14_000L.toHumanReadableNumber() shouldBe "14K"
        1_400L.toHumanReadableNumber() shouldBe "1.4K"

        997L.toHumanReadableNumber() shouldBe "997"
        10L.toHumanReadableNumber() shouldBe "10"
    }

    "Преобразование объёмов в человеко-читаемый формат" {

        (12 * (1L shl 30)).toHumanReadableVolume() shouldBe "12GB"
        (1.2 * (1L shl 30)).toLong().toHumanReadableVolume() shouldBe "1.2GB"

        (13 * (1L shl 20)).toHumanReadableVolume() shouldBe "13MB"
        (1.3 * (1L shl 20)).toLong().toHumanReadableVolume() shouldBe "1.3MB"

        (14 * (1L shl 10)).toHumanReadableVolume() shouldBe "14KB"
        (1.4 * (1L shl 10)).toLong().toHumanReadableVolume() shouldBe "1.4KB"

        997L.toHumanReadableVolume() shouldBe "997B"
        10L.toHumanReadableVolume() shouldBe "10B"
    }

    "Преобразование чисел в строку (для английского Excel)" {

        currentLocale shouldBe en

        1.23.formatForExcel() shouldBe "1.23"
        1.5.formatForExcel() shouldBe "1.50"
        1.0.formatForExcel() shouldBe "1.00"
    }

    "!Преобразование чисел в строку (для русского Excel)" {

        currentLocale shouldBe ru

        1.23.formatForExcel() shouldBe "1,23"
        1.5.formatForExcel() shouldBe "1,50"
        1.0.formatForExcel() shouldBe "1,00"
    }

    "Вычисление длины диапазона" {
        (1..1).length() shouldBe 1
        (1..2).length() shouldBe 2
        (10..20).length() shouldBe 11

        (1L..1L).length() shouldBe 1L
        (1L..2L).length() shouldBe 2L
        (1L..10_000_000_000L).length() shouldBe 10_000_000_000L
    }

    "Разбивает диапазоны на равномерные чанки" {

        fun test(
            range: LongRange,
            chunksCount: Int,
        ) {
            val chunks = range.uniformlyChunked(chunksCount)
            withClue("$range / $chunksCount -> $chunks") {
                chunks.size shouldBe chunksCount

                chunks.forEach {
                    it.first shouldBeLessThanOrEqual it.last
                }

                chunks.first().first shouldBe range.first
                chunks.last().last shouldBe range.last

                chunks.zipWithNext().forEach { (range, nextRange) ->
                    range.last shouldBe nextRange.first - 1
                }

                val uniqueChunkSizes = chunks
                    .map {
                        it.length()
                    }
                    .toHashSet()
                uniqueChunkSizes.size.shouldBeIn(1..2)
                if (uniqueChunkSizes.size == 2) {
                    uniqueChunkSizes.toList().let {
                        abs(it[0] - it[1])
                    } shouldBe 1
                }
            }
        }

        kotlin.repeat(100) {
            val x = Random.nextLong(from = -1_000_000, until = 1_000_000)
            val y = Random.nextLong(from = -1_000_000, until = 1_000_000)
            val rangeFirst = min(x, y)
            val rangeLast = max(x, y)

            val size = rangeLast - rangeFirst + 1
            val chunkCount = Random.nextInt(from = 1, until = min(20, size.toInt()))

            test(rangeFirst..rangeLast, chunkCount)
        }
    }
})
