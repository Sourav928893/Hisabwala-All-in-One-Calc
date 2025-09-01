package com.example.hisabwalaallinonecalc.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTS {

    private TextToSpeech tts;

    public boolean ttsCreate(Context context, TTSInitializationListener listener) {
        try {
            tts = new TextToSpeech(context, status -> {
                boolean isSuccess = (status == TextToSpeech.SUCCESS);
                if (isSuccess) {
                    int res = tts.setLanguage(Locale.getDefault());
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        isSuccess = false;
                    }
                }
                if (listener != null) {
                    listener.onTTSInitialized(isSuccess);
                }
            });
            return true;
        } catch (Exception e) {
            Log.e("TTS", "Init error", e);
            if (listener != null) listener.onTTSInitialized(false);
            return false;
        }
    }

    public void ttsSpeak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void ttsDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
