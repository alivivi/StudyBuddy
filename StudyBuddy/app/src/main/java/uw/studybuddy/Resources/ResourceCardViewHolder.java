package uw.studybuddy.Resources;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import uw.studybuddy.CustomCoursesSpinner;
import uw.studybuddy.R;
import uw.studybuddy.Tutoring.FirebaseTutor;
import uw.studybuddy.Tutoring.TutorInfo;
import uw.studybuddy.UserProfile.FirebaseUserInfo;

/**
 * Created by leksharamdenee on 2017-07-14.
 */

public class ResourceCardViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public ResourceCardViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setCourse(String course){
        TextView cour = (TextView)mView.findViewById(R.id.cardview_resource_course);
        cour.setText(course);
    }

    public void setTitle(String title) {
        TextView titleView = (TextView)mView.findViewById(R.id.cardview_resource_title);
        titleView.setText(title);
    }

    public void setUserNameWithQuestId(String questId, boolean isAnonymous){
        if (isAnonymous) {
            TextView header = (TextView)mView.findViewById(R.id.cardview_resource_by);
            TextView name = (TextView)mView.findViewById(R.id.cardview_resource_username);
            header.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
        } else {
            FirebaseUserInfo.setUserDisplayNameWithQuestId(questId, this);
        }
    }

    public void setDisplayName(String displayName) {
        TextView name = (TextView)mView.findViewById(R.id.cardview_resource_username);
        name.setText(displayName);
    }

    public void setLink(final String link){
        TextView url = (TextView)mView.findViewById(R.id.cardview_resource_link);
        url.setText(link);

        url.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });
    }

    public void setButton(boolean isCreator, final ResourceInfo resourceInfo) {
        Button flagButton = (Button)mView.findViewById(R.id.cardview_resource_flag_button);
        Button editButton = (Button)mView.findViewById(R.id.cardview_resource_edit);
        Button deleteButton = (Button)mView.findViewById(R.id.cardview_resource_delete);

        if (isCreator) {
            flagButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(mView.getContext(), resourceInfo);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showDeleteDialog(mView.getContext(), resourceInfo);
                }
            });
        } else {
            flagButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            flagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // showContactDialog(mView.getContext(), resourceInfo);
                }
            });
        }
    }

    private void showEditDialog(Context context, final ResourceInfo resourceInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_new_resource, null);
        builder.setTitle("Edit Resource");
        builder.setView(view);

//         Setup the courses spinner
        final Spinner spinner = CustomCoursesSpinner.getSpinner(R.id.new_resource_courses_spinner, context, view);
        CustomCoursesSpinner.setSelectedText(resourceInfo.getCourse());

        // Get UI Components
        final EditText title = (EditText) view.findViewById(R.id.new_resource_title);
        final EditText link = (EditText) view.findViewById(R.id.new_resource_link);
        final CheckBox anonymous = (CheckBox) view.findViewById(R.id.anonymous_checkBox);

        // Set the current values
        title.setText(resourceInfo.getTitle());
        link.setText(resourceInfo.getLink());
        anonymous.setChecked(resourceInfo.isAnonymous());

        builder.setNeutralButton("cancel", null);
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String course = spinner.getSelectedItem().toString();
                String newTitle = title.getText().toString();
                String newLink = link.getText().toString();
                boolean newAnonymous = anonymous.isChecked();

                resourceInfo.setCourse(course);
                resourceInfo.setTitle(newTitle);
                resourceInfo.setLink(newLink);
                resourceInfo.setAnonymous(newAnonymous);
                FirebaseResourceInfo.updateResource(resourceInfo);
            }
        });
        builder.show();
    }
}
