package com.project.glegleg.ui.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.glegleg.R
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<IntakeLogRecord, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    // Fungsi ini dipanggil untuk membuat ViewHolder baru (baris baru) saat dibutuhkan.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_intake_history, parent, false)
        return HistoryViewHolder(view)
    }

    // Fungsi ini dipanggil untuk mengisi data ke dalam ViewHolder yang sudah ada.
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    // ViewHolder merepresentasikan satu baris di dalam RecyclerView.
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        // Siapkan format tanggal yang kita inginkan
        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        // Fungsi untuk "mengikat" data dari record ke dalam elemen UI.
        fun bind(record: IntakeLogRecord) {
            tvAmount.text = "${record.amountMl} ml"
            tvTimestamp.text = dateFormat.format(Date(record.timestamp))
        }
    }
}

// DiffUtil membantu RecyclerView mengetahui perubahan data secara efisien.
class HistoryDiffCallback : DiffUtil.ItemCallback<IntakeLogRecord>() {
    override fun areItemsTheSame(oldItem: IntakeLogRecord, newItem: IntakeLogRecord): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: IntakeLogRecord, newItem: IntakeLogRecord): Boolean {
        return oldItem == newItem
    }
}