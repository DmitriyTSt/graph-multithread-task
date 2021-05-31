package ru.dmitriyt.multithreadtask.server.network

import ru.dmitriyt.multithreadtask.GraphTaskGrpcKt
import ru.dmitriyt.multithreadtask.GraphTaskProto
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class GraphTaskService(
    private val partSize: Int = 1000,
    private val startHandler: (Int) -> Unit,
    private val resultHandler: suspend (List<List<Int>>, total: Int) -> Unit
) : GraphTaskGrpcKt.GraphTaskCoroutineImplBase() {
    private val taskId = AtomicInteger(0)
    private val tasks = LinkedList<GraphTaskProto.GetTaskResponse>()

    override suspend fun getTask(
        request: GraphTaskProto.GetTaskRequest
    ): GraphTaskProto.GetTaskResponse {
        val graphs = mutableListOf<String>()
        repeat(partSize) {
            readLine()?.let { graphs.add(it) } ?: run {
                return@repeat
            }
        }
        return if (graphs.isEmpty()) {
            tasks.firstOrNull() ?: GraphTaskProto.GetTaskResponse.newBuilder()
                .addAllGraphs(emptyList())
                .build()
        } else {
            startHandler(graphs.size)
            val localTaskId = taskId.getAndIncrement()
            val response = GraphTaskProto.GetTaskResponse.newBuilder()
                .setTaskId(localTaskId)
                .addAllGraphs(graphs)
                .build()
            tasks.add(response)
            response
        }
    }

    override suspend fun sendTaskResult(
        request: GraphTaskProto.SendTaskResultRequest
    ): GraphTaskProto.SendTaskResultResponse {
        val taskId = request.taskId
        tasks.find { it.taskId == taskId }?.let { tasks.remove(it) }
        resultHandler(request.resultRowsList.map { it.countList }, request.total)
        return GraphTaskProto.SendTaskResultResponse.newBuilder().build()
    }
}