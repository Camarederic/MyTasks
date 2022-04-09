package com.camarederic.yourtasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.camarederic.yourtasks.adapter.OnTodoClickListener;
import com.camarederic.yourtasks.adapter.RecyclerViewAdapter;
import com.camarederic.yourtasks.model.Priority;
import com.camarederic.yourtasks.model.SharedViewModel;
import com.camarederic.yourtasks.model.Task;
import com.camarederic.yourtasks.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTodoClickListener {

    private TaskViewModel taskViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private int counter;

    BottomSheetFragment bottomSheetFragment;

    private SharedViewModel sharedViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        counter = 0;

        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById( R.id.bottom_sheet );
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from( constraintLayout );
        bottomSheetBehavior.setPeekHeight( BottomSheetBehavior.STATE_HIDDEN );

        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(
                MainActivity.this.getApplication() ).create( TaskViewModel.class );

        sharedViewModel = new ViewModelProvider( this ).get( SharedViewModel.class );

        taskViewModel.getAllTasks().observe( this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                for (Task task : tasks) {
                    Log.d( "ITEM_TAG", "onCreate: " + task.getTaskId() );
                }
                recyclerViewAdapter = new RecyclerViewAdapter( tasks, MainActivity.this );
                recyclerView.setAdapter( recyclerViewAdapter );
            }
        } );


        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showBottomSheetDialog();

            }
        } );

    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show( getSupportFragmentManager(), bottomSheetFragment.getTag() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    public void onTodoClick(Task task) {
        Log.d( "Click", "onRadioButton: " + task.getTask() );
        sharedViewModel.selectItem( task );
        sharedViewModel.setIsEdit( true );
        showBottomSheetDialog();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onTodoRadioButtonClick(Task task) {
        Log.d( "Click", "onRadioButton: " + task.getTask() );
        TaskViewModel.delete( task );
        recyclerViewAdapter.notifyDataSetChanged();
    }
}