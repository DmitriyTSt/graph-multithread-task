package ru.dmitriyt.multithreadtask.core

interface GraphTask<out T> {
    fun solve(graph6: String): T
}