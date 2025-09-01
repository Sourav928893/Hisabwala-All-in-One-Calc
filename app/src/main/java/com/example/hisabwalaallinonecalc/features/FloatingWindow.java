package com.example.hisabwalaallinonecalc.features;

import static com.example.hisabwalaallinonecalc.main.calculator.CalculatorUtils.highlightSpecialSymbols;
import static com.example.hisabwalaallinonecalc.utils.Utils.formatNumber;
import static com.example.hisabwalaallinonecalc.utils.Utils.isNumeric;
import static com.example.hisabwalaallinonecalc.utils.Utils.isSymbol;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.MainActivity;
import com.example.hisabwalaallinonecalc.main.calculator.Calculator;
import com.example.hisabwalaallinonecalc.utils.TouchAnimation;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Floating calculator overlay window service
 */
public class FloatingWindow extends Service implements View.OnClickListener {

    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private WindowManager windowManager;
    private TextView inputView, outputView;
    private int left = 0, right = 0;
    private ColorStateList color;
    private boolean isSmallSize = true;
    private float currentAlpha = 1.0f;

    // Buttons used in floating calculator
    private static final int[] BUTTON_IDS = {
            R.id.div, R.id.mul, R.id.sub, R.id.add, R.id.seven,
            R.id.eight, R.id.nine, R.id.brackets, R.id.four, R.id.five,
            R.id.six, R.id.inverse, R.id.delete, R.id.three, R.id.two,
            R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean
    };

    // Overlay type (API check)
    private static final int LAYOUT_TYPE =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();

        // Screen width/height
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int trueWidth, trueHeight;
        if (width >= height) { // landscape
            trueHeight = (int) (height * 0.45f);
            trueWidth = trueHeight * 10 / 16;
        } else { // portrait
            trueWidth = (int) (width * 0.38f);
            trueHeight = (int) (height * 0.32f);
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_layout, null);

        ImageView btMaximize = floatView.findViewById(R.id.open_in_full);
        ImageView btExit = floatView.findViewById(R.id.close_float);
        ImageView btResize = floatView.findViewById(R.id.resizeWindow);
        ImageView btTransparency = floatView.findViewById(R.id.transparency);
        inputView = floatView.findViewById(R.id.edit);
        outputView = floatView.findViewById(R.id.view);
        color = outputView.getTextColors();

        // Update text color when result changes
        outputView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.equals(getString(R.string.formatError)) || text.equals(getString(R.string.bigNum))) {
                    outputView.setTextColor(getColor(R.color.wrong));
                } else {
                    outputView.setTextColor(color);
                }
            }
        });

        // Attach listeners to buttons
        for (int buttonId : BUTTON_IDS) {
            View btn = floatView.findViewById(buttonId);
            if (btn != null) {
                btn.setOnClickListener(this);
                btn.setOnTouchListener(new TouchAnimation(btn));
            }
        }

        floatWindowLayoutParam = new WindowManager.LayoutParams(
                trueWidth,
                trueHeight,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Center of screen
        floatWindowLayoutParam.gravity = Gravity.CENTER;
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        // Add to window
        windowManager.addView(floatView, floatWindowLayoutParam);

        // Maximize → open MainActivity
        btMaximize.setOnClickListener(v -> {
            Intent backToHome = new Intent(FloatingWindow.this, MainActivity.class);
            backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(backToHome);
            exit();
        });

        // Close button
        btExit.setOnClickListener(v -> exit());

        // Resize
        btResize.setOnClickListener(v -> {
            if (isSmallSize) {
                floatWindowLayoutParam.width = (int) (trueWidth * 1.3f);
                floatWindowLayoutParam.height = (int) (trueHeight * 1.3f);
            } else {
                floatWindowLayoutParam.width = trueWidth;
                floatWindowLayoutParam.height = trueHeight;
            }
            windowManager.updateViewLayout(floatView, floatWindowLayoutParam);
            isSmallSize = !isSmallSize;
        });

        // Transparency
        btTransparency.setOnClickListener(v -> {
            currentAlpha = (currentAlpha == 1.0f) ? 0.6f : 1.0f;
            floatWindowLayoutParam.alpha = currentAlpha;
            windowManager.updateViewLayout(floatView, floatWindowLayoutParam);
        });

        // Drag floating window
        floatView.setOnTouchListener(new View.OnTouchListener() {
            private int initX, initY;
            private float touchX, touchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = floatWindowLayoutParam.x;
                        initY = floatWindowLayoutParam.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutParam.x = initX + (int) (event.getRawX() - touchX);
                        floatWindowLayoutParam.y = initY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(floatView, floatWindowLayoutParam);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exit();
    }

    private void exit() {
        try {
            if (floatView != null && windowManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && floatView.isAttachedToWindow()) {
                    windowManager.removeView(floatView);
                } else {
                    windowManager.removeViewImmediate(floatView);
                }
            }
        } catch (Exception ignored) {}
        stopSelf();
    }

    @Override
    public void onClick(View v) {
        String inputStr = inputView.getText().toString();
        Calculator formulaUtil = new Calculator(false);

        try {
            if (v.getId() == R.id.equal && !inputStr.isEmpty()) {
                handleEqualButton(inputStr, formulaUtil, true);
            } else if (v.getId() == R.id.Clean) {
                handleCleanButton();
            } else if (v.getId() == R.id.delete) {
                handleDeleteButton(inputStr);
            } else if (v.getId() == R.id.brackets) {
                handleBracketsButton(inputStr);
            } else if (v.getId() == R.id.inverse) {
                handleInverseButton(inputStr);
            } else {
                handleOtherButtons(v, inputStr);
            }
            String newInput = inputView.getText().toString();
            highlightSpecialSymbols(inputView);
            if (!newInput.isEmpty()) {
                handleEqualButton(newInput, new Calculator(false), false);
            }
        } catch (Exception e) {
            outputView.setText("");
        }
    }

    /** ✅ Fixed delete button */
    private void handleDeleteButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            char last = inputStr.charAt(inputStr.length() - 1);
            if (last == '(') left--;
            if (last == ')') right--;
            inputView.setText(inputStr.substring(0, inputStr.length() - 1));
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleEqualButton(String inputStr, Calculator formulaUtil, boolean clicked) {
        if (inputStr.isEmpty() || isNumeric(inputStr)) {
            outputView.setText("");
            return;
        }

        if (isSymbol(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
            inputStr += "0";
        } else if (isSymbol(String.valueOf(inputStr.charAt(0))) || inputStr.charAt(0) == '%') {
            inputStr = "0" + inputStr;
        }

        if (left != right) {
            int addCount = Math.abs(left - right);
            StringBuilder builder = new StringBuilder(inputStr);
            for (int j = 0; j < addCount; j++) {
                builder.append(")");
            }
            inputStr = builder.toString();
        }

        try {
            // ✅ FIXED: no more .toBigDecimal()
            Object result = formulaUtil.calc(inputStr);

            BigDecimal bigDecimal;
            if (result instanceof BigDecimal) {
                bigDecimal = (BigDecimal) result;
            } else if (result instanceof String) {
                bigDecimal = new BigDecimal((String) result);
            } else if (result instanceof Number) {
                bigDecimal = BigDecimal.valueOf(((Number) result).doubleValue());
            } else {
                outputView.setText(getString(R.string.formatError));
                return;
            }

            bigDecimal = bigDecimal.setScale(10, RoundingMode.HALF_UP);
            String res = bigDecimal.stripTrailingZeros().toPlainString();

            if (clicked) {
                inputView.setText(res);
                outputView.setText("");
            } else {
                outputView.setText(formatNumber(res));
            }
        } catch (Exception e) {
            if (clicked) {
                outputView.setText(getString(R.string.formatError));
            }
        }
    }


    private void handleCleanButton() {
        inputView.setText("");
        outputView.setText("");
        left = 0;
        right = 0;
    }

    /** TODO: Implement these */
    private void handleBracketsButton(String inputStr) {
        // Add logic for bracket handling and update left/right counts
    }

    private void handleInverseButton(String inputStr) {
        // Add logic for inverse (1/x) handling
    }

    private void handleOtherButtons(View v, String inputStr) {
        // Add logic for general button presses
        if (v instanceof MaterialButton) {
            String text = ((MaterialButton) v).getText().toString();
            inputView.append(text);
            if ("(".equals(text)) left++;
            if (")".equals(text)) right++;
        }
    }
}
