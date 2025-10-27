package com.su.washcall.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.su.washcall.R
import com.su.washcall.database.WashingMachine

class WashingMachineAdapter(private val onItemClicked: (WashingMachine) -> Unit) : ListAdapter<WashingMachine, WashingMachineAdapter.WashingMachineViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WashingMachineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_washing_machine, parent, false)
        return WashingMachineViewHolder(view)
    }

    override fun onBindViewHolder(holder: WashingMachineViewHolder, position: Int) {
        val currentMachine = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentMachine)
        }
        holder.bind(currentMachine)
    }

    class WashingMachineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val machineNameTextView: TextView = itemView.findViewById(R.id.tvMachineName)
        private val machineStatusTextView: TextView = itemView.findViewById(R.id.tvMachineStatus)
        private val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)

        fun bind(machine: WashingMachine) {
            machineNameTextView.text = machine.machineName
            machineStatusTextView.text = machine.status

            // 상태에 따라 색상 변경
            when (machine.status.lowercase()) {
                "available", "사용 가능" -> statusIndicator.setBackgroundColor(Color.GREEN)
                "running", "세탁중" -> statusIndicator.setBackgroundColor(Color.BLUE)
                "error", "고장" -> statusIndicator.setBackgroundColor(Color.RED)
                else -> statusIndicator.setBackgroundColor(Color.GRAY)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<WashingMachine>() {
            override fun areItemsTheSame(oldItem: WashingMachine, newItem: WashingMachine): Boolean {
                return oldItem.machineId == newItem.machineId
            }

            override fun areContentsTheSame(oldItem: WashingMachine, newItem: WashingMachine): Boolean {
                return oldItem == newItem
            }
        }
    }
}
