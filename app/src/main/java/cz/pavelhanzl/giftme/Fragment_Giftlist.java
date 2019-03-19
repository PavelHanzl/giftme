package cz.pavelhanzl.giftme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Fragment_Giftlist extends Fragment {
    private Button mButtonTestDb;
    private FirebaseFirestore mDb;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_giftlist,container,false);

        mDb = FirebaseFirestore.getInstance();

        mButtonTestDb = view.findViewById(R.id.fragment_button_testDb);



        mButtonTestDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveToDB();
            }
        });



        return view;
    }

    private void saveToDB(){
        Map<String,Object> note = new HashMap<>();
        note.put("Title","První data");
        note.put("Desription","Lorem ipsum sit dolor amut.");

        mDb.collection("Users").document("FirstDocument").set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
