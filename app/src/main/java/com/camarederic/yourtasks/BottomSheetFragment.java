package com.camarederic.yourtasks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.camarederic.yourtasks.model.Priority;
import com.camarederic.yourtasks.model.SharedViewModel;
import com.camarederic.yourtasks.model.Task;
import com.camarederic.yourtasks.model.TaskViewModel;
import com.camarederic.yourtasks.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;


public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private ImageButton saveButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private CalendarView calendarView;
    private Group calendarGroup;

    private Date dueDate;
    Calendar calendar = Calendar.getInstance();

    private SharedViewModel sharedViewModel;

    private boolean isEdit;

    private Priority priority;


    public BottomSheetFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.bottom_sheet, container, false );

        enterTodo = view.findViewById( R.id.enter_todo_edit_text );
        calendarButton = view.findViewById( R.id.today_calendar_button );
        priorityButton = view.findViewById( R.id.priority_todo_button );
        saveButton = view.findViewById( R.id.save_todo_button );
        calendarGroup = view.findViewById( R.id.calendar_group );
        calendarView = view.findViewById( R.id.calendarView );
        priorityRadioGroup = view.findViewById( R.id.radio_group_priority );

        Chip todayChip = view.findViewById( R.id.today_chip );
        Chip tomorrowChip = view.findViewById( R.id.tomorrow_chip );
        Chip nextWeekChip = view.findViewById( R.id.next_week_chip );

        todayChip.setOnClickListener( this );
        tomorrowChip.setOnClickListener( this );
        nextWeekChip.setOnClickListener( this );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText( task.getTask() );
            Log.d( "MY", "onViewCreated: " + task.getTask() );
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        sharedViewModel = new ViewModelProvider( requireActivity() ).get( SharedViewModel.class );


        calendarButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                calendarGroup.setVisibility( calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE );
                Utils.hideSoftKeyboard( view1 );
            }
        } );

        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                Log.d( "Calendar", "onViewCreated: ===> month " + " " + (month + 1) + ", dayOfMonth " + dayOfMonth );
                calendar.clear();
                calendar.set( year, month, dayOfMonth );
                dueDate = calendar.getTime();
            }
        } );

        priorityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view3) {
                Utils.hideSoftKeyboard( view3 );

                priorityRadioGroup.setVisibility(
                        priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE );
                priorityRadioGroup.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                        if (priorityRadioGroup.getVisibility() == View.VISIBLE) {
                            selectedButtonId = checkId;
                            selectedRadioButton = view.findViewById( selectedButtonId );

                            if (selectedRadioButton.getId() == R.id.radio_button_high) {
                                priority = Priority.HIGH;
                            } else if (selectedRadioButton.getId() == R.id.radio_button_medium) {
                                priority = Priority.MEDIUM;
                            } else if (selectedRadioButton.getId() == R.id.radio_button_low) {
                                priority = Priority.LOW;
                            } else {
                                priority = Priority.LOW;
                            }
                        } else {
                            priority = Priority.LOW;
                        }


                    }
                } );
            }
        } );

        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String task = enterTodo.getText().toString().trim();
                if (!TextUtils.isEmpty( task ) && dueDate != null && priority != null) {
                    Task myTask = new Task( task, priority,
                            dueDate, Calendar.getInstance().getTime(), false );
                    if (isEdit) {
                        Task updateTask = sharedViewModel.getSelectedItem().getValue();
                        assert updateTask != null;
                        updateTask.setTask( task );
                        updateTask.setDataCreated( Calendar.getInstance().getTime() );
                        updateTask.setPriority( priority );
                        updateTask.setDueDate( dueDate );
                        TaskViewModel.update( updateTask );
                        sharedViewModel.setIsEdit( false );
                    } else {
                        TaskViewModel.insert( myTask );
                    }
                    enterTodo.setText( "" );
                    if (BottomSheetFragment.this.isVisible()) {
                        BottomSheetFragment.this.dismiss();
                    }


                } else {
                    Snackbar.make( saveButton, "Empty task", Snackbar.LENGTH_LONG ).show();
                }
            }
        } );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.today_chip) {
            calendar.add( Calendar.DAY_OF_YEAR, 0 );
            dueDate = calendar.getTime();
            Log.d( "TIME", "onClick: " + dueDate.toString() );
        } else if (id == R.id.tomorrow_chip) {
            calendar.add( Calendar.DAY_OF_YEAR, 1 );
            dueDate = calendar.getTime();
            Log.d( "TIME", "onClick: " + dueDate.toString() );
        } else if (id == R.id.next_week_chip) {
            calendar.add( Calendar.DAY_OF_YEAR, 7 );
            dueDate = calendar.getTime();
            Log.d( "TIME", "onClick: " + dueDate.toString() );
        }

    }
}