package com.example.mensageria.presenter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mensageria.R
import com.example.mensageria.data.model.Mensage

class MessageAdapter(
    private val context: Context,
    private var messages: List<Mensage>,
    private val contactNumber: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val sortedMessages: List<Mensage>
        get() = messages.sortedBy { it.order }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = sortedMessages[position]
        holder.messageText.text = message.msg

        val layoutParams = holder.messageText.layoutParams as ConstraintLayout.LayoutParams

        if (message.from.getPhone() == contactNumber) {
            Log.i("onBindViewHolder","Left " + message.toString())

            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            layoutParams.setMargins(dpToPx(16), dpToPx(8), dpToPx(80), dpToPx(8))
        } else {
            Log.i("onBindViewHolder","Right " + message.toString())

            layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.setMargins(dpToPx(80), dpToPx(8), dpToPx(16), dpToPx(8))
        }

        holder.messageText.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return sortedMessages.size
    }

    fun updateMessages(newMessages: List<Mensage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Int): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics).toInt()
    }
}
