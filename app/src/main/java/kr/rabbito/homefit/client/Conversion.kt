package kr.rabbito.homefit.client

val FOOD_CLASSES = arrayListOf("Salad", "Kimchi Fried Rice", "Sundaegukbap", "Jajangmyeon", "Stir-Fried Pork")
val FOOD_NAMES_KR = arrayListOf("샐러드", "김치볶음밥", "순대국밥", "짜장면", "제육볶음")
//val CONVERSION_VALUES = arrayListOf(0.573342, 0.648699, 1.014491, 0.930378, 0.698722)

// 임시로 사용하는 수치 - 실제로는 서버에서 계산되어 들어옴
val CONVERSION_VALUES = arrayListOf((0.573342) / 2, 0.648699, 1.014491, 0.930378, 0.698722)   // 샐러드 임시 조정
val CALORIES = arrayListOf(0.814, 1.49, 1.2, 1.75, 1.91)

val VOLUME = "volume(cm^3)"
val CALORIE = "calorie(kcal)"
val WEIGHT = "weight(g)"
val FAT = "fat(g)"
val CARBOHYDRATE = "carbohydrate(g)"
val PROTEIN = "protein(g)"

fun calcWeight(index: Int, volume: Double): Double {
    val weight = volume * CONVERSION_VALUES[index]

    return weight
}

fun calcCalorie(index: Int, weight: Double): Int {
    val calorie = weight * CALORIES[index]

    return calorie.toInt()
}