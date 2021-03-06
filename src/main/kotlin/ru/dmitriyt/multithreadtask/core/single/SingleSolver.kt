package ru.dmitriyt.multithreadtask.core.single

import ru.dmitriyt.multithreadtask.core.*

class SingleSolver(private val graphTask: GraphTask<TaskResult>) : Solver {
    private val ans = Array(Graph.MAX_N) { Array(Graph.MAX_N) { 0 } }

    override fun run(): SolverResult {
        var total = 0
        var graph6: String? = readLine()
        while (graph6 != null) {
            total++
            val result = graphTask.solve(graph6)
            addResult(result)
            graph6 = readLine()
        }
        return SolverResult(total, ans.map { it.toList() })
    }

    private fun addResult(result: TaskResult) {
        ans[result.firstDimension][result.secondDimension]++
        ans[Graph.MAX_N - 1][result.secondDimension]++
        ans[result.firstDimension][Graph.MAX_N - 1]++
    }
}