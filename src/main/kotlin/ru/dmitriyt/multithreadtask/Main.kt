package ru.dmitriyt.multithreadtask

import ru.dmitriyt.multithreadtask.data.CnInTask
import ru.dmitriyt.multithreadtask.core.Solver
import ru.dmitriyt.multithreadtask.core.multi.MultiThreadSolver
import ru.dmitriyt.multithreadtask.core.single.SingleSolver
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val argsManager = ArgsManager(args)

    val solver: Solver = if (argsManager.isMulti) {
        MultiThreadSolver(CnInTask())
    } else {
        SingleSolver(CnInTask())
    }

    val startTime = System.currentTimeMillis()
    val result = solver.run()
    val endTime = System.currentTimeMillis()

    println("Total: ${result.total}")
    println(" \t${IntRange(0, result.ans.size - 1).joinToString("\t")}")
    result.ans.forEachIndexed { index, it ->
        println("$index\t${it.joinToString("\t")}")
    }

    println(getFormattedSpentTime(startTime, endTime))
    exitProcess(0)
}

private fun getFormattedSpentTime(startTime: Long, endTime: Long): String {
    val totalTime = endTime - startTime
    val millis = totalTime % 1000
    val seconds = totalTime / 1000 % 60
    val minutes = totalTime / 1000 / 60 % 60
    val hours = totalTime / 1000 / 60 / 60
    val timeBuilder = StringBuilder()
    if (hours != 0L) timeBuilder.append("${hours}h ")
    if (minutes != 0L || timeBuilder.isNotEmpty()) timeBuilder.append("${minutes}m ")
    timeBuilder.append("%d.%03ds".format(seconds, millis))
    return timeBuilder.toString()
}