package demo.false_sharing

import demo.IDemo
import helpers.repeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.system.measureTimeMillis

object FalseSharing : IDemo {
    override val title = "False sharing"

    override suspend fun execute() {
        suspend fun measureMs(
            iterations: Long,
            counters: ICounters,
            verbose: Boolean,
        ) {
            if (verbose) {
                println("Способ исполнения\tms")
            }

            counters.reset()

            measureTimeMillis {
                repeat(times = iterations) {
                    counters.counter1++
                }
                repeat(times = iterations) {
                    counters.counter2++
                }
            }.also {
                if (verbose) {
                    println("Последовательно\t$it")
                }
            }

            counters.reset()

            val twoThreadsPool = Dispatchers.IO.limitedParallelism(2)

            suspend fun warmUpPool() {
                withContext(twoThreadsPool) {
                    launch {
                        repeat(100) {
                            yield()
                        }
                    }
                    launch {
                        repeat(100) {
                            yield()
                        }
                    }
                }
            }

            warmUpPool()

            measureTimeMillis {
                withContext(twoThreadsPool) {
                    launch {
                        repeat(times = iterations) {
                            counters.counter1++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            counters.counter2++
                        }
                    }
                }
            }.also {
                if (verbose) {
                    println("Параллельно, одна кэш-линия\t$it")
                }
            }

            counters.reset()

            measureTimeMillis {
                withContext(twoThreadsPool) {
                    launch {
                        repeat(times = iterations) {
                            counters.counter1++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            counters.counter9++
                        }
                    }
                }
            }.also {
                if (verbose) {
                    println("Параллельно, разные кэш-линии\t$it")
                }
            }
        }

        val counters = Counters()

        suspend fun warmUp() {
            measureMs(
                iterations = 1,
                counters = counters,
                verbose = false,
            )
        }

        warmUp()

        measureMs(
            iterations = 10_000_000_000L,
            counters = counters,
            verbose = true,
        )
    }
}
