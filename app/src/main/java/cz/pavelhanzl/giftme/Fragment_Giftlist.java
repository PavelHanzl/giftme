package cz.pavelhanzl.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Fragment_Giftlist extends Fragment {
    private Button mButtonTestDb;
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mNameReference;
    private Adapter_Name mAdapter_name;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_giftlist,container,false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mNameReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names");
        FloatingActionButton buttonAddName = mView.findViewById(R.id.frag_giftlist_floatingButton_add_name);
        buttonAddName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Activity_NewName.class));
            }
        });

        setUpRecyclerView();

//        mButtonTestDb = view.findViewById(R.id.fragment_button_testDb);

//        mButtonTestDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               saveToDB();
//            }
//        });
        return mView;
    }

    private void setUpRecyclerView() {
        Query query = mNameReference.orderBy("budget", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Name> options = new FirestoreRecyclerOptions.Builder<Name>().setQuery(query, Name.class).build();
        mAdapter_name = new Adapter_Name(options);

        RecyclerView recyclerView = mView.findViewById(R.id.frag_giftlist_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter_name.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter_name.stopListening();
    }

    //    private void saveToDB(){
//        Map<String,Object> note = new HashMap<>();
//        note.put("Title","První data přepiš");
//        note.put("Desription","Lorem ipsum sit dolor amut.");
//
//        mDb.collection("Users").document("FirstDocument").set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}
