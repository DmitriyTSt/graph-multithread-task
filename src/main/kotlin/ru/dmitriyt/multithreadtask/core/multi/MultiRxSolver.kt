package ru.dmitriyt.multithreadtask.core.multi

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import ru.dmitriyt.multithreadtask.core.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MultiRxSolver(private val task: GraphTask<TaskResult>) : AbstractMultiSolver() {
    private val batch = AtomicInteger(0)

    override fun run(): SolverResult {
        val threadCnt = Runtime.getRuntime().availableProcessors() + 1
        val executor = Executors.newFixedThreadPool(threadCnt)
        val scheduler = Schedulers.from(executor)

        val source = Flowable.create(FlowableOnSubscribe<String> { emitter ->
            var graph6: String? = readLine()
            while (graph6 != null) {
                total.getAndIncrement()
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

        return getSolverResult()
    }
}