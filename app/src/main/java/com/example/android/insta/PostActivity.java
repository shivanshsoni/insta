package com.example.android.insta;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST =2;
    private Uri uri = null;
    private ImageButton imagebutton;
    private EditText editname,editdesc;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private DatabaseReference dataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editname = (EditText)findViewById(R.id.editname);
        editdesc = (EditText)findViewById(R.id.editdesc);
     //   submitbutton = (Button)findViewById(R.id.submit);
        storageReference = FirebaseStorage.getInstance().getReference();
        dataref = database.getInstance().getReference().child("insta");
    }

    public void imageButtonClicked(View view){
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("Image/*");
        startActivityForResult(galleryintent,GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if(requestcode == GALLERY_REQUEST && resultcode == RESULT_OK) {
            uri = data.getData();
            imagebutton = (ImageButton)findViewById(R.id.image);
            imagebutton.setImageURI(uri);
        }
    }

    public void submitButtonClicked(View view){

        final String titleValue = editname.getText().toString().trim();
        final String descValue = editdesc.getText().toString().trim();
        ProgressDialog pd = new ProgressDialog(PostActivity.this);
        pd.setMessage("Uploading...");
        pd.show();
        if(!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(descValue)){

            StorageReference filepath = storageReference.child("postImage").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(PostActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    DatabaseReference newPost = dataref.push();
                    newPost.child("title").setValue(titleValue);
                    newPost.child("desc").setValue(descValue);
                    newPost.child("image").setValue(downloadurl.toString());
                    Intent main=new Intent(PostActivity.this, MainActivity.class);
                    startActivity(main);
                }
            });
        }
        else
        {
            if(TextUtils.isEmpty(titleValue)){
                editname.setError("Enter Name");
            }
            else if(TextUtils.isEmpty(descValue)){
                editdesc.setError("Enter Description");
            }
            else
            {
                Toast.makeText(this, "Select an Image First", Toast.LENGTH_SHORT).show();
            }

        }
        pd.dismiss();
    }
}
