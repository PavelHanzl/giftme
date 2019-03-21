package cz.pavelhanzl.giftme;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CountDownLatch;

public class Activity_Persons_Gitflist extends AppCompatActivity {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private DocumentSnapshot mDocumentSnapshotName;
    private Name mSelectedNameObject;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_gitflist);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        getDocumentSnapshotForSelectedName();
        mHandler.postDelayed(mRunnable,500);// 0,5s timeout pro získání snapshotu


    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setTitle(getString(R.string.giftlist_title) + " - " + mSelectedNameObject.getName());
        }
    };


    private void getDocumentSnapshotForSelectedName() {
        DocumentReference documentReferenceName = mDb.document(getIntent().getStringExtra("path"));
        documentReferenceName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mDocumentSnapshotName = task.getResult();
                    if (mDocumentSnapshotName.exists()) {
                        Log.d("Activity_Persons_Giftli", "DocumentSnapshot data: " + mDocumentSnapshotName.getData());
                        mSelectedNameObject = mDocumentSnapshotName.toObject(Name.class);
                        Log.d("Activity_Persons_Giftli", " mSelectedNameObject " + mSelectedNameObject.getName());
                    } else {
                        Log.d("Activity_Persons_Giftli", "No such document");
                    }
                } else {
                    Log.d("Activity_Persons_Giftli", "get failed with ", task.getException());
                }
            }
        });
    }
}
