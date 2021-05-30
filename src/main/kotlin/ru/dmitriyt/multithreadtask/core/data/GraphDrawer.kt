package ru.dmitriyt.multithreadtask.core.data

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin

class GraphDrawer(private val graph: Graph) {
    companion object {
        private const val IMAGE_SIZE = 300
        private const val VERTEX_RADIUS = 20
        private const val GRAPH_RADIUS = 100
        private const val PATH = "graphs/"
        private const val EXTENSION = "png"
    }

    fun drawImage() {
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.apply {
            paint = Color.WHITE
            fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)
            paint = Color.BLACK
            font = Font(null, Font.BOLD, 24)
            val vertexCount = graph.n
            val dAngle = 360 / vertexCount
            var angle = 0
            // draw edges
            repeat(graph.n) { i ->
                repeat(graph.n) { j ->
                    if (graph.a[i][j] == 1) {
                        val (x1, y1) = getVertexCenter(i, dAngle)
                        val (x2, y2) = getVertexCenter(j, dAngle)
                        drawEdge(this, x1, y1, x2, y2)
                    }
                }
            }
            // draw vertices
            repeat(graph.n) { vertex ->
                drawVertex(
                    this,
                    vertex,
                    IMAGE_SIZE / 2 + (GRAPH_RADIUS * sin(Math.toRadians(angle.toDouble()))).toInt(),
                    IMAGE_SIZE / 2 + (GRAPH_RADIUS * cos(Math.toRadians(angle.toDouble()))).toInt()
                )
                angle += dAngle
            }

            dispose()
        }

        val dir = File(PATH)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val number = dir.listFiles()?.size
        ImageIO.write(image, EXTENSION, File("$PATH$number.$EXTENSION"))
    }

    private fun getVertexCenter(vertex: Int, dAngle: Int): Pair<Int, Int> {
        val angle = dAngle * vertex
        val x = IMAGE_SIZE / 2 + (GRAPH_RADIUS * sin(Math.toRadians(angle.toDouble()))).toInt()
        val y = IMAGE_SIZE / 2 + (GRAPH_RADIUS * cos(Math.toRadians(angle.toDouble()))).toInt()
        return x to y
    }

    private fun drawVertex(graphics: Graphics2D, vertex: Int, centerX: Int, centerY: Int) {
        graphics.stroke = BasicStroke(2f)
        graphics.paint = Color.LIGHT_GRAY
        graphics.fillOval(
            centerX - VERTEX_RADIUS,
            centerY - VERTEX_RADIUS,
            2 * VERTEX_RADIUS,
            2 * VERTEX_RADIUS
        )
        graphics.paint = Color.BLACK
        graphics.drawOval(
            centerX - VERTEX_RADIUS,
            centerY - VERTEX_RADIUS,
            2 * VERTEX_RADIUS,
            2 * VERTEX_RADIUS
        )
        graphics.drawCenteredString(
            vertex.toString(),
            Rectangle(
                centerX - VERTEX_RADIUS,
                centerY - VERTEX_RADIUS,
                2 * VERTEX_RADIUS,
                2 * VERTEX_RADIUS
            )
        )
    }

    private fun drawEdge(graphics: Graphics2D, v1x: Int, v1y: Int, v2x: Int, v2y: Int) {
        graphics.paint = Color.BLACK
        graphics.stroke = BasicStroke(4f)
        graphics.drawLine(v1x, v1y, v2x, v2y)
    }

    private fun Graphics.drawCenteredString(text: String, rect: Rectangle) {
        val metrics = getFontMetrics(font)
        val x = rect.x + (rect.width - metrics.stringWidth(text)) / 2
        val y = rect.y + (rect.height - metrics.height) / 2 + metrics.ascent
        drawString(text, x, y)
    }
}