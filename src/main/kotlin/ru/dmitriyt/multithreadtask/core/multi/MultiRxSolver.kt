package ru.dmitriyt.multithreadtask.core.multi

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import ru.dmitriyt.multithreadtask.core.data.GraphTask
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.core.data.TaskResult
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MultiRxSolver(private val task: GraphTask<TaskResult>) : AbstractMultiSolver() {
    private val batch = AtomicInteger(0)

    override fun run(inputProvider: () -> List<String>): SolverResult {
        val threadCnt = Runtime.getRuntime().availableProcessors() + 1
        val executor = Executors.newFixedThreadPool(threadCnt)
        val scheduler = Schedulers.from(executor)

        val source = Flowable.create(FlowableOnSubscribe<String> { emitter ->
            var graph6List = inputProvider()
            while (graph6List.isNotEmpty()) {
                total.getAndAdd(graph6List.size)
                graph6List.forEach { graph6 ->
                    emitter.onNext(graph6)
                }
                graph6List = inputProvider()
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

        return getSolverResult()
    }
}