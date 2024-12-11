package com.example.psico
import DialogflowService
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var dialogflowService: DialogflowService
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private val chatMessages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializa el servicio de Dialogflow
        dialogflowService = DialogflowService(this)

        // Configura las vistas
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Configura el RecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.adapter = chatAdapter

        // Configura el botón de enviar mensaje
        sendButton.setOnClickListener {
            val userMessage = messageEditText.text.toString()
            if (userMessage.isNotBlank()) {
                // Añade el mensaje del usuario a la lista y actualiza el RecyclerView
                chatMessages.add("Tú: $userMessage")
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                messageEditText.text.clear()

                // Envía el mensaje a Dialogflow
                sendMessageToDialogflow(userMessage, chatAdapter)
            }
        }
    }

    private fun sendMessageToDialogflow(message: String, chatAdapter: ChatAdapter) {
        lifecycleScope.launch {
            try {
                val sessionId = "unique_session_id"
                val fulfillmentText = dialogflowService.sendMessageToDialogflow(sessionId, message)
                chatMessages.add("Bot: $fulfillmentText")

                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DialogflowError", "Error al enviar mensaje a Dialogflow", e)
            }
        }
    }
}