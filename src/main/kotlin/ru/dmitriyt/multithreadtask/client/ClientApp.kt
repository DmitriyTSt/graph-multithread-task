package ru.dmitriyt.multithreadtask.client

import io.grpc.Status
import io.grpc.StatusRuntimeException
import ru.dmitriyt.multithreadtask.ArgsManager
import ru.dmitriyt.multithreadtask.client.network.GraphTaskRepository
import ru.dmitriyt.multithreadtask.core.multi.MultiThreadClientSolver
import ru.dmitriyt.multithreadtask.core.multi.MultiThreadSolver
import ru.dmitriyt.multithreadtask.core.single.SingleClientSolver
import ru.dmitriyt.multithreadtask.core.single.SingleSolver
import ru.dmitriyt.multithreadtask.data.CnInTask
import java.util.*
import kotlin.system.exitProcess

class ClientApp(private val argsManager: ArgsManager) {
	private val repository = GraphTaskRepository(argsManager.serverAddress, argsManager.port)

	var graphs = LinkedList<String>()
	var successSent = ThreadLocal<Boolean>()

	fun start() {
		println("Client onStart")
		val solver = if (argsManager.isMulti) {
			MultiThreadClientSolver(CnInTask())
		} else {
			SingleClientSolver(CnInTask())
		}

		solver.run({
			try {
				repository.getTask()
			} catch (e: Exception) {
				0 to emptyList()
			}

		}, { taskId, result ->
			successSent.set(false)
			while (!successSent.get()) {
				try {
					repository.sendResult(taskId, result)
					successSent.set(true)
				} catch (e: StatusRuntimeException) {
					if (e.status.code == Status.UNAVAILABLE.code) {
						successSent.set(true)
						exitProcess(0)
					} else {
						println("Error send task $taskId ${e.status.code.name}")
					}
				} catch (e: Exception) {
					println("Error send task $taskId ${e.message}")
				}
			}
		})

		repository.close()
		println("client on close")
	}
}