package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency;

import static com.example.hisabwalaallinonecalc.utils.Utils.closeKeyboard;
import static com.example.hisabwalaallinonecalc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.BaseFunctionActivity;
import com.example.hisabwalaallinonecalc.utils.TouchAnimation;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Currency Converter Activity
 */
public class CurrencyActivity extends BaseFunctionActivity {

    private Spinner fromCurrencySpinner, toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView, textView, nameFrom, nameTo;
    private CurrencyViewModel currencyViewModel;
    private ShapeableImageView imageViewFrom, imageViewTo;
    private boolean first = true, showEnglishName;

    private static final List<Currency> CURRENCIES = Arrays.asList(
            new Currency(R.drawable.australia, "AUD", "澳大利亚元", "Australian Dollar"),
            new Currency(R.drawable.bulgaria, "BGN", "保加利亚列弗", "Bulgarian Lev"),
            new Currency(R.drawable.brazil, "BRL", "巴西雷亚尔", "Brazilian Real"),
            new Currency(R.drawable.canada, "CAD", "加拿大元", "Canadian Dollar"),
            new Currency(R.drawable.switzerland, "CHF", "瑞士法郎", "Swiss Franc"),
            new Currency(R.drawable.china, "CNY", "人民币", "Chinese Yuan"),
            new Currency(R.drawable.czechia, "CZK", "捷克克朗", "Czech Koruna"),
            new Currency(R.drawable.denmark, "DKK", "丹麦克朗", "Danish Krone"),
            new Currency(R.drawable.eu, "EUR", "欧元", "Euro"),
            new Currency(R.drawable.uk, "GBP", "英镑", "British Pound"),
            new Currency(R.drawable.hongkongchina, "HKD", "港元", "Hong Kong Dollar"),
            new Currency(R.drawable.hungary, "HUF", "匈牙利福林", "Hungarian Forint"),
            new Currency(R.drawable.indonesia, "IDR", "印尼卢比", "Indonesian Rupiah"),
            new Currency(R.drawable.israel, "ILS", "以色列新谢克尔", "Israeli Shekel"),
            new Currency(R.drawable.india, "INR", "印度卢比", "Indian Rupee"),
            new Currency(R.drawable.iceland, "ISK", "冰岛克朗", "Icelandic Króna"),
            new Currency(R.drawable.japan, "JPY", "日元", "Japanese Yen"),
            new Currency(R.drawable.south_korea, "KRW", "韩元", "South Korean Won"),
            new Currency(R.drawable.mexico, "MXN", "墨西哥比索", "Mexican Peso"),
            new Currency(R.drawable.malaysia, "MYR", "马来西亚林吉特", "Malaysian Ringgit"),
            new Currency(R.drawable.norway, "NOK", "挪威克朗", "Norwegian Krone"),
            new Currency(R.drawable.new_zealand, "NZD", "新西兰元", "New Zealand Dollar"),
            new Currency(R.drawable.philippines, "PHP", "菲律宾比索", "Philippine Peso"),
            new Currency(R.drawable.poland, "PLN", "波兰兹罗提", "Polish Zloty"),
            new Currency(R.drawable.romania, "RON", "罗马尼亚列伊", "Romanian Leu"),
            new Currency(R.drawable.sweden, "SEK", "瑞典克朗", "Swedish Krona"),
            new Currency(R.drawable.singapore, "SGD", "新加坡元", "Singapore Dollar"),
            new Currency(R.drawable.thailand, "THB", "泰铢", "Thai Baht"),
            new Currency(R.drawable.turkey, "TRY", "土耳其里拉", "Turkish Lira"),
            new Currency(R.drawable.us, "USD", "美元", "United States Dollar"),
            new Currency(R.drawable.south_africa, "ZAR", "南非兰特", "South African Rand")
    );

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_exchange);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tip) {
            ListView listView = findViewById(R.id.list_view_currency);
            boolean visible = listView.getVisibility() == View.VISIBLE;
            listView.setVisibility(visible ? View.GONE : View.VISIBLE);
            item.setIcon(visible ? R.drawable.tips_off : R.drawable.tips_on);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();

        // Detect language
        showEnglishName = !getResources().getConfiguration().getLocales().get(0).getLanguage().equals("zh");

        setupSpinners();
        setupViewModel();
        setupCurrencyListView();
    }

    private void setupSpinners() {
        ArrayList<String> currencySymbols = new ArrayList<>();
        currencySymbols.add("N/A");
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencySymbols);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(fromAdapter);

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencySymbols);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCurrencySpinner.setAdapter(toAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String symbol = parent.getItemAtPosition(position).toString();
                for (Currency c : CURRENCIES) {
                    if (c.symbol().equals(symbol)) {
                        if (parent == fromCurrencySpinner) {
                            imageViewFrom.setImageResource(c.id());
                            nameFrom.setText(showEnglishName ? c.englishName() : c.chineseName());
                            savePreference("from", position);
                        } else {
                            imageViewTo.setImageResource(c.id());
                            nameTo.setText(showEnglishName ? c.englishName() : c.chineseName());
                            savePreference("to", position);
                        }
                        break;
                    }
                }
                calculateCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };

        fromCurrencySpinner.setOnItemSelectedListener(listener);
        toCurrencySpinner.setOnItemSelectedListener(listener);
    }

    private void savePreference(String key, int value) {
        if (!first) {
            SharedPreferences.Editor editor = defaultSp.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    private void setupViewModel() {
        currencyViewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);
        currencyViewModel.getExchangeRates().observe(this, exchangeRates -> {
            if (exchangeRates == null || exchangeRates.isEmpty()) return;

            List<String> symbols = new ArrayList<>(exchangeRates.keySet());
            symbols.sort(String::compareTo);
            ((ArrayAdapter<String>) fromCurrencySpinner.getAdapter()).clear();
            ((ArrayAdapter<String>) fromCurrencySpinner.getAdapter()).addAll(symbols);
            ((ArrayAdapter<String>) toCurrencySpinner.getAdapter()).clear();
            ((ArrayAdapter<String>) toCurrencySpinner.getAdapter()).addAll(symbols);

            if (first) {
                first = false;
                int from = defaultSp.getInt("from", 0);
                int to = defaultSp.getInt("to", 0);
                fromCurrencySpinner.setSelection(from);
                toCurrencySpinner.setSelection(to);
                updateCurrencyDisplay(from, imageViewFrom, nameFrom);
                updateCurrencyDisplay(to, imageViewTo, nameTo);
            }

            updateExchangeDate();
        });

        currencyViewModel.loadExchangeRates();
    }

    private void setupCurrencyListView() {
        ListView listView = findViewById(R.id.list_view_currency);
        listView.setAdapter(new CurrencyAdapter(this, CURRENCIES, showEnglishName));
    }

    private void updateCurrencyDisplay(int pos, ShapeableImageView imageView, TextView textView) {
        Currency c = CURRENCIES.get(pos);
        imageView.setImageResource(c.id());
        textView.setText(showEnglishName ? c.englishName() : c.chineseName());
    }

    private void updateExchangeDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Central"));
        if (cal.get(Calendar.HOUR_OF_DAY) < 15) cal.add(Calendar.DAY_OF_MONTH, -1);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        textView.setText(getString(R.string.ecb) + "\n" + getString(R.string.updateDate) + " " + year + "-" + month + "-" + day);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUi() {
        resultTextView = findViewById(R.id.result_text_view);
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        textView = findViewById(R.id.textView2);
        imageViewFrom = findViewById(R.id.flag_from);
        imageViewTo = findViewById(R.id.flag_to);
        nameFrom = findViewById(R.id.name_from);
        nameTo = findViewById(R.id.name_to);

        ImageView switchImage = findViewById(R.id.switchCurrency);
        TouchAnimation touchAnimation = new TouchAnimation(switchImage);
        switchImage.setOnTouchListener(touchAnimation);
        switchImage.setOnClickListener(v -> {
            int from = fromCurrencySpinner.getSelectedItemPosition();
            int to = toCurrencySpinner.getSelectedItemPosition();
            fromCurrencySpinner.setSelection(to);
            toCurrencySpinner.setSelection(from);
        });

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateCurrency(); }
        });

        amountEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                amountEditText.clearFocus();
                return true;
            }
            return false;
        });
    }

    public void calculateCurrency() {
        HashMap<String, Double> rates = currencyViewModel.getExchangeRates().getValue();
        if (rates == null || rates.isEmpty()) return;

        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty() || "N/A".equals(amountStr)) {
            resultTextView.setText("");
            return;
        }

        try {
            String from = fromCurrencySpinner.getSelectedItem().toString();
            String to = toCurrencySpinner.getSelectedItem().toString();
            double amount = Double.parseDouble(amountStr);
            Double fromRate = rates.get(from);
            Double toRate = rates.get(to);
            if (fromRate != null && toRate != null) {
                double result = (amount / fromRate) * toRate;
                resultTextView.setText(formatNumberFinance(String.valueOf(result)));
            }
        } catch (Exception e) {
            resultTextView.setText("");
        }
    }
}
