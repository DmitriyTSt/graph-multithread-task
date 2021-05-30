package ru.dmitriyt.multithreadtask.server.network

import ru.dmitriyt.multithreadtask.GraphTaskGrpcKt
import ru.dmitriyt.multithreadtask.GraphTaskProto

class GraphTaskService(
    private val partSize: Int = 1000,
    private val startHandler: (Int) -> Unit,
    private val resultHandler: suspend (List<List<Int>>, total: Int) -> Unit
) : GraphTaskGrpcKt.GraphTaskCoroutineImplBase() {

    override suspend fun getTask(
        request: GraphTaskProto.GetTaskRequest
    ): GraphTaskProto.GetTaskResponse {
        val graphs = mutableListOf<String>()
        var lastPart = false
        var graph: String?
        repeat(partSize) {
            graph = readLine()
            graph?.let { graphs.add(it) } ?: run {
                lastPart = true
                return@repeat
            }
        }
        startHandler(graphs.size)
        return GraphTaskProto.GetTaskResponse.newBuilder()
            .setLastPart(lastPart)
            .addAllGraphs(graphs)
            .build()
    }

    override suspend fun sendTaskResult(
        request: GraphTaskProto.SendTaskResultRequest
    ): GraphTaskProto.SendTaskResultResponse {
        resultHandler(request.resultRowsList.map { it.countList }, request.total)
        return GraphTaskProto.SendTaskResultResponse.newBuilder().build()
    }
}