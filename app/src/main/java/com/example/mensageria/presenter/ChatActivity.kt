package com.example.mensageria.presenter

import DatastoreRepository
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mensageria.R
import com.example.mensageria.data.MessageRepository
import com.example.mensageria.data.model.Phone
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var contactNumber: String
    private lateinit var currentNumber: String

    private val handler = Handler(Looper.getMainLooper())
    private val updateMessagesRunnable = object : Runnable {
        override fun run() {
            updateMessages()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerViewMessages)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        contactNumber = intent.getStringExtra("contact_number") ?: "---"

        lifecycleScope.launch {
            currentNumber = DatastoreRepository.getPhone(this@ChatActivity)
            val messages = MessageRepository().getMessagesByContactNumber(contactNumber,currentNumber)
            adapter = MessageAdapter(this@ChatActivity, messages, contactNumber)
            recyclerView.adapter = adapter
        }

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateMessagesRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateMessagesRunnable)
    }

    private fun sendMessage(messageText: String) {
        lifecycleScope.launch {
            val fromPhone = Phone(DatastoreRepository.getPhone(this@ChatActivity))
            val toPhone = Phone(contactNumber)
            val order: Int = (adapter.itemCount + 1)
            val isSuccess = MessageRepository().writeMessage(fromPhone, toPhone, messageText, order)

            if (isSuccess) {
                messageInput.text.clear()
            } else {
                Toast.makeText(this@ChatActivity, "Erro ao enviar mensagem...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMessages() {
        lifecycleScope.launch {
            val messages = MessageRepository().getMessagesByContactNumber(contactNumber,currentNumber)
            for(msg in messages){
                Log.i("ChatActivity",msg.toString())
            }
            adapter.updateMessages(messages)
        }
    }
}
