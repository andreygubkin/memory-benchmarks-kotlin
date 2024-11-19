package demo.matrices

import demo.IDemo
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object MatrixTraverse : IDemo {
    override val title = "Обход матрицы"

    override suspend fun execute() {
        fun warmUp() {
            benchmarkMatrixTraverse(
                benchmarkIterations = 1,
                matrixSize = 64,
                verbose = false,
            )
        }

        warmUp()

        benchmarkMatrixTraverse(
            benchmarkIterations = 100,
            matrixSize = 4096,
            verbose = true,
        )
    }

    private fun benchmarkMatrixTraverse(
        benchmarkIterations: Int,
        matrixSize: Int,
        verbose: Boolean,
    ) {
        val matrix = Matrix.generateRandomSquareMatrix(size = matrixSize)
        val sumRowsFirst = matrix.traverseRowsFirst()
        val sumColumnsFirst = matrix.traverseColumnsFirst()
        val sumsDelta = abs(sumRowsFirst - sumColumnsFirst)
        check(sumsDelta < 1e-2) {
            "Неравные суммы в двух методах: $sumRowsFirst и $sumColumnsFirst, delta = $sumsDelta"
        }

        if (verbose) {
            println("Направление обхода\tms")
        }

        measureTimeMillis {
            repeat(times = benchmarkIterations) {
                matrix.traverseColumnsFirst()
            }
        }.also {
            if (verbose) {
                println("Сначала по колонкам\t$it")
            }
        }

        measureTimeMillis {
            repeat(times = benchmarkIterations) {
                matrix.traverseRowsFirst()
            }
        }.also {
            if (verbose) {
                println("Сначала по строкам\t$it")
            }
        }
    }
}
