package ru.dmitriyt.multithreadtask.core.data

interface GraphTask<out T> {
    fun solve(graph6: String): T
}