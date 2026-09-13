package com.example.dailyhabittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(
    private var exercises: List<Exercise>,
    private val onCompleteClick: (Exercise) -> Unit,
    private val onEditClick: (Exercise) -> Unit,
    private val onDeleteClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvExerciseName: TextView =
            itemView.findViewById(R.id.tvExerciseName)

        val tvExerciseDuration: TextView =
            itemView.findViewById(R.id.tvExerciseDuration)

        val tvExerciseCalories: TextView =
            itemView.findViewById(R.id.tvExerciseCalories)

        val btnCompleteExercise: Button =
            itemView.findViewById(R.id.btnCompleteExercise)

        val btnEditExercise: Button =
            itemView.findViewById(R.id.btnEditExercise)

        val btnDeleteExercise: Button =
            itemView.findViewById(R.id.btnDeleteExercise)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExerciseViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)

        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ExerciseViewHolder,
        position: Int
    ) {

        val exercise = exercises[position]

        holder.tvExerciseName.text =
            "🏃 ${exercise.name}"

        holder.tvExerciseDuration.text =
            "⏱️ ${exercise.durationMinutes} minutes"

        holder.tvExerciseCalories.text =
            "🔥 ${exercise.caloriesBurned.toInt()} kcal"

        if (exercise.isCompleted) {

            holder.btnCompleteExercise.text =
                "Completed ✅"

            holder.btnCompleteExercise.isEnabled =
                false

        } else {

            holder.btnCompleteExercise.text =
                "✅ Complete Exercise"

            holder.btnCompleteExercise.isEnabled =
                true

            holder.btnCompleteExercise.setOnClickListener {
                onCompleteClick(exercise)
            }
        }

        holder.btnEditExercise.setOnClickListener {
            onEditClick(exercise)
        }

        holder.btnDeleteExercise.setOnClickListener {
            onDeleteClick(exercise)
        }
    }

    override fun getItemCount(): Int =
        exercises.size

    fun updateExercises(
        newExercises: List<Exercise>
    ) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}