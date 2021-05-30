package ru.dmitriyt.multithreadtask.core.data

interface Solver {
    fun run(inputProvider: () -> String?): SolverResult
}