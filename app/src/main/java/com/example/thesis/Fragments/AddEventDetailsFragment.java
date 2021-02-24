package com.example.thesis.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.thesis.AddEventActivity;
import com.example.thesis.AddNoteActivity;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddEventDetailsFragment extends Fragment {

    final Calendar calendar = Calendar.getInstance();

    private final int MAX_CHARS = 144;
    private final int TITLE_MAX_CHARS = 35;

    private TextView charsLimitTitle, charsLimitDesc, participantsText;
    private EditText titleInput, descInput, dateInput;
    private MaterialButton addParticipants, addEvent;
    private CheckBox createChat;

    private ArrayList<User> participants = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        initWidgets(view);
        getParticipants();
        Log.d("participants", "created");
        return view;
    }

    private void initWidgets(View v) {

        //Text views
        charsLimitTitle = (TextView) v.findViewById(R.id.charLimitTitleEvent);
        charsLimitDesc = (TextView) v.findViewById(R.id.charLimitEventDesc);
        participantsText = (TextView) v.findViewById(R.id.participantsText);

        //EditText views
        titleInput = (EditText) v.findViewById(R.id.addEventTitle);
        descInput = (EditText) v.findViewById(R.id.addEventDesc);
        dateInput = (EditText) v.findViewById(R.id.enterDate);

        descInput.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //CheckBox
        createChat = (CheckBox) v.findViewById(R.id.createChatCheck);

        //MatButton Views
        addParticipants = (MaterialButton) v.findViewById(R.id.addParticipantsBtn);
        addEvent = (MaterialButton) v.findViewById(R.id.addEventBtn);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventActivity activity = new AddEventActivity();
                activity.changeFragment();
            }
        });

        dateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(v.getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean addCondition = !TextUtils.isEmpty(titleInput.getText())
                        && !TextUtils.isEmpty(descInput.getText()) && !TextUtils.isEmpty(dateInput.getText()) && !participants.isEmpty();

                if(addCondition) {

                    ArrayList<String> participantsId = new ArrayList<>();

                    for (User part: participants) {
                        participantsId.add(part.getuId());
                    }
                    participantsId.add(MainActivity.userObj.getuId());

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("date", dateInput.getText().toString());
                    returnIntent.putExtra("desc", descInput.getText().toString());
                    returnIntent.putExtra("title", titleInput.getText().toString());
                    returnIntent.putExtra("hasChat", createChat.isChecked());
                    returnIntent.putStringArrayListExtra("participants", participantsId);

                    getActivity().setResult(AddEventActivity.RESULT_OK, returnIntent);
                    getActivity().finish();
                }
                else {
                    Toast.makeText(v.getContext(), "Please provide all input fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLabel() {
        String format = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        dateInput.setText(sdf.format(calendar.getTime()));
    }

    private void getParticipants() {
        Bundle bundle = this.getArguments();
        //Log.d("participants", "NULL");
        if(bundle != null) {
            Log.d("participants", "Not NULL");
            participants.clear();
            participants.addAll((ArrayList<User>) bundle.getSerializable("participantsList"));
            Log.d("participants", participants.toString());
            for(User part: participants) {
                Log.d("participants", part.getuDisplayName());
                if(participants.indexOf(part) == 0) {
                    participantsText.setText(part.getuDisplayName());
                }
                else {
                    participantsText.setText(participantsText.getText() + ", " + part.getuDisplayName());
                }

            }
            if(participants.size() > 0) {
                participantsText.setText(participantsText.getText()  + MainActivity.userObj.getuDisplayName());
            }


        }
    }
}
