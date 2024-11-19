package demo.false_sharing

import demo.IDemo
import helpers.repeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

object FalseSharing : IDemo {
    override val title = "False sharing"

    override suspend fun execute() {

        fun someEasyCpuWork() {
            var sum = 0L
            repeat(20) {
                sum += it
            }
            sum.toString()
        }

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
                    someEasyCpuWork()
                    counters.counter1++
                }
                repeat(times = iterations) {
                    someEasyCpuWork()
                    counters.counter2++
                }
                repeat(times = iterations) {
                    someEasyCpuWork()
                    counters.counter3++
                }
            }.also {
                if (verbose) {
                    println("Последовательно\t$it")
                }
            }

            counters.reset()

            val threeThreadsPool = Dispatchers.IO.limitedParallelism(3)

            suspend fun warmUpPool() {
                withContext(threeThreadsPool) {
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
                    launch {
                        repeat(100) {
                            yield()
                        }
                    }
                }
            }

            warmUpPool()

            measureTimeMillis {
                withContext(threeThreadsPool) {
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter1++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter2++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter3++
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
                withContext(threeThreadsPool) {
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter1++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter10++
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            counters.counter19++
                        }
                    }
                }
            }.also {
                if (verbose) {
                    println("Параллельно, разные кэш-линии\t$it")
                }
            }

            measureTimeMillis {
                val atomicCounter = AtomicInteger()

                withContext(threeThreadsPool) {
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            atomicCounter.incrementAndGet()
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            atomicCounter.incrementAndGet()
                        }
                    }
                    launch {
                        repeat(times = iterations) {
                            someEasyCpuWork()
                            atomicCounter.incrementAndGet()
                        }
                    }
                }
            }.also {
                if (verbose) {
                    println("Параллельно, общий атомик\t$it")
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
            iterations = 500_000_000L,
            counters = counters,
            verbose = true,
        )
    }
}
