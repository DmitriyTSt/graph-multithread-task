package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.concurrent.thread

class MultiThreadSolver(private val graphTask: GraphTask<TaskResult>) : Solver {
    companion object {
        private const val PART_SIZE = 1000
    }

    private val ans = Array(Graph.MAX_N) { AtomicIntegerArray(Graph.MAX_N) }
    private var total = AtomicInteger(0)
    private val readLock = Any()

    override fun run(): SolverResult {
        val nCpu = Runtime.getRuntime().availableProcessors()
        val threads = IntRange(0, nCpu).map {
            thread {
                val graph6List = mutableListOf<String>()
                var isFinished = false
                while (!isFinished) {
                    synchronized(readLock) {
                        repeat(PART_SIZE) {
                            val graph6 = readLine()
                            if (graph6 == null) {
                                isFinished = true
                                return@repeat
                            }
                            total.getAndIncrement()
                            graph6List.add(graph6)
                        }
                    }
                    graph6List.forEach {
                        addResult(graphTask.solve(it))
                    }
                    graph6List.clear()
                }
            }
        }
        threads.map { it.join() }
        val result = mutableListOf<MutableList<Int>>()
        ans.forEach { atomicArray ->
            val resultRow = mutableListOf<Int>()
            repeat(Graph.MAX_N) {
                resultRow.add(atomicArray[it])
            }
            result.add(resultRow)
        }
        return SolverResult(total.get(), result)
    }

    private fun addResult(result: TaskResult) {
        ans[result.firstDimension].incrementAndGet(result.secondDimension)
        ans[result.firstDimension].incrementAndGet(Graph.MAX_N - 1)
        ans[Graph.MAX_N - 1].incrementAndGet(result.secondDimension)
    }
}