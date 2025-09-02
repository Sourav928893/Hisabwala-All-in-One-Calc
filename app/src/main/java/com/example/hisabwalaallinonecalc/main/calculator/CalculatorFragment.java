package com.example.hisabwalaallinonecalc.main.calculator;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.hisabwalaallinonecalc.main.calculator.CalculatorUtils.highlightSpecialSymbols;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.utils.TTS;
import com.example.hisabwalaallinonecalc.utils.TTSInitializationListener;
import com.example.hisabwalaallinonecalc.utils.TouchAnimation;

import java.util.ArrayList;
import java.util.Locale;

public class CalculatorFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
        , View.OnClickListener, TTSInitializationListener {
    private TextView inputView;
    private TextView outputView;
    SharedPreferences defaultSp, historySp;
    private boolean switched = false;
    private TTS tts;
    private boolean ttsAvailable;
    private ColorStateList color;
    private boolean fromUser;
    private static final int[] BUTTON_IDS = {R.id.div, R.id.mul, R.id.sub, R.id.add, R.id.seven,
            R.id.eight, R.id.nine, R.id.brackets, R.id.four, R.id.five, R.id.six, R.id.inverse, R.id.delete,
            R.id.e, R.id.pi, R.id.factorial, R.id.time, R.id.SHOW_ALL, R.id.percentage, R.id.g,
            R.id.switchViews, R.id.sin, R.id.cos, R.id.tan, R.id.cot,
            R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean};
    private CalculatorViewModel viewModel;

    private final ActivityResultLauncher<Intent> speechRecognizerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> speechResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (speechResult != null && !speechResult.isEmpty()) {
                        inputView.setText(speechResult.get(0));
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchSpeechRecognizer();
                } else {
                    Toast.makeText(getContext(), "Permission for speech recognition denied.", Toast.LENGTH_SHORT).show();
                }
            });

    public CalculatorFragment() {
    }

    public static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defaultSp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        defaultSp.registerOnSharedPreferenceChangeListener(this);
        historySp = requireActivity().getSharedPreferences("history", MODE_PRIVATE);

        tts = new TTS();
        ttsAvailable = tts.ttsCreate(requireActivity(), this);

        inputView = view.findViewById(R.id.edit);
        outputView = view.findViewById(R.id.view);
        color = outputView.getTextColors();
        outputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(getString(R.string.formatError)) || editable.toString().equals(getString(R.string.bigNum))) {
                    outputView.setTextColor(requireActivity().getColor(R.color.wrong));
                } else {
                    outputView.setTextColor(color);
                }
            }
        });

        for (int buttonId : BUTTON_IDS) {
            View view1 = view.findViewById(buttonId);
            if (null != view1) {
                view1.setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
                view1.setOnClickListener(this);
                TouchAnimation touchAnimation = new TouchAnimation(view1);
                view1.setOnTouchListener(touchAnimation);
            }
        }

        ImageButton microphoneButton = view.findViewById(R.id.microphone);
        microphoneButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED) {
                launchSpeechRecognizer();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        });

        updateSpeaker();

        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            if (null != bundle.getString("select")) {
                String selected = bundle.getString("select");
                if (selected != null && selected.contains("-")) {
                    selected = "(" + selected + ")";
                }
                String inputtedEquation = inputView.getText().toString();
                if (inputtedEquation.isEmpty()) {
                    inputView.setText(selected);
                } else {
                    char last = inputtedEquation.charAt(inputtedEquation.length() - 1);
                    if (last == '+' || last == '-' || last == '(' || last == '×' || last == '÷' || last == '^') {
                        inputView.setText(inputtedEquation + selected);
                    } else {
                        inputView.setText(inputtedEquation + "+" + selected);
                    }
                    boolean useDeg = defaultSp.getBoolean("mode", false);
                    Calculator formulaUtil1 = new Calculator(useDeg);
                    fromUser = false;
                    viewModel.handleEqualButton(inputView.getText().toString(), formulaUtil1, defaultSp, historySp, fromUser, getString(R.string.bigNum), getString(R.string.formatError));
                }
                highlightSpecialSymbols(inputView);
            }
        });

        viewModel.getInputTextState().observe(getViewLifecycleOwner(), input -> inputView.setText(input));
        viewModel.getOutputTextState().observe(getViewLifecycleOwner(), output -> outputView.setText(output));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                View view = requireView().findViewById(buttonId);
                if (view != null) {
                    view.setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
                }
            }
        } else if ("scale".equals(s) || "mode".equals(s)) {
            String inputStr1 = inputView.getText().toString();
            boolean useDeg = defaultSp.getBoolean("mode", false);
            if (!inputStr1.isEmpty()) {
                Calculator formulaUtil1 = new Calculator(useDeg);
                fromUser = false;
                viewModel.handleEqualButton(inputStr1, formulaUtil1, defaultSp, historySp, false, getString(R.string.bigNum), getString(R.string.formatError));
            }
        }
    }

    private void launchSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            speechRecognizerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tts.ttsDestroy();
        ttsAvailable = tts.ttsCreate(requireActivity(), this);
        updateSpeaker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.ttsDestroy();
        defaultSp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onClick(View v) {

        boolean useDeg = defaultSp.getBoolean("mode", false);
        boolean canSpeak = defaultSp.getBoolean("voice", false);

        String inputStr = inputView.getText().toString();
        Calculator formulaUtil = new Calculator(useDeg);

        try {
            if (v.getId() == R.id.equal && !inputStr.isEmpty()) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.equal));
                }
                fromUser = true;
                viewModel.handleEqualButton(inputStr, formulaUtil, defaultSp, historySp, true, getString(R.string.bigNum), getString(R.string.formatError));
            } else if (v.getId() == R.id.Clean) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.resetInput));
                }
                viewModel.handleCleanButton();
            } else if (v.getId() == R.id.factorial) {
                String fac = getString(R.string.factorial);
                String doubleFac = getString(R.string.double_factorial);
                viewModel.handleFactorial(inputStr, canSpeak, tts, fac, doubleFac);
            } else if (v.getId() == R.id.delete) {
                viewModel.handleDeleteButton(inputStr);
            } else if (v.getId() == R.id.brackets) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.bracket));
                }
                viewModel.handleBracketsButton(inputStr);
            } else if (v.getId() == R.id.inverse) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.inverse));
                }
                viewModel.handleInverseButton(inputStr);
            } else if (v.getId() == R.id.time) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.power));
                }
                viewModel.handleOtherButtons(v, inputStr, canSpeak, tts, fromUser);
            } else if (v.getId() == R.id.switchViews) {
                View view = requireView();
                if (!switched) {
                    ((Button) view.findViewById(R.id.sin)).setText("sin⁻¹");
                    ((Button) view.findViewById(R.id.cos)).setText("cos⁻¹");
                    ((Button) view.findViewById(R.id.tan)).setText("tan⁻¹");
                    ((Button) view.findViewById(R.id.cot)).setText("cot⁻¹");
                    ((Button) view.findViewById(R.id.g)).setText("ln");
                    ((Button) view.findViewById(R.id.e)).setText("exp");
                } else {
                    ((Button) view.findViewById(R.id.sin)).setText("sin");
                    ((Button) view.findViewById(R.id.cos)).setText("cos");
                    ((Button) view.findViewById(R.id.tan)).setText("tan");
                    ((Button) view.findViewById(R.id.cot)).setText("cot");
                    ((Button) view.findViewById(R.id.g)).setText("log");
                    ((Button) view.findViewById(R.id.e)).setText("e");
                }
                switched = !switched;
            } else {
                viewModel.handleOtherButtons(v, inputStr, canSpeak, tts, fromUser);
            }
            String inputStr1 = inputView.getText().toString();
            highlightSpecialSymbols(inputView);
            if (v.getId() != R.id.equal) {
                //自动运算
                if (!inputStr1.isEmpty()) {
                    Calculator formulaUtil1 = new Calculator(useDeg);
                    fromUser = false;
                    viewModel.handleEqualButton(inputStr1, formulaUtil1, defaultSp, historySp, false, getString(R.string.bigNum), getString(R.string.formatError));
                }
            }
        } catch (Exception e) {
            outputView.setText("");
        }
    }

    @Override
    public void onTTSInitialized(boolean isSuccess) {
        ttsAvailable = isSuccess;
        updateSpeaker();
    }

    private void updateSpeaker() {
        ImageButton microphoneButton = requireView().findViewById(R.id.microphone);
        if (!ttsAvailable) {
            microphoneButton.setVisibility(View.INVISIBLE);
        } else {
            microphoneButton.setVisibility(View.VISIBLE);
            microphoneButton.bringToFront();
        }
    }
}
