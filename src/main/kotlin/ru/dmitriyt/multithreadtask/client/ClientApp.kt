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
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

class ClientApp(private val argsManager: ArgsManager) {
	private val repository = GraphTaskRepository(argsManager.serverAddress, argsManager.port)
	private val completedTaskCount = AtomicInteger(0)
	private val lock = Any()

	fun start() {
		println("Client onStart")
		val solver = if (argsManager.isMulti) {
			MultiThreadClientSolver(CnInTask())
		} else {
			SingleClientSolver(CnInTask())
		}

		solver.run({
			synchronized(lock) {
				try {
					repository.getTask()
				} catch (e: Exception) {
					0 to emptyList()
				}
			}
		}, { taskId, result ->
			synchronized(lock) {
				try {
					repository.sendResult(taskId, result)
					completedTaskCount.getAndIncrement()
				} catch (e: Exception) {
					if (e is StatusRuntimeException && e.status.code == Status.Code.UNAVAILABLE) {
						repository.close()
						println("client on close")
						println("Solved tasks count : ${completedTaskCount.get()}")
						exitProcess(0)
					} else {
						println("error send task $taskId")
						e.printStackTrace()
					}
				}
			}
		})

		repository.close()
		println("client on close")
		println("Solved tasks count : ${completedTaskCount.get()}")
	}
}