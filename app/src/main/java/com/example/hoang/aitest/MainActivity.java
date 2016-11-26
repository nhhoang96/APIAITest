package com.example.hoang.aitest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {
    private Button listenButton;
    private TextView resultTextView;
    private AIService aiService;
    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        final AIConfiguration config = new AIConfiguration("384b243aa7c148b590da67014af0be92",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        listenButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aiService.startListening();

                //String toSpeak = resultTextView.getText().toString();
                //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void speakUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speakGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
//    public void listenButtonOnClick(final View view) {
//
//        aiService.startListening();
//    }
    @Override

    public void onResult(final AIResponse response) {
        Result result = response.getResult();
        final Result referenceRes = result;

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        } else {
            Toast.makeText(this, "NOT WORKING", Toast.LENGTH_LONG);
        }
        t1 = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                t1.setLanguage(Locale.US);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speakGreater21(referenceRes.getFulfillment().getSpeech());
                } else {
                    speakUnder20(referenceRes.getFulfillment().getSpeech());
                }
            }
        });

        String actionString = result.getAction().substring(1);
        confirmAction(result,parameterString);
//        if (actionString.equals("Greetback")) {
//            confirmAction(result, parameterString);
//
//        }
    }

    @Override
    public void onError(AIError error) {
        resultTextView.setText(error.toString());
    }

    public void confirmAction(Result result, String parameterString) {
        resultTextView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parameterString +
                "\n Speech response: " + result.getFulfillment().getSpeech());
    }
    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
