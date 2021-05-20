package ru.dmitriyt.multithreadtask

class ArgsManager(_args: Array<String>) {
    private val args = _args.toList()

    val isMulti = args.contains("-m")
}