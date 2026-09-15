package com.example.dailyhabittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

        val ivHabitImage: ImageView =
            itemView.findViewById(R.id.ivHabitImage)

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

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_habit,
                    parent,
                    false
                )

        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HabitViewHolder,
        position: Int
    ) {

        val habit = habits[position]

        // Habit name
        holder.tvHabitName.text =
            habit.name

        // Habit streak
        holder.tvHabitStreak.text =
            "🔥 ${habit.currentStreak}"

        // Automatic habit image
        holder.ivHabitImage.setImageResource(
            getHabitImage(habit.name)
        )

        // Completion status
        if (habit.isCompletedToday) {

            holder.tvHabitStatus.text =
                "Completed Today ✅"

            holder.btnComplete.text =
                "Completed"

            holder.btnComplete.isEnabled =
                false

            holder.btnComplete.setOnClickListener(
                null
            )

        } else {

            holder.tvHabitStatus.text =
                "Not Completed"

            holder.btnComplete.text =
                "✅ Complete"

            holder.btnComplete.isEnabled =
                true

            holder.btnComplete.setOnClickListener {

                onCompleteClick(habit)
            }
        }

        // Three-dot menu
        holder.tvMore.setOnClickListener {

            val popupMenu =
                PopupMenu(
                    holder.itemView.context,
                    holder.tvMore
                )

            popupMenu.menu.add(
                "✏️ Edit"
            )

            popupMenu.menu.add(
                "🗑️ Delete"
            )

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (
                    menuItem.title.toString()
                ) {

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

    override fun getItemCount(): Int {
        return habits.size
    }

    fun updateHabits(
        newHabits: List<Habit>
    ) {

        habits = newHabits

        notifyDataSetChanged()
    }

    // -----------------------------------------
    // AUTOMATIC HABIT IMAGE
    // -----------------------------------------

    private fun getHabitImage(
        habitName: String
    ): Int {

        val name =
            habitName
                .trim()
                .lowercase()

        return when {

            // Study
            name.contains("study") ||
                    name.contains("stud") ||
                    name.contains("homework") ||
                    name.contains("class") ||
                    name.contains("learn") ->
                R.drawable.habit_study

            // Exercise / Running
            name.contains("exercise") ||
                    name.contains("running") ||
                    name.contains("run") ||
                    name.contains("walk") ||
                    name.contains("jog") ->
                R.drawable.habit_exercise

            // Reading
            name.contains("read") ||
                    name.contains("book") ->
                R.drawable.habit_reading

            // Water
            name.contains("water") ||
                    name.contains("drink") ||
                    name.contains("hydration") ->
                R.drawable.habit_water

            // Sleep
            name.contains("sleep") ||
                    name.contains("rest") ->
                R.drawable.habit_sleep

            // Meditation
            name.contains("meditation") ||
                    name.contains("meditate") ||
                    name.contains("yoga") ->
                R.drawable.habit_meditation

            // Food
            name.contains("food") ||
                    name.contains("eat") ||
                    name.contains("meal") ||
                    name.contains("diet") ->
                R.drawable.habit_food

            // Gym
            name.contains("gym") ||
                    name.contains("workout") ||
                    name.contains("weights") ->
                R.drawable.habit_gym

            // Health
            name.contains("health") ||
                    name.contains("medicine") ||
                    name.contains("doctor") ->
                R.drawable.habit_health

            // Photography
            name.contains("photo") ||
                    name.contains("photography") ||
                    name.contains("camera") ->
                R.drawable.habit_photography

            // Hobby
            name.contains("hobby") ||
                    name.contains("painting") ||
                    name.contains("music") ||
                    name.contains("drawing") ->
                R.drawable.habit_hobby

            // Morning
            name.contains("morning") ||
                    name.contains("wake") ->
                R.drawable.habit_morning

            // Night
            name.contains("night") ||
                    name.contains("moon") ->
                R.drawable.habit_night

            // Growth
            name.contains("growth") ||
                    name.contains("self improvement") ->
                R.drawable.habit_growth

            // Goal
            name.contains("goal") ||
                    name.contains("target") ->
                R.drawable.habit_goal

            // Default
            else ->
                R.drawable.habit_default
        }
    }
}