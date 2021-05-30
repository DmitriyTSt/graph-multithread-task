package ru.dmitriyt.multithreadtask.client.network

import io.grpc.ManagedChannelBuilder
import ru.dmitriyt.multithreadtask.GraphTaskGrpc
import ru.dmitriyt.multithreadtask.GraphTaskProto
import ru.dmitriyt.multithreadtask.core.data.SolverResult
import java.io.Closeable
import java.util.concurrent.TimeUnit

class GraphTaskRepository(server: String, port: Int) : Closeable {
    private val channel = ManagedChannelBuilder.forAddress(server, port).usePlaintext().build()
    private val stub = GraphTaskGrpc.newBlockingStub(channel)

    fun getTask(): List<String> {
        return stub.getTask(GraphTaskProto.GetTaskRequest.newBuilder().build()).graphsList
    }

    fun sendResult(result: SolverResult) {
        try {
            stub.sendTaskResult(
                GraphTaskProto.SendTaskResultRequest.newBuilder()
                    .addAllResultRows(
                        result.ans.map {
                            GraphTaskProto.TaskRow.newBuilder()
                                .addAllCount(it)
                                .build()
                        }
                    )
                    .setTotal(result.total)
                    .build()
            )
        } catch (e: Exception) {

        }
    }

    override fun close() {
        channel.shutdown()
    }
}