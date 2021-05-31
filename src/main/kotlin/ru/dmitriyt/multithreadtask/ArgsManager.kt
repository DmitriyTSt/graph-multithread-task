package ru.dmitriyt.multithreadtask

class ArgsManager(_args: Array<String>) {
    private val args = _args.toList()

    val isMulti = args.contains("-m")

    val runMode = when {
        args.contains("-s") -> RunMode.SERVER
        args.contains("-c") -> RunMode.CLIENT
        else -> RunMode.STANDALONE
    }

    val serverAddress = getParam("--server") ?: "localhost"
    val port = getParam("--port")?.toIntOrNull() ?: 9999
    val partSize = getParam("-p")?.toIntOrNull() ?: 1000

    enum class RunMode {
        STANDALONE,
        CLIENT,
        SERVER
    }

    private fun getParam(key: String): String? {
        return args.indexOf(key).takeIf { it > -1 }?.let { args.getOrNull(it + 1) }
    }
}