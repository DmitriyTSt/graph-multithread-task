package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.data.GraphTask
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.core.data.TaskResult
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class MultiThreadPCSolver(private val graphTask: GraphTask<TaskResult>) : AbstractMultiSolver() {
    private val pc = ProducerConsumer()

    override fun run(inputProvider: () -> String?): SolverResult {
        val nCpu = Runtime.getRuntime().availableProcessors()
        val threads = IntRange(0, nCpu - 1).map {
            thread {
                var needFinish = false
                while (true) {
                    val graph6 = pc.consume()
                    if (graph6 == null) {
                        if (needFinish) break
                        Thread.sleep(10)
                        needFinish = true
                    } else {
                        needFinish = false
                        val result = graphTask.solve(graph6)
                        addResult(result)
                    }
                }
            }
        }

        var graph6: String? = readLine()
        while (graph6 != null) {
            total.getAndIncrement()
            pc.produce(graph6)
            graph6 = readLine()
        }
        threads.map { it.join() }
        return getSolverResult()
    }

    class ProducerConsumer(private val capacity: Int = 1000) {
        private val buffer = LinkedList<String>()
        private var bufferSize = AtomicInteger(0)

        fun produce(graph6: String) {
            while (bufferSize.get() == capacity) {
                Thread.sleep(10)
            }
            synchronized(this) {
                buffer.add(graph6)
                bufferSize.getAndIncrement()
            }
        }

        fun consume(): String? {
            synchronized(this) {
                return try {
                    val graph6 = buffer.removeFirst()
                    bufferSize.getAndDecrement()
                    graph6
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}