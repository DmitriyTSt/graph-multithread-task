package ru.dmitriyt.multithreadtask

import ru.dmitriyt.multithreadtask.client.ClientApp
import ru.dmitriyt.multithreadtask.server.ServerApp

fun main(args: Array<String>) {
    val argsManager = ArgsManager(args)

    when (argsManager.runMode) {
        ArgsManager.RunMode.STANDALONE -> StandaloneApp(argsManager).start()
        ArgsManager.RunMode.CLIENT -> ClientApp(argsManager).start()
        ArgsManager.RunMode.SERVER -> ServerApp(argsManager).start()
    }
}