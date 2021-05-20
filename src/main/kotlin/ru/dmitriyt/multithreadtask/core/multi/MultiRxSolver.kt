package ru.dmitriyt.multithreadtask.core.multi

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import ru.dmitriyt.multithreadtask.core.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray


class MultiRxSolver(private val task: GraphTask<TaskResult>) : Solver {
    private val ans = Array(Graph.MAX_N) { AtomicIntegerArray(Graph.MAX_N) }
    private val batch = AtomicInteger(0)

    override fun run(): SolverResult {
        val threadCnt = Runtime.getRuntime().availableProcessors() + 1
        val executor = Executors.newFixedThreadPool(threadCnt)
        val scheduler = Schedulers.from(executor)

        var total = 0
        val source = Flowable.create(FlowableOnSubscribe<String> { emitter ->
            var graph6: String? = readLine()
            while (graph6 != null) {
                total++
                emitter.onNext(graph6)
                graph6 = readLine()
            }
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)
        source
            .groupBy { batch.getAndIncrement() % threadCnt }
            .flatMap { group ->
                group.observeOn(scheduler)
                    .map { graph ->
                        task.solve(graph)
                    }
                    .map { result ->
                        addResult(result)
                    }
            }
            .blockingSubscribe()

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