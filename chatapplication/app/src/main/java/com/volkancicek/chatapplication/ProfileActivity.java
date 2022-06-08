package com.volkancicek.chatapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.volkancicek.chatapplication.databinding.ActivityProfileBinding;

import java.util.HashMap;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityProfileBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        getData();

    }
    public void getData(){
        DatabaseReference newReference = database.getReference("Profiles");
        Query query = newReference.orderByChild("usermessagetime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String,String> hashMap = (HashMap<String,String>) ds.getValue();
                    String username = hashMap.get("useremail");
                    if(username.matches(auth.getCurrentUser().getEmail().toString())){
                        String des = hashMap.get("Description");
                        if(des!= null)
                        {
                            binding.comment.setText(des);
                        }
                        else binding.comment.setText("");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void saveProfile (View view){
        UUID uuid = UUID.randomUUID();
        String uuidRef = uuid.toString();
        String description = binding.comment.getText().toString();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail().toString();
        //databaseReference.child("Profiles").child(uuidRef).child("userImageUrl").setValue(downloadUrl);
        databaseReference.child("Profiles").child(uuidRef).child("useremail").setValue(email);
        databaseReference.child("Profiles").child(uuidRef).child("Description").setValue(description);
        databaseReference.child("Profiles").child(uuidRef).child("usermessagetime").setValue(ServerValue.TIMESTAMP);
        Toast.makeText(getApplicationContext(),"Successful Upload",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        startActivity(intent);
        }




}