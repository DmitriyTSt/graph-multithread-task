package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.data.GraphTask
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.core.data.TaskResult
import kotlin.concurrent.thread

class MultiThreadSolver(private val graphTask: GraphTask<TaskResult>) : AbstractMultiSolver() {

    private val readLock = Any()

    override fun run(inputProvider: () -> List<String>): SolverResult {
        val nCpu = Runtime.getRuntime().availableProcessors()
        val threads = IntRange(0, nCpu).map {
            thread {
                var graph6List: List<String>
                graph6List = inputProvider()
                total.getAndAdd(graph6List.size)
                while (graph6List.isNotEmpty()) {
                    graph6List.forEach {
                        addResult(graphTask.solve(it))
                    }
                    graph6List = inputProvider()
                }
            }
        }
        threads.map { it.join() }

        return getSolverResult()
    }
}