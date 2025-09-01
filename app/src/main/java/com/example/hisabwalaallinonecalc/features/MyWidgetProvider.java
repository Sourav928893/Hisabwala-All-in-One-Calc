package com.example.hisabwalaallinonecalc.features;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.MainActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.StatisticsActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.compass.Compass;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.converter.UnitActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency.CurrencyActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.finance.FinanceActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.shopping.ShoppingActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.time.DateRangeActivity;

/**
 * App Widget for quick access to calculator features.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    // Unique widget action identifiers
    private static final String ACTION_BUTTON_0 = "com.example.hisabwalaallinonecalc.ACTION_CALCULATOR";
    private static final String ACTION_BUTTON_1 = "com.example.hisabwalaallinonecalc.ACTION_STATISTICS";
    private static final String ACTION_BUTTON_2 = "com.example.hisabwalaallinonecalc.ACTION_UNIT";
    private static final String ACTION_BUTTON_3 = "com.example.hisabwalaallinonecalc.ACTION_CURRENCY";
    private static final String ACTION_BUTTON_4 = "com.example.hisabwalaallinonecalc.ACTION_DATERANGE";
    private static final String ACTION_BUTTON_5 = "com.example.hisabwalaallinonecalc.ACTION_SHOPPING";
    private static final String ACTION_BUTTON_6 = "com.example.hisabwalaallinonecalc.ACTION_COMPASS";
    private static final String ACTION_BUTTON_7 = "com.example.hisabwalaallinonecalc.ACTION_FINANCE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Attach broadcast PendingIntents for each button
            setOnClickPendingIntent(context, views, R.id.calculator, ACTION_BUTTON_0);
            setOnClickPendingIntent(context, views, R.id.button1, ACTION_BUTTON_1);
            setOnClickPendingIntent(context, views, R.id.button2, ACTION_BUTTON_2);
            setOnClickPendingIntent(context, views, R.id.button3, ACTION_BUTTON_3);
            setOnClickPendingIntent(context, views, R.id.button4, ACTION_BUTTON_4);
            setOnClickPendingIntent(context, views, R.id.button5, ACTION_BUTTON_5);
            setOnClickPendingIntent(context, views, R.id.button6, ACTION_BUTTON_6);
            setOnClickPendingIntent(context, views, R.id.button7, ACTION_BUTTON_7);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action == null) return;

        switch (action) {
            case ACTION_BUTTON_0:
                startNewTaskActivity(context, MainActivity.class);
                break;
            case ACTION_BUTTON_1:
                startNewTaskActivity(context, StatisticsActivity.class);
                break;
            case ACTION_BUTTON_2:
                startNewTaskActivity(context, UnitActivity.class);
                break;
            case ACTION_BUTTON_3:
                startNewTaskActivity(context, CurrencyActivity.class);
                break;
            case ACTION_BUTTON_4:
                startNewTaskActivity(context, DateRangeActivity.class);
                break;
            case ACTION_BUTTON_5:
                startNewTaskActivity(context, ShoppingActivity.class);
                break;
            case ACTION_BUTTON_6:
                startNewTaskActivity(context, Compass.class);
                break;
            case ACTION_BUTTON_7:
                startNewTaskActivity(context, FinanceActivity.class);
                break;
        }
    }


    private void setOnClickPendingIntent(Context context, RemoteViews views, int viewId, String action) {
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                action.hashCode(),  // unique request code per action
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        views.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private void startNewTaskActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
