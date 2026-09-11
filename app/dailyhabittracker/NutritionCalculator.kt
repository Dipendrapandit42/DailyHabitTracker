package com.example.dailyhabittracker

object NutritionCalculator {

    // Estimated daily calorie requirement
    fun calculateCalories(profile: UserProfile): Double {

        val bmr = if (profile.gender.equals("Male", ignoreCase = true)) {
            (10 * profile.weightKg) +
                    (6.25 * profile.heightCm) -
                    (5 * profile.age) + 5
        } else {
            (10 * profile.weightKg) +
                    (6.25 * profile.heightCm) -
                    (5 * profile.age) - 161
        }

        val activityMultiplier = when {
            profile.activityLevel.equals("Low", ignoreCase = true) -> 1.2
            profile.activityLevel.equals("Moderate", ignoreCase = true) -> 1.55
            profile.activityLevel.equals("High", ignoreCase = true) -> 1.725
            else -> 1.2
        }

        var calories = bmr * activityMultiplier

        // Goal adjustment
        calories = when {
            profile.goal.equals("Gain", ignoreCase = true) ->
                calories + 300

            profile.goal.equals("Lose", ignoreCase = true) ->
                calories - 300

            else ->
                calories
        }

        return calories
    }

    // Estimated daily protein requirement
    fun calculateProtein(profile: UserProfile): Double {

        val proteinPerKg = when {
            profile.goal.equals("Gain", ignoreCase = true) -> 1.6
            profile.activityLevel.equals("High", ignoreCase = true) -> 1.6
            profile.activityLevel.equals("Moderate", ignoreCase = true) -> 1.4
            else -> 1.2
        }

        return profile.weightKg * proteinPerKg
    }
}