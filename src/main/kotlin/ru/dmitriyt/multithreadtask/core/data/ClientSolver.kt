package ru.dmitriyt.multithreadtask.core.data

interface ClientSolver {
    fun run(inputProvider: () -> Pair<Int, List<String>>, resultHandler: (Int, SolverResult) -> Unit)
}