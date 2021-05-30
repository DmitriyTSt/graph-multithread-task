package ru.dmitriyt.multithreadtask

import ru.dmitriyt.multithreadtask.core.TimeHelper
import ru.dmitriyt.multithreadtask.core.data.Solver
import ru.dmitriyt.multithreadtask.core.multi.MultiThreadSolver
import ru.dmitriyt.multithreadtask.core.single.SingleSolver
import ru.dmitriyt.multithreadtask.data.CnInTask
import kotlin.system.exitProcess

class StandaloneApp(private val argsManager: ArgsManager) {
    fun start() {
        val solver = if (argsManager.isMulti) {
            MultiThreadSolver(CnInTask())
        } else {
            SingleSolver(CnInTask())
        }

        val startTime = System.currentTimeMillis()
        val result = solver.run { readLine() }
        val endTime = System.currentTimeMillis()

        println("Total: ${result.total}")
        println(" \t${IntRange(0, result.ans.size - 1).joinToString("\t")}")
        result.ans.forEachIndexed { index, it ->
            println("$index\t${it.joinToString("\t")}")
        }

        println(TimeHelper.getFormattedSpentTime(startTime, endTime))
        exitProcess(0)
    }
}