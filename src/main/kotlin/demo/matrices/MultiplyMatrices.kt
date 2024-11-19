package demo.matrices

import demo.IDemo
import kotlin.system.measureTimeMillis

object MultiplyMatrices : IDemo {
    override val title = "Классическое умножение матриц"

    override suspend fun execute() {
        fun measureMatrixMultiplyMs(
            matrixSize: Int,
        ): Long {
            val matrix1 = Matrix.generateRandomSquareMatrix(size = matrixSize)
            val matrix2 = Matrix.generateRandomSquareMatrix(size = matrixSize)

            val ms = measureTimeMillis {
                matrix1 multiply matrix2
            }

            return  ms
        }

        println("Размер матрицы\tms")
        DEMO_MATRIX_SIZES
            .forEach { matrixSize ->
                val ms = measureMatrixMultiplyMs(matrixSize)
                println("$matrixSize\t$ms")
            }
    }

    val DEMO_MATRIX_SIZES = listOf(
        1000,
        1200,
        1400,
        1600,
        1800,
        2000,
        2200,
        2400,
        2600,
    )
}
