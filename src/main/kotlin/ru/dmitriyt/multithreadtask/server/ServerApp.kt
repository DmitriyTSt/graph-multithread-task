package ru.dmitriyt.multithreadtask.server

import io.grpc.ServerBuilder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.dmitriyt.multithreadtask.ArgsManager
import ru.dmitriyt.multithreadtask.core.TimeHelper
import ru.dmitriyt.multithreadtask.core.data.Graph
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import ru.dmitriyt.multithreadtask.server.network.GraphTaskService
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.system.exitProcess

class ServerApp(private val argsManager: ArgsManager) {
    private val ans = Array(Graph.MAX_N) { AtomicIntegerArray(Graph.MAX_N) }
    private var total = AtomicInteger(0)
    private var startTime = 0L
    private var endTime = 0L
    private var processedGraphs = AtomicInteger(0)
    private var resultHandled = false
    private val resultMutex = Mutex()
    private var isCompleted = false

    private val server = ServerBuilder
        .forPort(argsManager.port)
        .addService(GraphTaskService(argsManager.partSize, ::handleStart, ::handleResult) { isCompleted = true })
        .build()

    fun start() {
        server.start()
        println("Server started at port ${argsManager.port}")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this.server.shutdown()
            }
        )
        server.awaitTermination()
    }

    private fun handleStart(partSize: Int) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
        total.getAndAdd(partSize)
    }

    private suspend fun handleResult(counts: List<List<Int>>, total: Int, tasksInProgress: Int) {
        processedGraphs.getAndAdd(total)
        counts.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, count ->
                ans[rowIndex].getAndAdd(columnIndex, count)
            }
        }
//        println("total = ${this.total.get()}, processed = ${processedGraphs.get()}, inprogress = $tasksInProgress, isComplete = $isCompleted")
        if (this.total.get() == processedGraphs.get() && tasksInProgress == 0 && isCompleted) {
            resultMutex.withLock {
                if (!resultHandled) {
                    resultHandled = true
                    endTime = System.currentTimeMillis()
                    printResultAndStop()
                }
            }
        }
    }

    private fun printResultAndStop() {
        val resultCounts = mutableListOf<MutableList<Int>>()
        ans.forEach { atomicArray ->
            val resultRow = mutableListOf<Int>()
            repeat(Graph.MAX_N) {
                resultRow.add(atomicArray[it])
            }
            resultCounts.add(resultRow)
        }
        val result = SolverResult(total.get(), resultCounts)

        println("Total: ${result.total}")
        println(" \t${IntRange(0, result.ans.size - 1).joinToString("\t")}")
        result.ans.forEachIndexed { index, it ->
            println("$index\t${it.joinToString("\t")}")
        }

        println(TimeHelper.getFormattedSpentTime(startTime, endTime))
        exitProcess(0)
    }
}