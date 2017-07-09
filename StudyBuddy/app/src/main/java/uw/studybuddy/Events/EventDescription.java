package uw.studybuddy.Events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uw.studybuddy.MainActivity;
import uw.studybuddy.R;

public class EventDescription extends AppCompatActivity {

    private String mPostKey = null;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseJoin;

    private TextView edCourse;
    private TextView edTitle;
    private TextView edDescription;
    private TextView edLocation;
    private TextView edDate;
    private TextView edTime;
    private TextView edQuestId;
    private TextView edParticipants;

    private Button bDeleteEvent;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        edCourse = (TextView) findViewById(R.id.edCourse);
        edTitle = (TextView) findViewById(R.id.edTitle);
        edDescription = (TextView) findViewById(R.id.edDescription);
        edLocation = (TextView) findViewById(R.id.edLocation);
        edDate = (TextView) findViewById(R.id.edDate);
        edTime = (TextView) findViewById(R.id.edTime);
        edQuestId = (TextView) findViewById(R.id.edQuestId);
        edParticipants = (TextView) findViewById(R.id.edParticipants);

        bDeleteEvent = (Button) findViewById(R.id.BdeleteEvent);

        mPostKey = getIntent().getExtras().getString("event_id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event");
        mDatabaseJoin = FirebaseDatabase.getInstance().getReference().child("Participants");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabase.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String course = (String) dataSnapshot.child("course").getValue();
                String title = (String) dataSnapshot.child("title").getValue();
                String description = (String) dataSnapshot.child("description").getValue();
                String location = (String) dataSnapshot.child("location").getValue();
                String date = (String) dataSnapshot.child("date").getValue();
                String time = (String) dataSnapshot.child("time").getValue();
                String questId = (String) dataSnapshot.child("questId").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();

                edCourse.setText(course);
                edTime.setText(time);
                edDate.setText(date);
                edLocation.setText(location);
                edDescription.setText(description);
                edTitle.setText(title);
                edQuestId.setText(questId);

                if(mCurrentUser.getUid().equals(uid)){
                    bDeleteEvent.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseJoin.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //int numParticipants = (int) dataSnapshot.getChildrenCount();
                String participants = "";
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    String participant = (String) child.getValue();
                    if(participants.equals("")){
                        participants = participant;
                    } else {
                        participants = participants + " , " + participant;
                    }
                    edParticipants.setText(participants);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Toast.makeText(EventDescription.this, eventKey, Toast.LENGTH_LONG).show();

        bDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPostKey).removeValue();
                finish();
            }
        });
    }
}