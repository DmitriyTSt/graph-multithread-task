package ru.dmitriyt.multithreadtask.client

import ru.dmitriyt.multithreadtask.ArgsManager
import ru.dmitriyt.multithreadtask.client.network.GraphTaskRepository
import ru.dmitriyt.multithreadtask.core.multi.MultiThreadSolver
import ru.dmitriyt.multithreadtask.core.single.SingleSolver
import ru.dmitriyt.multithreadtask.data.CnInTask
import java.util.*

class ClientApp(private val argsManager: ArgsManager) {
    private val repository = GraphTaskRepository(argsManager.serverAddress, argsManager.port)

    var graphs = LinkedList<String>()

    fun start() {
        println("Client onStart")
        val solver = if (argsManager.isMulti) {
            MultiThreadSolver(CnInTask())
        } else {
            SingleSolver(CnInTask())
        }

        val result = solver.run {
            repository.getTask()
        }

        repository.sendResult(result)
        repository.close()
    }
}