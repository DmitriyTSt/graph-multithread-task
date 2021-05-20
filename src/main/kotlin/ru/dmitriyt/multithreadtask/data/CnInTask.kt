package ru.dmitriyt.multithreadtask.data

import ru.dmitriyt.multithreadtask.core.Graph
import ru.dmitriyt.multithreadtask.core.GraphDrawer
import ru.dmitriyt.multithreadtask.core.GraphTask
import ru.dmitriyt.multithreadtask.core.TaskResult

/**
 * Задание вычисления кликового числа и числа независимости
 */
class CnInTask : GraphTask<CnInTask.Result> {

    class Result(
        cliqueNumber: Int,
        independenceNumber: Int
    ) : TaskResult {
        override val firstDimension = cliqueNumber
        override val secondDimension = independenceNumber
    }

    override fun solve(graph6: String): Result {
        val graph = Graph(graph6)
        val cliqueNumber = Clique(graph).cliqueNumber()
        val independenceNumber = Clique(graph.inversed()).cliqueNumber()
        if (cliqueNumber == 2 && independenceNumber == graph.n - 1 ||
            independenceNumber == 2 && cliqueNumber == graph.n - 1
        ) {
            GraphDrawer(graph).drawImage()
        }
        return Result(cliqueNumber, independenceNumber)
    }
}