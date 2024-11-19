package demo.matrices

import demo.IDemo
import demo.matrices.MultiplyMatrices.DEMO_MATRIX_SIZES
import kotlin.system.measureTimeMillis

object MultiplyMatricesWithTransposition : IDemo {
    override val title = "Умножение матриц с транспонированием"

    override suspend fun execute() {
        fun measureMatrixMultiplyMs(
            matrixSize: Int,
        ): Long {
            val matrix1 = Matrix.generateRandomSquareMatrix(size = matrixSize)
            val matrix2 = Matrix.generateRandomSquareMatrix(size = matrixSize)

            val ms = measureTimeMillis {
                matrix1 multiplyWithTransposition matrix2
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
}
