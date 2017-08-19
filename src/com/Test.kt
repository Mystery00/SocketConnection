package com

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.util.*

class Test
{
    companion object
    {
        private val SERVER_PORT = 6666

        @JvmStatic
        fun main(args: Array<String>)
        {
            try
            {
                val socket = Socket("127.0.0.1", SERVER_PORT)
                val scanner = Scanner(System.`in`)
                println("请输入id")
                val id = scanner.nextLine()
                val result = sendMessage(socket, id)
                println(result)
                Thread(sendThread(scanner, socket)).start()
                Thread(receiveThread(socket)).start()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

        private fun sendMessage(socket: Socket, message: String): Boolean
        {
            return try
            {
                val outputStream = socket.getOutputStream()
                outputStream.write((message + "\n").toByteArray())
                outputStream.flush()
                true
            }
            catch (e: IOException)
            {
                e.printStackTrace()
                false
            }
        }
    }
}

class sendThread(private val scanner: Scanner, private val socket: Socket) : Runnable
{
    override fun run()
    {
        while (true)
        {
            val message = scanner.nextLine()
            sendMessage(socket, message)
        }
    }

    private fun sendMessage(socket: Socket, message: String): Boolean
    {
        return try
        {
            val outputStream = socket.getOutputStream()
            outputStream.write((message + "\n").toByteArray())
            outputStream.flush()
            true
        }
        catch (e: IOException)
        {
            e.printStackTrace()
            false
        }
    }
}

class receiveThread(private val socket: Socket) : Runnable
{
    override fun run()
    {
        while (true)
        {
            val response = receiveMessage(socket)
            if (response != null)
                println(response)
        }
    }

    private fun receiveMessage(socket: Socket): String?
    {
        var response: String? = null
        try
        {
            val inputStream = socket.getInputStream()
            response = BufferedReader(InputStreamReader(inputStream)).readLine()
            if (response == "test")
                return null
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
        return response
    }
}