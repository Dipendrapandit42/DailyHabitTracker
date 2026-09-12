package com.example.dailyhabittracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddFoodActivity : AppCompatActivity() {

    private lateinit var etFoodName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var btnSaveFood: Button
    private lateinit var tvNutritionPreview: TextView

    private lateinit var database: HabitDatabase

    /*
     * English + Hindi + Nepali + common local names
     */
    private val foodAliases = mapOf(

        // Rice
        "rice" to "Rice",
        "chawal" to "Rice",
        "चावल" to "Rice",
        "bhat" to "Rice",
        "भात" to "Rice",
        "chamal" to "Rice",
        "चामल" to "Rice",

        // Roti
        "roti" to "Roti",
        "chapati" to "Roti",
        "रोटी" to "Roti",
        "चपाती" to "Roti",

        // Dal
        "dal" to "Dal",
        "daal" to "Dal",
        "दाल" to "Dal",
        "lentil" to "Dal",
        "lentils" to "Dal",

        // Potato
        "potato" to "Potato",
        "aloo" to "Potato",
        "aalu" to "Potato",
        "आलू" to "Potato",
        "आलु" to "Potato",

        // Onion
        "onion" to "Onion",
        "pyaz" to "Onion",
        "pyaaz" to "Onion",
        "प्याज" to "Onion",
        "प्याज़" to "Onion",

        // Tomato
        "tomato" to "Tomato",
        "tamatar" to "Tomato",
        "टमाटर" to "Tomato",
        "golbheda" to "Tomato",
        "गोलभेडा" to "Tomato",

        // Milk
        "milk" to "Milk",
        "dudh" to "Milk",
        "doodh" to "Milk",
        "दूध" to "Milk",
        "दुध" to "Milk",

        // Curd
        "curd" to "Curd",
        "yogurt" to "Curd",
        "dahi" to "Curd",
        "दही" to "Curd",

        // Chicken
        "chicken" to "Chicken",
        "चिकन" to "Chicken",
        "kukura" to "Chicken",
        "कुखुरा" to "Chicken",
        "kukhurako masu" to "Chicken",
        "कुखुराको मासु" to "Chicken",

        // Egg
        "egg" to "Egg",
        "eggs" to "Egg",
        "anda" to "Egg",
        "अंडा" to "Egg",
        "अण्डा" to "Egg",

        // Banana
        "banana" to "Banana",
        "kela" to "Banana",
        "केला" to "Banana",
        "kera" to "Banana",
        "केरा" to "Banana",

        // Apple
        "apple" to "Apple",
        "seb" to "Apple",
        "सेब" to "Apple",
        "syau" to "Apple",
        "स्याउ" to "Apple",

        // Orange
        "orange" to "Orange",
        "santra" to "Orange",
        "संतरा" to "Orange",
        "suntala" to "Orange",
        "सुन्तला" to "Orange",

        // Mango
        "mango" to "Mango",
        "aam" to "Mango",
        "आम" to "Mango",
        "aap" to "Mango",
        "आँप" to "Mango",

        // Guava
        "guava" to "Guava",
        "amrud" to "Guava",
        "अमरूद" to "Guava",
        "amba" to "Guava",
        "अम्बा" to "Guava",

        // Papaya
        "papaya" to "Papaya",
        "papita" to "Papaya",
        "पपीता" to "Papaya",

        // Pomegranate
        "pomegranate" to "Pomegranate",
        "anar" to "Pomegranate",
        "अनार" to "Pomegranate",

        // Watermelon
        "watermelon" to "Watermelon",
        "tarbooj" to "Watermelon",
        "तरबूज" to "Watermelon",
        "tarbuj" to "Watermelon",
        "तरबुजा" to "Watermelon",

        // Cucumber
        "cucumber" to "Cucumber",
        "kheera" to "Cucumber",
        "खीरा" to "Cucumber",
        "kakro" to "Cucumber",
        "काक्रो" to "Cucumber",

        // Carrot
        "carrot" to "Carrot",
        "gajar" to "Carrot",
        "गाजर" to "Carrot",

        // Spinach
        "spinach" to "Spinach",
        "palak" to "Spinach",
        "पालक" to "Spinach",
        "palungo" to "Spinach",
        "पालुंगो" to "Spinach",

        // Mustard Greens
        "mustard greens" to "Mustard Greens",
        "sarson" to "Mustard Greens",
        "सरसों" to "Mustard Greens",
        "raayo" to "Mustard Greens",
        "रायो" to "Mustard Greens",
        "raayoko saag" to "Mustard Greens",
        "रायोको साग" to "Mustard Greens",

        // Cauliflower
        "cauliflower" to "Cauliflower",
        "phool gobi" to "Cauliflower",
        "फूलगोभी" to "Cauliflower",
        "kauli" to "Cauliflower",
        "काउली" to "Cauliflower",

        // Cabbage
        "cabbage" to "Cabbage",
        "patta gobi" to "Cabbage",
        "पत्तागोभी" to "Cabbage",
        "banda" to "Cabbage",
        "बन्दा" to "Cabbage",

        // Green Peas
        "peas" to "Green Peas",
        "green peas" to "Green Peas",
        "matar" to "Green Peas",
        "मटर" to "Green Peas",
        "kerau" to "Green Peas",
        "केराउ" to "Green Peas",

        // Chickpeas
        "chickpeas" to "Chickpeas",
        "chickpea" to "Chickpeas",
        "chana" to "Chickpeas",
        "चना" to "Chickpeas",
        "kabuli chana" to "Chickpeas",
        "काबुली चना" to "Chickpeas",

        // Black Gram
        "black gram" to "Black Gram",
        "urad" to "Black Gram",
        "urad dal" to "Black Gram",
        "उड़द" to "Black Gram",
        "उड़द दाल" to "Black Gram",
        "maas" to "Black Gram",

        // Kidney Beans
        "kidney beans" to "Kidney Beans",
        "rajma" to "Kidney Beans",
        "राजमा" to "Kidney Beans",

        // Soybean
        "soybean" to "Soybean",
        "soybeans" to "Soybean",
        "soy" to "Soybean",
        "सोयाबीन" to "Soybean",
        "bhatmas" to "Soybean",
        "भटमास" to "Soybean",

        // Paneer
        "paneer" to "Paneer",
        "पनीर" to "Paneer",
        "panir" to "Paneer",

        // Tofu
        "tofu" to "Tofu",
        "सोया पनीर" to "Tofu",

        // Peanuts
        "peanut" to "Peanuts",
        "peanuts" to "Peanuts",
        "moongfali" to "Peanuts",
        "मूंगफली" to "Peanuts",

        // Almonds
        "almond" to "Almonds",
        "almonds" to "Almonds",
        "badam" to "Almonds",
        "बादाम" to "Almonds",

        // Cashews
        "cashew" to "Cashews",
        "cashews" to "Cashews",
        "kaju" to "Cashews",
        "काजू" to "Cashews",

        // Walnuts
        "walnut" to "Walnuts",
        "walnuts" to "Walnuts",
        "akhrot" to "Walnuts",
        "अखरोट" to "Walnuts",
        "okhar" to "Walnuts",

        // Oats
        "oats" to "Oats",
        "ओट्स" to "Oats",

        // Bread
        "bread" to "Bread",
        "ब्रेड" to "Bread",

        // Poha
        "poha" to "Poha",
        "पोहा" to "Poha",
        "chiura" to "Poha",
        "चिउरा" to "Poha",

        // Upma
        "upma" to "Upma",
        "उपमा" to "Upma",

        // Idli
        "idli" to "Idli",
        "इडली" to "Idli",

        // Dosa
        "dosa" to "Dosa",
        "डोसा" to "Dosa",

        // Samosa
        "samosa" to "Samosa",
        "समोसा" to "Samosa",

        // Momo
        "momo" to "Momo",
        "momos" to "Momo",
        "मोमो" to "Momo",

        // Thukpa
        "thukpa" to "Thukpa",
        "थुक्पा" to "Thukpa",

        // Dal Bhat
        "dal bhat" to "Dal Bhat",
        "dalbhaat" to "Dal Bhat",
        "दाल भात" to "Dal Bhat",
        "दालभात" to "Dal Bhat",

        // Ghee
        "ghee" to "Ghee",
        "घी" to "Ghee",
        "ghiu" to "Ghee",
        "घिउ" to "Ghee",

        // Mustard Oil
        "mustard oil" to "Mustard Oil",
        "sarso oil" to "Mustard Oil",
        "सरसों का तेल" to "Mustard Oil",
        "tori ko tel" to "Mustard Oil",
        "तोरीको तेल" to "Mustard Oil",

        // Sugar
        "sugar" to "Sugar",
        "chini" to "Sugar",
        "चीनी" to "Sugar",
        "चिनी" to "Sugar",

        // Honey
        "honey" to "Honey",
        "shahad" to "Honey",
        "शहद" to "Honey",
        "mah" to "Honey",
        "मह" to "Honey"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_food)

        etFoodName = findViewById(R.id.etFoodName)
        etQuantity = findViewById(R.id.etQuantity)
        btnSaveFood = findViewById(R.id.btnSaveFood)
        tvNutritionPreview = findViewById(R.id.tvNutritionPreview)

        database = HabitDatabase.getDatabase(this)

        // Save button disabled until database is ready
        btnSaveFood.isEnabled = false

        lifecycleScope.launch {

            addDefaultFoods()

            runOnUiThread {

                btnSaveFood.isEnabled = true

                tvNutritionPreview.text =
                    "Enter food name and quantity to see nutrition 👇"

                setupNutritionPreview()
            }
        }

        btnSaveFood.setOnClickListener {

            val enteredName =
                etFoodName.text.toString().trim()

            val quantity =
                etQuantity.text.toString().toDoubleOrNull()

            if (enteredName.isEmpty()) {

                etFoodName.error = "Enter food name"
                return@setOnClickListener
            }

            if (quantity == null || quantity <= 0) {

                etQuantity.error = "Enter valid quantity"
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val searchName =
                    enteredName.lowercase()

                /*
                 * Check aliases first.
                 */
                val canonicalName =
                    foodAliases[searchName]
                        ?: enteredName

                val foods =
                    database.foodDao().getAllFoods()

                val food =
                    foods.firstOrNull {
                        it.name.equals(
                            canonicalName,
                            ignoreCase = true
                        )
                    }

                if (food == null) {

                    runOnUiThread {

                        Toast.makeText(
                            this@AddFoodActivity,
                            "Food not found. Try another name.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return@launch
                }

                // Nutrition values are stored per 100g
                val multiplier =
                    quantity / 100.0

                val entry =
                    NutritionEntry(
                        foodName = food.name,
                        quantity = quantity,
                        unit = "g",

                        calories =
                            food.calories * multiplier,

                        protein =
                            food.protein * multiplier,

                        carbohydrates =
                            food.carbohydrates * multiplier,

                        fat =
                            food.fat * multiplier,

                        fiber =
                            food.fiber * multiplier,

                        vitaminA =
                            food.vitaminA * multiplier,

                        vitaminC =
                            food.vitaminC * multiplier,

                        calcium =
                            food.calcium * multiplier,

                        iron =
                            food.iron * multiplier,

                        date =
                            LocalDate.now().toString()
                    )

                database.nutritionDao()
                    .insertEntry(entry)

                runOnUiThread {

                    Toast.makeText(
                        this@AddFoodActivity,
                        "${food.name} added successfully ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    /*
     * Live nutrition preview
     */
    private fun setupNutritionPreview() {

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateNutritionPreview()
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        }

        etFoodName.addTextChangedListener(textWatcher)
        etQuantity.addTextChangedListener(textWatcher)
    }

    /*
     * Calculate and display nutrition automatically
     */
    private fun updateNutritionPreview() {

        lifecycleScope.launch {

            val enteredName =
                etFoodName.text.toString().trim()

            val quantity =
                etQuantity.text.toString().toDoubleOrNull()

            if (
                enteredName.isEmpty() ||
                quantity == null ||
                quantity <= 0
            ) {

                runOnUiThread {

                    tvNutritionPreview.text =
                        "Nutrition will be calculated automatically"
                }

                return@launch
            }

            val canonicalName =
                foodAliases[enteredName.lowercase()]
                    ?: enteredName

            val foods =
                database.foodDao().getAllFoods()

            val food =
                foods.firstOrNull {
                    it.name.equals(
                        canonicalName,
                        ignoreCase = true
                    )
                }

            if (food == null) {

                runOnUiThread {

                    tvNutritionPreview.text =
                        "❌ Food not found\n\nTry: Rice, Dal, Roti, Apple, Banana..."
                }

                return@launch
            }

            // All food values are based on 100g
            val multiplier =
                quantity / 100.0

            val calories =
                food.calories * multiplier

            val protein =
                food.protein * multiplier

            val carbohydrates =
                food.carbohydrates * multiplier

            val fat =
                food.fat * multiplier

            val fiber =
                food.fiber * multiplier

            val vitaminA =
                food.vitaminA * multiplier

            val vitaminC =
                food.vitaminC * multiplier

            val calcium =
                food.calcium * multiplier

            val iron =
                food.iron * multiplier

            runOnUiThread {

                tvNutritionPreview.text =
                    """
                    🍎 ${food.name} — ${String.format("%.0f", quantity)} g
                    
                    🔥 Calories: ${calories.toInt()} kcal
                    💪 Protein: ${String.format("%.1f", protein)} g
                    🍚 Carbs: ${String.format("%.1f", carbohydrates)} g
                    🥑 Fat: ${String.format("%.1f", fat)} g
                    🌾 Fiber: ${String.format("%.1f", fiber)} g
                    
                    🍊 Vitamin A: ${String.format("%.1f", vitaminA)} mg
                    🍊 Vitamin C: ${String.format("%.1f", vitaminC)} mg
                    🦴 Calcium: ${String.format("%.1f", calcium)} mg
                    🩸 Iron: ${String.format("%.1f", iron)} mg
                    """.trimIndent()
            }
        }
    }

    private suspend fun addDefaultFoods() {

        val existingFoods =
            database.foodDao().getAllFoods()

        val defaultFoods = listOf(

            FoodItem(
                name = "Rice",
                calories = 130.0,
                protein = 2.7,
                carbohydrates = 28.2,
                fat = 0.3,
                fiber = 0.4,
                calcium = 10.0,
                iron = 0.2
            ),

            FoodItem(
                name = "Milk",
                calories = 61.0,
                protein = 3.2,
                carbohydrates = 4.8,
                fat = 3.3,
                vitaminA = 46.0,
                calcium = 113.0
            ),

            FoodItem(
                name = "Chicken",
                calories = 165.0,
                protein = 31.0,
                fat = 3.6,
                vitaminA = 13.0,
                calcium = 15.0,
                iron = 1.0
            ),

            FoodItem(
                name = "Egg",
                calories = 155.0,
                protein = 13.0,
                carbohydrates = 1.1,
                fat = 11.0,
                vitaminA = 160.0,
                calcium = 50.0,
                iron = 1.2
            ),

            FoodItem(
                name = "Banana",
                calories = 89.0,
                protein = 1.1,
                carbohydrates = 22.8,
                fat = 0.3,
                fiber = 2.6,
                vitaminA = 3.0,
                vitaminC = 8.7,
                calcium = 5.0,
                iron = 0.3
            ),

            FoodItem(
                name = "Roti",
                calories = 297.0,
                protein = 11.0,
                carbohydrates = 55.0,
                fat = 4.0,
                fiber = 11.0,
                iron = 3.0
            ),

            FoodItem(
                name = "Dal",
                calories = 116.0,
                protein = 9.0,
                carbohydrates = 20.0,
                fat = 0.4,
                fiber = 7.9,
                iron = 3.3
            ),

            FoodItem(
                name = "Potato",
                calories = 77.0,
                protein = 2.0,
                carbohydrates = 17.5,
                fat = 0.1,
                fiber = 2.2,
                vitaminC = 19.7
            ),

            FoodItem(
                name = "Onion",
                calories = 40.0,
                protein = 1.1,
                carbohydrates = 9.3,
                fat = 0.1,
                fiber = 1.7,
                vitaminC = 7.4
            ),

            FoodItem(
                name = "Tomato",
                calories = 18.0,
                protein = 0.9,
                carbohydrates = 3.9,
                fat = 0.2,
                fiber = 1.2,
                vitaminA = 42.0,
                vitaminC = 13.7
            ),

            FoodItem(
                name = "Curd",
                calories = 61.0,
                protein = 3.5,
                carbohydrates = 4.7,
                fat = 3.3,
                calcium = 121.0
            ),

            FoodItem(
                name = "Apple",
                calories = 52.0,
                protein = 0.3,
                carbohydrates = 13.8,
                fat = 0.2,
                fiber = 2.4,
                vitaminC = 4.6
            ),

            FoodItem(
                name = "Orange",
                calories = 47.0,
                protein = 0.9,
                carbohydrates = 11.8,
                fat = 0.1,
                fiber = 2.4,
                vitaminC = 53.2
            ),

            FoodItem(
                name = "Mango",
                calories = 60.0,
                protein = 0.8,
                carbohydrates = 15.0,
                fat = 0.4,
                fiber = 1.6,
                vitaminA = 54.0,
                vitaminC = 36.4
            ),

            FoodItem(
                name = "Guava",
                calories = 68.0,
                protein = 2.6,
                carbohydrates = 14.3,
                fat = 1.0,
                fiber = 5.4,
                vitaminC = 228.0
            ),

            FoodItem(
                name = "Papaya",
                calories = 43.0,
                protein = 0.5,
                carbohydrates = 10.8,
                fat = 0.3,
                fiber = 1.7,
                vitaminA = 47.0,
                vitaminC = 60.9
            ),

            FoodItem(
                name = "Pomegranate",
                calories = 83.0,
                protein = 1.7,
                carbohydrates = 18.7,
                fat = 1.2,
                fiber = 4.0,
                vitaminC = 10.2
            ),

            FoodItem(
                name = "Watermelon",
                calories = 30.0,
                protein = 0.6,
                carbohydrates = 7.6,
                fat = 0.2,
                fiber = 0.4,
                vitaminA = 28.0,
                vitaminC = 8.1
            ),

            FoodItem(
                name = "Cucumber",
                calories = 15.0,
                protein = 0.7,
                carbohydrates = 3.6,
                fat = 0.1,
                fiber = 0.5,
                vitaminC = 2.8
            ),

            FoodItem(
                name = "Carrot",
                calories = 41.0,
                protein = 0.9,
                carbohydrates = 9.6,
                fat = 0.2,
                fiber = 2.8,
                vitaminA = 835.0,
                vitaminC = 5.9
            ),

            FoodItem(
                name = "Spinach",
                calories = 23.0,
                protein = 2.9,
                carbohydrates = 3.6,
                fat = 0.4,
                fiber = 2.2,
                vitaminA = 469.0,
                vitaminC = 28.1,
                calcium = 99.0,
                iron = 2.7
            ),

            FoodItem(
                name = "Mustard Greens",
                calories = 27.0,
                protein = 2.9,
                carbohydrates = 4.7,
                fat = 0.4,
                fiber = 3.2,
                vitaminA = 302.0,
                vitaminC = 70.0,
                calcium = 115.0,
                iron = 1.6
            ),

            FoodItem(
                name = "Cauliflower",
                calories = 25.0,
                protein = 1.9,
                carbohydrates = 5.0,
                fat = 0.3,
                fiber = 2.0,
                vitaminC = 48.2,
                calcium = 22.0
            ),

            FoodItem(
                name = "Cabbage",
                calories = 25.0,
                protein = 1.3,
                carbohydrates = 5.8,
                fat = 0.1,
                fiber = 2.5,
                vitaminC = 36.6
            ),

            FoodItem(
                name = "Green Peas",
                calories = 81.0,
                protein = 5.4,
                carbohydrates = 14.5,
                fat = 0.4,
                fiber = 5.7,
                vitaminC = 40.0,
                iron = 1.5
            ),

            FoodItem(
                name = "Chickpeas",
                calories = 164.0,
                protein = 8.9,
                carbohydrates = 27.4,
                fat = 2.6,
                fiber = 7.6,
                iron = 2.9
            ),

            FoodItem(
                name = "Black Gram",
                calories = 341.0,
                protein = 25.0,
                carbohydrates = 59.0,
                fat = 1.6,
                fiber = 18.0,
                iron = 7.6
            ),

            FoodItem(
                name = "Kidney Beans",
                calories = 127.0,
                protein = 8.7,
                carbohydrates = 22.8,
                fat = 0.5,
                fiber = 6.4,
                iron = 2.9
            ),

            FoodItem(
                name = "Soybean",
                calories = 173.0,
                protein = 16.6,
                carbohydrates = 9.9,
                fat = 9.0,
                fiber = 6.0,
                iron = 5.1
            ),

            FoodItem(
                name = "Paneer",
                calories = 265.0,
                protein = 18.3,
                carbohydrates = 6.1,
                fat = 20.8,
                calcium = 208.0
            ),

            FoodItem(
                name = "Tofu",
                calories = 76.0,
                protein = 8.0,
                carbohydrates = 1.9,
                fat = 4.8,
                calcium = 350.0,
                iron = 5.4
            ),

            FoodItem(
                name = "Peanuts",
                calories = 567.0,
                protein = 25.8,
                carbohydrates = 16.1,
                fat = 49.2,
                fiber = 8.5,
                calcium = 92.0,
                iron = 4.6
            ),

            FoodItem(
                name = "Almonds",
                calories = 579.0,
                protein = 21.2,
                carbohydrates = 21.6,
                fat = 49.9,
                fiber = 12.5,
                calcium = 269.0,
                iron = 3.7
            ),

            FoodItem(
                name = "Cashews",
                calories = 553.0,
                protein = 18.2,
                carbohydrates = 30.2,
                fat = 43.8,
                fiber = 3.3,
                calcium = 37.0,
                iron = 6.7
            ),

            FoodItem(
                name = "Walnuts",
                calories = 654.0,
                protein = 15.2,
                carbohydrates = 13.7,
                fat = 65.2,
                fiber = 6.7,
                calcium = 98.0,
                iron = 2.9
            ),

            FoodItem(
                name = "Oats",
                calories = 389.0,
                protein = 16.9,
                carbohydrates = 66.3,
                fat = 6.9,
                fiber = 10.6,
                iron = 4.7
            ),

            FoodItem(
                name = "Bread",
                calories = 265.0,
                protein = 9.0,
                carbohydrates = 49.0,
                fat = 3.2,
                fiber = 2.7,
                iron = 3.6
            ),

            FoodItem(
                name = "Poha",
                calories = 130.0,
                protein = 2.4,
                carbohydrates = 27.0,
                fat = 1.0,
                fiber = 1.5
            ),

            FoodItem(
                name = "Upma",
                calories = 150.0,
                protein = 3.5,
                carbohydrates = 25.0,
                fat = 4.0,
                fiber = 2.0
            ),

            FoodItem(
                name = "Idli",
                calories = 58.0,
                protein = 2.0,
                carbohydrates = 12.0,
                fat = 0.4,
                fiber = 0.8
            ),

            FoodItem(
                name = "Dosa",
                calories = 168.0,
                protein = 3.9,
                carbohydrates = 29.0,
                fat = 3.7,
                fiber = 1.0
            ),

            FoodItem(
                name = "Samosa",
                calories = 262.0,
                protein = 5.0,
                carbohydrates = 31.0,
                fat = 13.0,
                fiber = 2.0
            ),

            FoodItem(
                name = "Momo",
                calories = 200.0,
                protein = 8.0,
                carbohydrates = 25.0,
                fat = 7.0
            ),

            FoodItem(
                name = "Thukpa",
                calories = 90.0,
                protein = 4.0,
                carbohydrates = 12.0,
                fat = 3.0
            ),

            FoodItem(
                name = "Dal Bhat",
                calories = 150.0,
                protein = 5.0,
                carbohydrates = 27.0,
                fat = 2.0,
                fiber = 3.0
            ),

            FoodItem(
                name = "Ghee",
                calories = 900.0,
                fat = 100.0
            ),

            FoodItem(
                name = "Mustard Oil",
                calories = 884.0,
                fat = 100.0
            ),

            FoodItem(
                name = "Sugar",
                calories = 387.0,
                carbohydrates = 100.0
            ),

            FoodItem(
                name = "Honey",
                calories = 304.0,
                protein = 0.3,
                carbohydrates = 82.4
            )
        )

        defaultFoods.forEach { defaultFood ->

            val alreadyExists =
                existingFoods.any {
                    it.name.equals(
                        defaultFood.name,
                        ignoreCase = true
                    )
                }

            if (!alreadyExists) {

                database.foodDao()
                    .insertFood(defaultFood)
            }
        }
    }
}