package com.example.hisabwalaallinonecalc.features;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.calculator.Calculator;
import com.example.hisabwalaallinonecalc.main.calculator.CalculatorUtils;
import com.example.hisabwalaallinonecalc.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculator AppWidget Provider
 */
public class MyWidgetProviderCalc1 extends AppWidgetProvider {

    private static final String ACTION_BUTTON_CLICK = "com.example.hisabwalaallinonecalc.BUTTON_CLICK1";
    private static final String PREF_NAME = "widget_content1";
    private static final String KEY_TEXT = "text";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calc1);

            // Set button listeners
            setButtonClickListeners(context, views);

            // Restore saved input if available
            SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String savedInput = sp.getString(KEY_TEXT, "");
            views.setTextViewText(R.id.textViewResult, savedInput);

            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_BUTTON_CLICK.equals(intent.getAction())) {
            String buttonValue = intent.getStringExtra("buttonValue");
            if (buttonValue != null) {
                Log.d("WidgetCalc", "Button Clicked: " + buttonValue);
                updateTextView(context, buttonValue);
            }
        }
    }

    private void setButtonClickListeners(Context context, RemoteViews views) {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonDelete, R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
                R.id.buttonDivide, R.id.buttonDecimal, R.id.buttonEquals, R.id.buttonClean,
                R.id.buttonPercentage, R.id.buttonPower
        };

        String[] buttonSymbols = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "⌫", "+", "-", "×", "÷", ".", "=", "C", "%", "^"
        };

        for (int i = 0; i < buttonIds.length; i++) {
            PendingIntent pi = getButtonClickPendingIntent(context, buttonSymbols[i], i);
            views.setOnClickPendingIntent(buttonIds[i], pi);
        }
    }

    private PendingIntent getButtonClickPendingIntent(Context context, String buttonSymbol, int requestCode) {
        Intent intent = new Intent(context, MyWidgetProviderCalc1.class);
        intent.setAction(ACTION_BUTTON_CLICK);
        intent.putExtra("buttonValue", buttonSymbol);

        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void updateTextView(Context context, String buttonValue) {
        @SuppressLint("RemoteViewLayout")
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calc1);

        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String input = sp.getString(KEY_TEXT, "");

        try {
            switch (buttonValue) {
                case "=":
                    input = CalculatorUtils.optimizePercentage(input);
                    input = input.replace("%", "÷100");

                    // FIX: use BigDecimal properly
                    BigDecimal result = new Calculator(false).calc(input);
                    if (result != null) {
                        input = Utils.removeZeros(result.setScale(10, RoundingMode.HALF_UP).toString());
                    }
                    break;

                case "⌫":
                    if (!input.isEmpty()) input = input.substring(0, input.length() - 1);
                    break;

                case "C":
                    input = "";
                    break;

                default:
                    input += buttonValue;
                    break;
            }
        } catch (Exception e) {
            input = context.getString(R.string.formatError);
        }

        // Save state
        sp.edit().putString(KEY_TEXT, input).apply();

        // Update UI
        views.setTextViewText(R.id.textViewResult, input);
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, MyWidgetProviderCalc1.class), views
        );
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Clear saved state when widget is removed
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        @SuppressLint("RemoteViewLayout")
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calc1);
        setButtonClickListeners(context, views);

        // Restore text
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedInput = sp.getString(KEY_TEXT, "");
        views.setTextViewText(R.id.textViewResult, savedInput);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
