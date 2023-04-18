package kr.rabbito.homefit.client

val FOOD_CLASSES = arrayListOf("Salad", "Kimchi Fried Rice", "Sundaegukbap", "Jajangmyeon", "Stir-Fried Pork")
val FOOD_NAMES_KR = arrayListOf("샐러드", "김치볶음밥", "순대국밥", "짜장면", "제육볶음")
val CONVERSION_VALUES = arrayListOf(0.573342, 0.648699, 1.014491, 0.930378, 0.698722)
val CALORIES = arrayListOf(0.814, 1.49, 1.2, 1.75, 1.91)

fun calcWeight(index: Int, volume: Int): Double {
    val weight = volume * CONVERSION_VALUES[index]

    return weight
}

fun calcCalorie(index: Int, weight: Double): Int {
    val calorie = weight * CALORIES[index]

    return calorie.toInt()
}