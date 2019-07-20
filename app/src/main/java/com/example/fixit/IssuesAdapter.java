package com.example.fixit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.ViewHolder>{

    private final static String IMAGE_STORAGE_ROUTE = "images/";
    private static final String IMAGE_FORMAT = ".jpg";

    Context context;
    List<Issue> issues;

   public IssuesAdapter(Context context, List<Issue> issues){
       this.context = context;
       this.issues = issues;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_issue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Issue issue = issues.get(position);
        holder.bind(issue);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView ivIssue;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitleSingle);
            ivIssue = itemView.findViewById(R.id.ivIssueSingle);
            tvTimestamp = itemView.findViewById(R.id.tvTimeStampSingle);
        }

        public void bind(Issue issue) {
            tvTitle.setText(issue.getDescription());
            try {
                downloadFile(issue.getIssueID());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void downloadFile(String key) throws IOException {
            final File issueImage = File.createTempFile("images", "jpg");
            StorageReference mImageRef = FirebaseStorage.getInstance().getReference().child(IMAGE_STORAGE_ROUTE + key + IMAGE_FORMAT);
            mImageRef.getFile(issueImage)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // by this point we have the camera photo on disk
                            Bitmap takenImage = BitmapFactory.decodeFile(issueImage.getAbsolutePath());
                            // Load the taken image into a preview
                            ivIssue.setImageBitmap(takenImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    Toast.makeText(context, "Unable t download image in adapter", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
