package com.example.psico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.psico.R

class ChatAdapter(private val chatMessages: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER = 0
        const val VIEW_TYPE_BOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].startsWith("Tú:")) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_user_message // Este debe existir también
        } else {
            R.layout.item_bot_message // Esto es lo que estás verificando
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatViewHolder).messageTextView.text = chatMessages[position].substringAfter(": ")

    }

    override fun getItemCount(): Int = chatMessages.size

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
    }
}
