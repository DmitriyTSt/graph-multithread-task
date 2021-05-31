package ru.dmitriyt.multithreadtask.client.network

import io.grpc.ManagedChannelBuilder
import ru.dmitriyt.multithreadtask.GraphTaskGrpc
import ru.dmitriyt.multithreadtask.GraphTaskProto
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class GraphTaskRepository(server: String, port: Int) : Closeable {
    private val channel = ManagedChannelBuilder.forAddress(server, port).usePlaintext().build()
    private val stub = GraphTaskGrpc.newBlockingStub(channel)

    fun getTask(): Pair<Int, List<String>> {
        return stub.getTask(GraphTaskProto.GetTaskRequest.newBuilder().build()).let { it.taskId to it.graphsList }
    }

    fun sendResult(taskId: Int, result: SolverResult) {
//        try {
            stub.sendTaskResult(
                GraphTaskProto.SendTaskResultRequest.newBuilder()
                    .addAllResultRows(
                        result.ans.map {
                            GraphTaskProto.TaskRow.newBuilder()
                                .addAllCount(it)
                                .build()
                        }
                    )
                    .setTaskId(taskId)
                    .setTotal(result.total)
                    .build()
            )
//            println("Task $taskId sent")
//        } catch (e: Exception) {
//            println("Error send task $taskId")
//        }
    }

    override fun close() {
        channel.shutdown()
    }
}