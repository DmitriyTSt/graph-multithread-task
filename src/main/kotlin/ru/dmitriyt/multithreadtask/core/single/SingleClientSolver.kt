package ru.dmitriyt.multithreadtask.core.single

import ru.dmitriyt.multithreadtask.core.data.*

class SingleClientSolver(private val graphTask: GraphTask<TaskResult>) : ClientSolver {
    private val ans = Array(Graph.MAX_N) { Array(Graph.MAX_N) { 0 } }
    private var total = 0

    override fun run(inputProvider: () -> Pair<Int, List<String>>, resultHandler: (Int, SolverResult) -> Unit) {
        var input = inputProvider()
        var taskId = input.first
        var graph6List = input.second
        while (graph6List.isNotEmpty()) {
            total += graph6List.size
            graph6List.forEach { graph6 ->
                val result = graphTask.solve(graph6)
                addResult(result)
            }
            resultHandler(taskId, SolverResult(total, ans.map { it.toList() }))
            clearResult()
            input = inputProvider()
            taskId = input.first
            graph6List = input.second
        }
    }

    private fun addResult(result: TaskResult) {
        ans[result.firstDimension][result.secondDimension]++
        ans[Graph.MAX_N - 1][result.secondDimension]++
        ans[result.firstDimension][Graph.MAX_N - 1]++
    }

    private fun clearResult() {
        total = 0
        repeat(Graph.MAX_N) { i ->
            repeat(Graph.MAX_N) { j ->
                ans[i][j] = 0
            }
        }
    }
}