package com.example.dailyhabittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private var habits: List<Habit>,
    private val onCompleteClick: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvHabitName: TextView =
            itemView.findViewById(R.id.tvHabitName)

        val tvHabitStreak: TextView =
            itemView.findViewById(R.id.tvHabitStreak)

        val tvHabitStatus: TextView =
            itemView.findViewById(R.id.tvHabitStatus)

        val tvMore: TextView =
            itemView.findViewById(R.id.tvMore)

        val btnComplete: Button =
            itemView.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)

        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HabitViewHolder,
        position: Int
    ) {

        val habit = habits[position]

        holder.tvHabitName.text = habit.name

        holder.tvHabitStreak.text =
            "🔥 ${habit.currentStreak}"

        if (habit.isCompletedToday) {

            holder.tvHabitStatus.text =
                "Completed Today ✅"

            holder.btnComplete.text =
                "Completed"

            holder.btnComplete.isEnabled = false

        } else {

            holder.tvHabitStatus.text =
                "Not Completed"

            holder.btnComplete.text =
                "Complete"

            holder.btnComplete.isEnabled = true

            holder.btnComplete.setOnClickListener {
                onCompleteClick(habit)
            }
        }

        // Three-dot menu
        holder.tvMore.setOnClickListener {

            val popupMenu = PopupMenu(
                holder.itemView.context,
                holder.tvMore
            )

            popupMenu.menu.add("✏️ Edit")
            popupMenu.menu.add("🗑️ Delete")

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.title.toString()) {

                    "✏️ Edit" -> {
                        onEditClick(habit)
                        true
                    }

                    "🗑️ Delete" -> {
                        onDeleteClick(habit)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int =
        habits.size

    fun updateHabits(newHabits: List<Habit>) {

        habits = newHabits

        notifyDataSetChanged()
    }
}