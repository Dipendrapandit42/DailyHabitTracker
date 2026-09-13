package com.example.dailyhabittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onEditClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvReminderTime: TextView =
            itemView.findViewById(R.id.tvReminderTime)

        val tvReminderMessage: TextView =
            itemView.findViewById(R.id.tvReminderMessage)

        val tvReminderMore: TextView =
            itemView.findViewById(R.id.tvReminderMore)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_reminder,
                    parent,
                    false
                )

        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReminderViewHolder,
        position: Int
    ) {

        val reminder = reminders[position]

        holder.tvReminderTime.text =
            "⏰ ${formatTime(reminder.time)}"

        holder.tvReminderMessage.text =
            reminder.message

        holder.tvReminderMore.setOnClickListener {

            val popupMenu =
                PopupMenu(
                    holder.itemView.context,
                    holder.tvReminderMore
                )

            popupMenu.menu.add("✏️ Edit")
            popupMenu.menu.add("🗑️ Delete")

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.title.toString()) {

                    "✏️ Edit" -> {
                        onEditClick(reminder)
                        true
                    }

                    "🗑️ Delete" -> {
                        onDeleteClick(reminder)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    fun updateReminders(
        newReminders: List<Reminder>
    ) {

        reminders = newReminders

        notifyDataSetChanged()
    }

    private fun formatTime(
        time: String
    ): String {

        val parts = time.split(":")

        if (parts.size != 2) {
            return time
        }

        val hour =
            parts[0].toIntOrNull()
                ?: return time

        val minute =
            parts[1].toIntOrNull()
                ?: return time

        val amPm =
            if (hour >= 12) {
                "PM"
            } else {
                "AM"
            }

        val displayHour =
            when {

                hour == 0 -> 12

                hour > 12 -> hour - 12

                else -> hour
            }

        return String.format(
            "%02d:%02d %s",
            displayHour,
            minute,
            amPm
        )
    }
}