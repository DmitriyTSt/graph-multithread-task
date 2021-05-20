package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.Graph
import ru.dmitriyt.multithreadtask.core.Solver
import ru.dmitriyt.multithreadtask.core.SolverResult
import ru.dmitriyt.multithreadtask.core.TaskResult
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray

abstract class AbstractMultiSolver : Solver {
    private val ans = Array(Graph.MAX_N) { AtomicIntegerArray(Graph.MAX_N) }
    protected var total = AtomicInteger(0)

    protected fun getSolverResult(): SolverResult {
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

    protected fun addResult(result: TaskResult) {
        ans[result.firstDimension].incrementAndGet(result.secondDimension)
        ans[result.firstDimension].incrementAndGet(Graph.MAX_N - 1)
        ans[Graph.MAX_N - 1].incrementAndGet(result.secondDimension)
    }
}