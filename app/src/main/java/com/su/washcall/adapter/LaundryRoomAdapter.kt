package com.su.washcall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.su.washcall.R
import com.su.washcall.database.LaundryRoom

class LaundryRoomAdapter(
    private val onItemClicked: (LaundryRoom) -> Unit
) : ListAdapter<LaundryRoom, LaundryRoomAdapter.LaundryRoomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaundryRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laundry_room, parent, false)
        return LaundryRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaundryRoomViewHolder, position: Int) {
        val currentRoom = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentRoom)
        }
        holder.bind(currentRoom)
    }

    class LaundryRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomNameTextView: TextView = itemView.findViewById(R.id.tvRoomName)

        fun bind(room: LaundryRoom) {
            roomNameTextView.text = room.roomName
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LaundryRoom>() {
            override fun areItemsTheSame(oldItem: LaundryRoom, newItem: LaundryRoom): Boolean {
                return oldItem.roomId == newItem.roomId
            }

            override fun areContentsTheSame(oldItem: LaundryRoom, newItem: LaundryRoom): Boolean {
                return oldItem == newItem
            }
        }
    }
}
