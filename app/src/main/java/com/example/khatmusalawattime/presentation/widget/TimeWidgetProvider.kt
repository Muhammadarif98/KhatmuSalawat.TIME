package com.example.khatmusalawattime.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import com.example.khatmusalawattime.MainActivity
import com.example.khatmusalawattime.R
import com.example.khatmusalawattime.domain.model.ReminderData
import com.google.gson.Gson
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_time)

            val reminderData = loadReminderData(context)

            val currentDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM"))
            val dayOfWeek = LocalDate.now().dayOfWeek

            val timeText = when (dayOfWeek) {
                DayOfWeek.THURSDAY -> reminderData?.datesKhunzakhSalawat?.get(currentDate) ?: "--:--"
                DayOfWeek.FRIDAY -> ""
                else -> reminderData?.datesKhunzakhHatmu?.get(currentDate) ?: "--:--"
            }

            // Рендерим текст как Bitmap с кастомным шрифтом
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
            val heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 40)

            val bitmap = renderTextBitmap(context, timeText, widthDp, heightDp)
            views.setImageViewBitmap(R.id.widget_time_image, bitmap)

            // Клик по виджету открывает приложение
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun renderTextBitmap(
            context: Context,
            text: String,
            widthDp: Int,
            heightDp: Int
        ): Bitmap {
            val density = context.resources.displayMetrics.density
            val widthPx = (widthDp * density).toInt().coerceAtLeast(1)
            val heightPx = (heightDp * density).toInt().coerceAtLeast(1)

            val typeface: Typeface = ResourcesCompat.getFont(context, R.font.russo_one_regular)
                ?: Typeface.DEFAULT

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.typeface = typeface
                color = 0xFF604D2E.toInt()
                textAlign = Paint.Align.CENTER
            }

            // Подбираем максимальный размер текста, чтобы вписать в виджет
            val padding = (4 * density) // 4dp padding с каждой стороны
            val availableWidth = widthPx - padding * 2
            val availableHeight = heightPx - padding * 2

            var textSizePx = availableHeight * 0.8f // Начинаем с 80% высоты
            val maxTextSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 34f, context.resources.displayMetrics
            )
            textSizePx = textSizePx.coerceAtMost(maxTextSizePx)

            paint.textSize = textSizePx

            // Уменьшаем если текст не помещается по ширине
            if (text.isNotEmpty()) {
                val textWidth = paint.measureText(text)
                if (textWidth > availableWidth) {
                    textSizePx *= (availableWidth / textWidth)
                    paint.textSize = textSizePx
                }
            }

            val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            if (text.isNotEmpty()) {
                val fontMetrics = paint.fontMetrics
                val textHeight = fontMetrics.descent - fontMetrics.ascent
                val y = (heightPx - textHeight) / 2f - fontMetrics.ascent
                canvas.drawText(text, widthPx / 2f, y, paint)
            }

            return bitmap
        }

        private fun loadReminderData(context: Context): ReminderData? {
            return try {
                val jsonString = context.resources.openRawResource(R.raw.reminder_data)
                    .bufferedReader()
                    .use { it.readText() }
                Gson().fromJson(jsonString, ReminderData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
