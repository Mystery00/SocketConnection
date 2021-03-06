package com

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ServerThread(private val client: Client, private val list: List<Client>) : Runnable
{
    override fun run()
    {
        try
        {
            while (true)
            {
                if (client.isClosed)
                    break
                val message = receiveMessage(client) ?: continue
                val index = message[0]
                when (index)
                {
                    '1' ->//正常消息
                        list.filter { it != client && !it.isClosed && it.socket.isConnected }
                                .forEach {
                                    sendMessage(it, client.id + ":" + message.substring(1))
                                }
                    '2' ->//获取当前在线人员列表
                    {
                        var response = "{\"code\":$index,\"number\":" + list.size + ",\"people\":["
                        list.forEachIndexed { i, client -> response += ("{\"id\":\"" + client.id + "\"}" + if (i != list.size - 1) "," else "") }
                        response += "]}"
                        sendMessage(client, response)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun sendMessage(client: Client, message: String): Boolean
    {
        return try
        {
            val socket = client.socket
            val outputStream = socket.getOutputStream()
            outputStream.write((message + "\n").toByteArray())
            outputStream.flush()
            println("服务端发送消息到" + client.id + "，消息内容：" + message)
            true
        }
        catch (e: IOException)
        {
            e.printStackTrace()
            false
        }
    }

    private fun receiveMessage(client: Client): String?
    {
        val socket = client.socket
        var response: String? = null
        try
        {
            val inputStream = socket.getInputStream()
            response = BufferedReader(InputStreamReader(inputStream)).readLine()
            if (response != null)
                println("接收到来自" + client.id + "的消息：" + response)
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
        return response
    }
}