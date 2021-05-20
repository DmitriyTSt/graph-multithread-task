package ru.dmitriyt.multithreadtask.core.multi

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ProducerConsumer(private val capacity: Int = 1000) {
    private val buffer = LinkedList<String>()
    private var bufferSize = AtomicInteger(0)

    fun produce(graph6: String) {
        while (bufferSize.get() == capacity) {
            Thread.sleep(10)
        }
        synchronized(this) {
            buffer.add(graph6)
            bufferSize.getAndIncrement()
        }
    }

    fun consume(): String? {
        synchronized(this) {
            return try {
                val graph6 = buffer.removeFirst()
                bufferSize.getAndDecrement()
                graph6
            } catch (e: Exception) {
                null
            }
        }
    }
}