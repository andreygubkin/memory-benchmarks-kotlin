import demo.*
import demo.false_sharing.FalseSharing
import demo.matrices.MatrixTranspose
import demo.matrices.MatrixTraverse
import demo.matrices.MultiplyMatrices
import demo.matrices.MultiplyMatricesWithTransposition

private fun getDemos(): List<IDemo> = listOf(
    MatrixTraverse,
    MultiplyMatrices,
    MultiplyMatricesWithTransposition,
    MatrixTranspose,
    FalseSharing,
)

suspend fun main() {
    getDemos()
        .forEach {
            it.run()
        }
}
