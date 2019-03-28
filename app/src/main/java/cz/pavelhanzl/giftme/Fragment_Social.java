package cz.pavelhanzl.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Fragment_Social extends Logic_DrawerFragment  {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mAddedUsersReference;
    private Adapter_Added_User mAdapter_added_user;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(1);
        mView = inflater.inflate(R.layout.fragment_social,container,false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //získá kolekci jmen pro přihlášeného uživatele
        mAddedUsersReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("AddedUsers");

        setUpFloatingButton();
        setUpRecyclerView();

        return mView;
    }

    /**
     * Nastaví floating button pro přidání uživatele.
     */
    private void setUpFloatingButton() {
        FloatingActionButton buttonAddName = mView.findViewById(R.id.frag_social_floatingButton_add_name);
        buttonAddName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Activity_NewAddedUser.class));
            }
        });
    }

    /**
     * Nastavuje recyclerView. Řadí podle jména.
     */
    private void setUpRecyclerView() {
        Query query = mAddedUsersReference.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<AddedUser> options = new FirestoreRecyclerOptions.Builder<AddedUser>().setQuery(query, AddedUser.class).build();
        mAdapter_added_user = new Adapter_Added_User(options);

        RecyclerView recyclerView = mView.findViewById(R.id.frag_social_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter( mAdapter_added_user);

        //deleteItemFromRecyclerView(recyclerView);
        //setCardsOnClickAction();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter_added_user.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter_added_user.stopListening();
    }


    @Override
    public void onResume() {

        super.onResume();
        setActiveMenuIcon(1);
    }


}
