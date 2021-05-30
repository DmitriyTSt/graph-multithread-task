package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.data.GraphTask
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.core.data.TaskResult
import kotlin.concurrent.thread

class MultiThreadSolver(private val graphTask: GraphTask<TaskResult>) : AbstractMultiSolver() {
    companion object {
        private const val PART_SIZE = 1000
    }

    private val readLock = Any()

    override fun run(inputProvider: () -> String?): SolverResult {
        val nCpu = Runtime.getRuntime().availableProcessors()
        val threads = IntRange(0, nCpu).map {
            thread {
                val graph6List = mutableListOf<String>()
                var isFinished = false
                while (!isFinished) {
                    synchronized(readLock) {
                        repeat(PART_SIZE) {
                            val graph6 = inputProvider()
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

        return getSolverResult()
    }
}