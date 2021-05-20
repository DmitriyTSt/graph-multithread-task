package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.*
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.concurrent.thread

class MultiThreadPCSolver(private val graphTask: GraphTask<TaskResult>) : Solver {
    private val ans = Array(Graph.MAX_N) { AtomicIntegerArray(Graph.MAX_N) }
    private val pc = ProducerConsumer()

    override fun run(): SolverResult {
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

        var total = 0
        var graph6: String? = readLine()
        while (graph6 != null) {
            total++
            pc.produce(graph6)
            graph6 = readLine()
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
        return SolverResult(total, result)
    }

    private fun addResult(result: TaskResult) {
        ans[result.firstDimension].incrementAndGet(result.secondDimension)
        ans[result.firstDimension].incrementAndGet(Graph.MAX_N - 1)
        ans[Graph.MAX_N - 1].incrementAndGet(result.secondDimension)
    }
}