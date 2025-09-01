package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency;

import android.util.Log;
import android.util.Xml;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for fetching and storing currency exchange rates from ECB.
 * Provides LiveData for observing exchange rate updates.
 *
 * @author 30415
 */
public class CurrencyViewModel extends ViewModel {

    private static final String TAG = "CurrencyViewModel";
    private static final String ECB_API_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private final MutableLiveData<HashMap<String, Double>> exchangeRates = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Returns LiveData containing the latest exchange rates.
     * Loads data automatically if not already loaded.
     */
    public LiveData<HashMap<String, Double>> getExchangeRates() {
        if (exchangeRates.getValue() == null) {
            loadExchangeRates();
        }
        return exchangeRates;
    }

    /**
     * Loads the latest exchange rates from ECB asynchronously.
     */
    public void loadExchangeRates() {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(ECB_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                inputStream = connection.getInputStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);

                HashMap<String, Double> ratesMap = parseXml(parser);

                // Post results to LiveData
                exchangeRates.postValue(ratesMap);

            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Failed to load exchange rates", e);
                exchangeRates.postValue(null);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) { }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    /**
     * Parses ECB XML data to extract currency exchange rates.
     *
     * @param parser XmlPullParser initialized with ECB XML input
     * @return HashMap mapping currency code to exchange rate
     */
    private HashMap<String, Double> parseXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        HashMap<String, Double> rates = new HashMap<>();
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "Cube".equals(parser.getName()) && parser.getAttributeCount() == 2) {
                String currency = parser.getAttributeValue(null, "currency");
                String rateString = parser.getAttributeValue(null, "rate");
                try {
                    double rate = Double.parseDouble(rateString);
                    rates.put(currency, rate);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Invalid rate value for " + currency + ": " + rateString);
                }
            }
            eventType = parser.next();
        }

        // Ensure EUR is always present
        rates.put("EUR", 1.0);
        return rates;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdownNow();
    }
}
