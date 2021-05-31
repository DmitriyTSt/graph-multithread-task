package ru.dmitriyt.multithreadtask.core.multi

import ru.dmitriyt.multithreadtask.core.data.ClientSolver
import ru.dmitriyt.multithreadtask.core.data.Graph
import ru.dmitriyt.multithreadtask.core.data.GraphTask
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.core.data.TaskResult
import kotlin.concurrent.thread

class MultiThreadClientSolver(
	private val graphTask: GraphTask<TaskResult>,
) : ClientSolver {
	private val ans: ThreadLocal<Array<IntArray>> = ThreadLocal()
	private val total: ThreadLocal<Int> = ThreadLocal()

	override fun run(inputProvider: () -> Pair<Int, List<String>>, resultHandler: (Int, SolverResult) -> Unit) {
		val nCpu = Runtime.getRuntime().availableProcessors()
		val threads = IntRange(0, nCpu).map {
			thread {
				clearResult(initial = true)
				var input = inputProvider()
				var graph6List = input.second
				var taskId = input.first
				total.set(total.get() + graph6List.size)
				while (graph6List.isNotEmpty()) {
					graph6List.forEach {
						addResult(graphTask.solve(it))
					}
					resultHandler(taskId, getSolverResult())
					clearResult()
					input = inputProvider()
					graph6List = input.second
					taskId = input.first
					total.set(total.get() + graph6List.size)
				}
			}
		}
		threads.map { it.join() }
	}

	private fun getSolverResult(): SolverResult {
		val result = mutableListOf<MutableList<Int>>()
		ans.get().forEach { atomicArray ->
			val resultRow = mutableListOf<Int>()
			repeat(Graph.MAX_N) {
				resultRow.add(atomicArray[it])
			}
			result.add(resultRow)
		}
		return SolverResult(total.get(), result)
	}

	private fun clearResult(initial: Boolean = false) {
		total.set(0)
		if (initial) {
			ans.set(Array(Graph.MAX_N) { IntArray(Graph.MAX_N) { 0 } })
		} else {
			repeat(Graph.MAX_N) { i ->
				repeat(Graph.MAX_N) { j ->
					ans.get()[i][j] = 0
				}
			}
		}

	}

	private fun addResult(result: TaskResult) {
		ans.get()[result.firstDimension][result.secondDimension]++
		ans.get()[result.firstDimension][Graph.MAX_N - 1]++
		ans.get()[Graph.MAX_N - 1][result.secondDimension]++
	}
}