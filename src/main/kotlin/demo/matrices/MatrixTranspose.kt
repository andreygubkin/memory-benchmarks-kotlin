package demo.matrices

import demo.IDemo
import kotlin.system.measureTimeMillis

object MatrixTranspose : IDemo {
    override val title = "Транспонирование матрицы"

    override suspend fun execute() {
        fun warmUp() {
            benchmarkMatrixTranspose(
                benchmarkIterations = 1,
                matrixSize = 64,
                verbose = false,
            )
        }

        warmUp()

        benchmarkMatrixTranspose(
            benchmarkIterations = 100,
            matrixSize = 4096,
            verbose = true,
        )
    }

    private fun benchmarkMatrixTranspose(
        benchmarkIterations: Int,
        matrixSize: Int,
        verbose: Boolean,
    ) {

        val matrix = Matrix.generateRandomSquareMatrix(size = matrixSize)
        check(matrix.transposeNaive() == matrix.transposeCacheOblivious()) {
            "Неравные матрицы в двух методах"
        }
        if (verbose) {
            println("Способ транспонирования\tms")
        }
        measureTimeMillis {
            repeat(times = benchmarkIterations) {
                matrix.transposeNaive()
            }
        }.also {
            if (verbose) {
                println("Наивное транспонирование\t$it")
            }
        }
        measureTimeMillis {
            repeat(times = benchmarkIterations) {
                matrix.transposeCacheOblivious()
            }
        }.also {
            if (verbose) {
                println("Cache-oblivious транспонирование\t$it")
            }
        }
    }
}
