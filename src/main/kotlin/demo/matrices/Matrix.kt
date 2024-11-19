package demo.matrices

import helpers.formatForExcel
import kotlin.random.Random

/**
 * Прямоугольная матрица даблов
 */
class Matrix(
    val rows: Int,
    val columns: Int,
) {
    private var data: DoubleArray = DoubleArray(rows * columns)

    override fun toString(): String {
        return (0 until rows)
            .joinToString(separator = "\n") { row ->
                (0 until columns)
                    .joinToString(separator = ", ") { column ->
                        get(row, column).formatForExcel()
                    }
            }
    }

    fun transposeNaive(): Matrix {
        val result = Matrix(columns, rows)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                result.set(i, j, get(j, i))
            }
        }
        return result
    }

    fun traverseRowsFirst(): Double {
        var sum = 0.0
        for (row in 0 until rows) {
            for (columns in 0 until columns) {
                sum += get(row, columns)
            }
        }
        return sum
    }

    fun traverseColumnsFirst(): Double {
        var sum = 0.0
        for (column in 0 until columns) {
            for (row in 0 until rows) {
                sum += get(row, column)
            }
        }
        return sum
    }

    fun transposeCacheOblivious(): Matrix {
        val result = Matrix(columns, rows)
        cacheObliviousTranspose(0, rows, 0, columns, result)
        return result
    }

    fun get(row: Int, column: Int): Double {
        return data[row * rows + column]
    }

    fun set(row: Int, column: Int, value: Double) {
        data[row * rows + column] = value
    }

    private fun cacheObliviousTranspose(rb: Int, re: Int, cb: Int, ce: Int, t: Matrix) {
        val r = re - rb
        val c = ce - cb
        // блок по горизонтали и по вертикали помещается в кэш-линию
        if (r <= BLOCK_SIZE && c <= BLOCK_SIZE) {
            for (i in rb until re) {
                for (j in cb until ce) {
                    t.data[j * rows + i] = data[i * columns + j]
                }
            }
        } else if (r >= c) {
            cacheObliviousTranspose(rb, rb + (r / 2), cb, ce, t)
            cacheObliviousTranspose(rb + (r / 2), re, cb, ce, t)
        } else {
            cacheObliviousTranspose(rb, re, cb, cb + (c / 2), t)
            cacheObliviousTranspose(rb, re, cb + (c / 2), ce, t)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (rows != other.rows) return false
        if (columns != other.columns) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + data.contentHashCode()
        return result
    }

    infix fun multiplyWithTransposition(
        matrix2: Matrix,
    ): Matrix {
        val matrix1 = this
        require(matrix1.columns == matrix2.rows) {
            "Не могу умножить матрицы. Ожидается, что количество строк в первой матрице" +
                    " равно количеству колонок во второй, а было:" +
                    " ${matrix1.rows}x${matrix1.columns} и ${matrix2.rows}x${matrix2.columns}"
        }

        val matrix2Transposed = matrix2.transposeCacheOblivious()
        return buildMatrix(rows = matrix1.rows, columns = matrix2.columns) { row, column ->
            var sum = 0.0
            for (i in 0 until matrix1.columns) {
                sum += matrix1.get(row = row, column = i) * matrix2Transposed.get(row = column, column = i)
            }
            sum
        }
    }

    infix fun multiply(
        matrix2: Matrix,
    ): Matrix {
        val matrix1 = this
        require(matrix1.columns == matrix2.rows) {
            "Не могу умножить матрицы. Ожидается, что количество строк в первой матрице" +
                    " равно количеству колонок во второй, а было:" +
                    " ${matrix1.rows}x${matrix1.columns} и ${matrix2.rows}x${matrix2.columns}"
        }

        return buildMatrix(rows = matrix1.rows, columns = matrix2.columns) { row, column ->
            var sum = 0.0
            for (i in 0 until matrix1.columns) {
                sum += matrix1.get(row = row, column = i) * matrix2.get(row = i, column = column)
            }
            sum
        }
    }

    companion object {

        /**
         * Размер кэш-линии в байтах
         */
        private const val CACHE_LINE_SIZE_BYTES = 64

        private const val BLOCK_SIZE = CACHE_LINE_SIZE_BYTES / Double.SIZE_BYTES

        /**
         * Построить матрицу
         */
        fun buildMatrix(
            rows: Int,
            columns: Int,
            calcElement: (row: Int, column: Int) -> Double,
        ): Matrix {
            return Matrix(rows, columns).apply {
                for (row in 0 until rows) {
                    for (column in 0 until columns) {
                        set(row, column, calcElement(row, column))
                    }
                }
            }
        }

        /**
         * Построить некоторую квадратную матрицу даблов
         * Содежимое матрицы - случайное, но одинаковое при повторном построении
         * для сверки результатов разных демо
         */
        fun generateRandomSquareMatrix(
            size: Int,
        ): Matrix {
            val random = Random(seed = 0)
            return buildMatrix(size, size) { _, _ ->
                random.nextDouble(from = 0.0, until = 1000.0)
            }
        }
    }
}
