package com.example.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.Arrays;

public class AddNoteActivity extends AppCompatActivity {

    private final int MAX_CHARS = 144;
    private final int TITLE_MAX_CHARS = 35;

    private TextView charsLimit, titleCharsLimit;
    private EditText noteInput, titleInput;
    private RadioGroup durationGroup;
    private MaterialButton addNote;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get view widgets
        charsLimit = (TextView) findViewById(R.id.wordLimitText);
        titleCharsLimit = (TextView) findViewById(R.id.charLimitTitle);
        noteInput = (EditText) findViewById(R.id.addNoteText);
        titleInput = (EditText) findViewById(R.id.addNoteTitle);
        durationGroup = (RadioGroup) findViewById(R.id.duratioRadioBtnGroup);
        addNote = (MaterialButton) findViewById(R.id.addNoteBtn);

        setWidgets();
    }

    private void setWidgets() {
        InputFilter[] textFilterArray = new InputFilter[1];
        InputFilter[] titleFilterArray = new InputFilter[1];
        textFilterArray[0] = new InputFilter.LengthFilter(MAX_CHARS);
        titleFilterArray[0] = new InputFilter.LengthFilter(TITLE_MAX_CHARS);

        titleInput.setFilters(titleFilterArray);
        noteInput.setFilters(textFilterArray);
        noteInput.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        charsLimit.setText("0/" + MAX_CHARS);
        titleCharsLimit.setText("0/" + TITLE_MAX_CHARS);
        charLimitChange();
        titleCharLimitChange();

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = noteInput.getText().toString();
                String noteTitle = titleInput.getText().toString();
                String noteDuration = durationData();

                if(!TextUtils.isEmpty(noteInput.getText()) && noteDuration != null
                        && !TextUtils.isEmpty(titleInput.getText())) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("duration", noteDuration);
                    returnIntent.putExtra("text", noteText);
                    returnIntent.putExtra("title", noteTitle);

                    setResult(AddNoteActivity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(v.getContext(), "Please provide all input fields.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void charLimitChange() {
        noteInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLength = s.toString().length();

                if (charsLength <= MAX_CHARS) {
                    String currLength = String.valueOf(charsLength);
                    charsLimit.setText(currLength + "/" + MAX_CHARS);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void titleCharLimitChange() {
        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLength = s.toString().length();

                if (charsLength <= TITLE_MAX_CHARS) {
                    String currLength = String.valueOf(charsLength);
                    titleCharsLimit.setText(currLength + "/" + TITLE_MAX_CHARS);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private int checkButton() {
        return durationGroup.getCheckedRadioButtonId();
    }

    private String durationData() {
        int result = checkButton();
        switch (result) {
            case R.id.radioButton30Min:
                return "min";
            case R.id.radioButton1hr:
                return "med";
            case R.id.radioButton3hr:
                return "max";
            default:
                return null;
        }
    }
}
