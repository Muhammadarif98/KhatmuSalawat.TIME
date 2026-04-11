package com.example.khatmusalawattime.domain.model

/**
 * Элемент зикра с названием и целевым количеством
 */
data class ZikrItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val arabicText: String = "",
    val targetCount: Int,
    val color: Long = 0xFFE8D8B1
)

/**
 * Тип режима счётчика для сериализации
 */
enum class CounterModeType {
    FREE, AZKAR, WIRD, CUSTOM
}

/**
 * Режим счётчика
 */
sealed class CounterMode {
    abstract val type: CounterModeType

    /**
     * Свободный режим — бесконечный счётчик без ограничений
     */
    data object Free : CounterMode() {
        override val type = CounterModeType.FREE
    }

    /**
     * Режим азкаров — фиксированная последовательность:
     * Истигъфар (10) → Субх1аналлагь (33) → Альх1амдулиллагь (33) → Аллагьу акбар (34)
     */
    data object Azkar : CounterMode() {
        override val type = CounterModeType.AZKAR
    }

    /**
     * Режим вирдов — три зикра по X раз каждый
     * @param countPerZikr количество повторений каждого зикра (100, 300, 500, 700)
     */
    data class Wird(val countPerZikr: Int) : CounterMode() {
        override val type = CounterModeType.WIRD
    }

    /**
     * Кастомный режим — пользователь сам задаёт зикры
     */
    data class Custom(val items: List<ZikrItem>) : CounterMode() {
        override val type = CounterModeType.CUSTOM
    }
}

/**
 * Состояние счётчика
 */
data class CounterState(
    val mode: CounterMode = CounterMode.Free,
    val currentZikrIndex: Int = 0,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val zikrItems: List<ZikrItem> = emptyList()
) {
    val currentZikr: ZikrItem?
        get() = zikrItems.getOrNull(currentZikrIndex)

    val totalProgress: Float
        get() {
            if (zikrItems.isEmpty()) return 0f
            val completedCount = zikrItems.take(currentZikrIndex).sumOf { it.targetCount }
            val totalCount = zikrItems.sumOf { it.targetCount }
            return if (totalCount > 0) {
                (completedCount + currentCount).toFloat() / totalCount
            } else 0f
        }

    val currentZikrProgress: Float
        get() {
            val target = currentZikr?.targetCount ?: return 0f
            return if (target > 0) currentCount.toFloat() / target else 0f
        }

    val totalZikrCount: Int
        get() = zikrItems.sumOf { it.targetCount }

    val completedTotalCount: Int
        get() {
            val completedCount = zikrItems.take(currentZikrIndex).sumOf { it.targetCount }
            return completedCount + currentCount
        }
}

/**
 * Предустановленные зикры для режима Азкаров
 */
object AzkarPreset {
    val items = listOf(
        ZikrItem(
            id = "azkar_istighfar",
            name = "ИСТИГЪФАР",
            arabicText = "أَسْتَغْفِرُ اللهَ",
            targetCount = 10,
            color = 0xFFE57373 // красноватый
        ),
        ZikrItem(
            id = "azkar_subhanallah",
            name = "СУБХ1АНАЛЛАГЬ",
            arabicText = "سُبْحَانَ اللهِ",
            targetCount = 33,
            color = 0xFF81C784 // зеленоватый
        ),
        ZikrItem(
            id = "azkar_alhamdulillah",
            name = "АЛЬХ1АМДУЛИЛЛАГЬ",
            arabicText = "الْحَمْدُ لِلَّهِ",
            targetCount = 33,
            color = 0xFF64B5F6 // голубоватый
        ),
        ZikrItem(
            id = "azkar_allahuakbar",
            name = "АЛЛАГЬУ АКБАР",
            arabicText = "اللهُ أَكْبَرُ",
            targetCount = 34,
            color = 0xFFBA68C8 // фиолетовый
        )
    )
}

/**
 * Предустановленные зикры для режима Вирдов
 */
object WirdPreset {
    fun getItems(countPerZikr: Int) = listOf(
        ZikrItem(
            id = "wird_istighfar",
            name = "ИСТИГЪФАР",
            arabicText = "أَسْتَغْفِرُ اللهَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيَّ الْقَيُّومَ وَأَتُوبُ إِلَيْهِ",
            targetCount = countPerZikr,
            color = 0xFFFF8A65 // оранжевый
        ),
        ZikrItem(
            id = "wird_salavat",
            name = "САЛАВАТ",
            arabicText = "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَنَبِيِّكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ",
            targetCount = countPerZikr,
            color = 0xFF81C784 // зелёный
        ),
        ZikrItem(
            id = "wird_tahlil",
            name = "ЛЯЯ ИЛЯЯГЬА ИЛЛАЛЛААГЬ",
            arabicText = "لَا إِلَهَ إِلَّا اللهُ",
            targetCount = countPerZikr,
            color = 0xFF9575CD // фиолетовый
        )
    )

    val availableCounts = listOf(100, 300, 500, 700)
}
