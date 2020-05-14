package JavaExtractor.SocketServer

import JavaExtractor.Common.CommandLineValues
import JavaExtractor.ExtractFeaturesTask
import JavaExtractor.FeatureExtractor
import JavaExtractor.FeaturesEntities.ProgramFeatures
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList
import kotlin.concurrent.thread

private val args = CommandLineValues(
    *"--max_path_length 8 --max_path_width 2 --dir ../code2vec-satd/build-dataset/java-small/one --num_threads 10".split(
        " "
    ).toTypedArray()
)

fun main() {
    val server = ServerSocket(9999)
    println("Server is running on port ${server.localPort}")
    while (true) {
        val client = server.accept()
        thread(isDaemon = true) { ClientHandler(client).serve() }
    }
}

class ClientHandler(val client: Socket) {
    val out = DataOutputStream(client.getOutputStream())
    fun serve() {

        DataInputStream(client.getInputStream()).use { inp ->
            val str = try {
                val code = inp.readChunkedString()
//                println("------------------------------------")
//                println(code)
                val featureExtractor = FeatureExtractor(args)

                val features = featureExtractor.extractFeatures(code)
                featuresToString(features)
            } catch (ex: Exception) {
                ex.printStackTrace()
                "FAILED\t"+ ex.toString().replace("\n", "\\n")
            }
            try {
                out.sendStringInChunk(str)
            } catch (e: Exception) {
            }
            try {
                out.close()
            } catch (e: Exception) {
            }
        }
    }

    private fun featuresToString(features: ArrayList<ProgramFeatures>?) = if (features == null) {
        "FAILED\tNULL"
    } else {
        val toPrint = ExtractFeaturesTask.featuresToString(features, args)
        if (toPrint.isNotEmpty()) {
            "OK\t$toPrint"
        } else "FAILED\ttoPrint.length()==0"
    }


}


private fun DataOutputStream.sendStringInChunk(source: String) {
    val chunk = source.chunked(1024 * 32)
    writeInt(chunk.size)
    chunk.forEach { writeUTF(it) }
    flush()
}

private fun DataInputStream.readChunkedString(): String {
    val chunkCount = readInt()
    val code = (1..chunkCount).joinToString("") { readUTF() }
    return code
}