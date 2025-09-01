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

import java.math.BigDecimal; // ✅ use this one, not android.icu.math.BigDecimal
import java.math.RoundingMode;

/**
 * Calculator Widget Provider
 */
public class MyWidgetProviderCalc2 extends AppWidgetProvider {

    private static final String ACTION_BUTTON_CLICK = "com.yangdai.calc.BUTTON_CLICK2";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calc2);

            // Set up click listeners for all buttons
            setButtonClickListeners(context, views);

            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null && intent.getAction().equals(ACTION_BUTTON_CLICK)) {
            String buttonValue = intent.getStringExtra("buttonValue");
            if (buttonValue != null) {
                Log.e("WidgetInput", buttonValue);
            }
            updateTextView(context, buttonValue);
        }
    }

    private void setButtonClickListeners(Context context, RemoteViews views) {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonDelete, R.id.buttonAdd, R.id.buttonSubtract,
                R.id.buttonMultiply, R.id.buttonDivide, R.id.buttonDecimal,
                R.id.buttonEquals, R.id.buttonClean, R.id.buttonPercentage, R.id.buttonPower
        };

        String[] buttonSymbols = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫",
                "+", "-", "×", "÷", ".", "=", "C", "%", "^"
        };

        for (int i = 0; i < buttonIds.length; i++) {
            views.setOnClickPendingIntent(
                    buttonIds[i],
                    getButtonClickPendingIntent(context, buttonSymbols[i], i)
            );
        }
    }

    private PendingIntent getButtonClickPendingIntent(Context context, String buttonSymbol, int requestCode) {
        Intent intent = new Intent(context, this.getClass());
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
        RemoteViews views1 = new RemoteViews(context.getPackageName(), R.layout.widget_calc2);
        SharedPreferences sharedPreferences = context.getSharedPreferences("widget_content2", Context.MODE_PRIVATE);

        String input = sharedPreferences.getString("text", "");

        if ("=".equals(buttonValue)) {
            try {
                input = CalculatorUtils.optimizePercentage(input);
                input = input.replace("%", "÷100");

                BigDecimal res = evaluateExpression(input);

                if (res != null) {
                    input = Utils.removeZeros(res.setScale(10, RoundingMode.HALF_UP).toPlainString());
                } else {
                    input = "";
                }
            } catch (Exception e) {
                input = context.getString(R.string.formatError);
            }
        } else if ("⌫".equals(buttonValue)) {
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
            }
        } else if ("C".equals(buttonValue)) {
            input = "";
        } else {
            input = input + buttonValue;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("text", input);
        editor.apply();

        views1.setTextViewText(R.id.textViewResult, input);

        AppWidgetManager.getInstance(context)
                .updateAppWidget(new ComponentName(context, this.getClass()), views1);
    }

    private BigDecimal evaluateExpression(String expression) {
        Calculator calculator = new Calculator(false);
        return calculator.calc(expression);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences sharedPreferences = context.getSharedPreferences("widget_content2", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calc2);
        setButtonClickListeners(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
