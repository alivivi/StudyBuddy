package uw.studybuddy.UserProfile;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.studybuddy.CourseInfo;
import uw.studybuddy.Events.EventCardViewHolder;
import uw.studybuddy.Events.EventsListRecycleViewFragment;
import uw.studybuddy.Resources.ResourceCardViewHolder;
import uw.studybuddy.Tutoring.TutorCardViewHolder;
import uw.studybuddy.Tutoring.TutorInfo;

/**
 * Created by Yuna on 17/7/7.
 */


public class FirebaseUserInfo {

    final static String TAG = "Firebase UserInfo";

    // All fields in the Users table
    private static String table_users          = "Users";
    private static String field_quest_id       = "quest_id";
    private static String field_display_name   = "display_name";
    private static String field_user_name      = "user_name";
    private static String field_about_me       = "about_me";
    private static String field_read           = "read";
    private static String field_nameList       = "namelist";
    private static String field_user_image     = "image";
    //private static String field_


    // Tables in the user table
    public static String table_courses         = "course";
    public static String table_friend          = "friendlist";

    private static String emptyString = "";

    //update the whole User profile to the firebase
    //if the child is existed in the firebase, then override it.


    public static String get_field_display_name(){
        return field_display_name;
    }

    public static DatabaseReference getUsersTable() {
        return FirebaseDatabase.getInstance().getReference().child(table_users);
    }

    public static DatabaseReference getUserWithQuestId(String questId) {
        return getUsersTable().child(questId);
    }

    public static String setUserDisplayNameWithQuestId(String questId, final RecyclerView.ViewHolder viewHolder) {
        DatabaseReference userRef = getUserWithQuestId(questId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String displayName = snapshot.child(field_display_name).getValue().toString();
                // Get the user's display name
                if (viewHolder.getClass() == TutorCardViewHolder.class) {
                    ((TutorCardViewHolder)viewHolder).setTutorName(displayName);
                }
                else if (viewHolder.getClass() == ResourceCardViewHolder.class) {
                    ((ResourceCardViewHolder)viewHolder).setDisplayName(displayName);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        return emptyString;
    }


    public static void update_UserInfo(UserPattern USER){
        //String key  = databaseReference.child("Users").push().getKey().toString();
        DatabaseReference DestReference = getUsersTable().child(USER.getquest_id().toString());
        DestReference.setValue(USER);

        //update the things to name list
        update_name_list(USER.getdisplay_name());

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        //the user.getemail() should display the key now
        UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(USER.getquest_id())
                .build();
        User.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated.");
                }
            }
        });

        return;
    }

    public static DatabaseReference get_namelist_ref(){
        return FirebaseDatabase.getInstance().getReference().child(field_nameList);
    }
    public static void update_name_list(String name){
        get_namelist_ref().child(name).setValue(get_QuestId());
    }

    public static void set_DisplayName(String name){
        DatabaseReference displayNameRef = getCurrentUserDisplayNameRef();
        //get the key
        displayNameRef.setValue(name);
    }

    public static void set_Image(Uri imageUri){
        DatabaseReference ImageUri = getCurrentUserRef().child(field_user_image);
        ImageUri.setValue(imageUri.toString());
    }

    // Getters

    public static String get_Email() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    }

    public static String get_QuestId() {
        String email = get_Email();
        String QuestID = email.substring(0, email.length()-17);
        return QuestID;
    }

    public static DatabaseReference getCurrentUserDisplayNameRef(){
        return getCurrentUserRef().child(field_display_name);
    }

    public static DatabaseReference getCurrentUserRef() {
        return getUsersTable().child(get_QuestId());
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getCurrentUserReadRef() {
        return getCurrentUserRef().child(field_read);
    }

    // Setters

    public static void set_mAboutMe(String mAboutMe){
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        //get the key
        if(User.getDisplayName() == null) {
            String email = User.getEmail();
            String key = email.substring(0, email.length() - 17);
            getUsersTable().child(key).child(field_about_me).setValue(mAboutMe);
        }else {
            getUsersTable().child(User.getDisplayName().toString()).child(field_about_me).setValue(mAboutMe);
        }
        return;
    }
    // Add a course to the database.
    public static void add_mCourse(int index, String subject, String num){
        String key  = get_QuestId();
        String courseIndexSting = Integer.toString(index);
        if(key != null) {
            DatabaseReference mCourseReference = getUsersTable().child(key).child(table_courses);
            CourseInfo newcourse = new CourseInfo(subject, num);
            mCourseReference.child(courseIndexSting).setValue(newcourse);
        }
        return;
    }

    public static void update_courseInfo(int index, String subject, String num) {
        add_mCourse(index, subject, num);
    }

    public static void set_UserRead(String val) {
        getCurrentUserReadRef().setValue(val);
    }

    public static void update_coursesList(List<CourseInfo> newList) {
        String key = get_QuestId();

        // Create new map to update course table for user
        Map<String, CourseInfo> coursesMap = new HashMap<>();
        for (int i = 0; i < newList.size(); i++) {
            String indexString = Integer.toString(i);
            coursesMap.put(indexString, (CourseInfo) newList.get(i));
        }

        if (key != null) {
            getUsersTable().child(key).child(table_courses).setValue(coursesMap);
        }
    }

    //update the read filed to make the listener function run
    public static void listener_trigger(){
        String key;
        key = get_QuestId();
        DatabaseReference mReadReference = getUsersTable().child(key).child("read");
        mReadReference.setValue("true");
    }

    //dataSnapshot is the dataSnapshot of the friendlist
    public static List<String> get_friend_list_fromDatabase(DataSnapshot dataSnapshot){
        List<String> FriendList = new ArrayList<String>();
        for(DataSnapshot value : dataSnapshot.getChildren()){
            String result = value.getValue(String.class);
            FriendList.add(result);
        }
        return FriendList;
    }

    public static String getField_user_image() {
        return field_user_image;
    }
}
