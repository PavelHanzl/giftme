package cz.pavelhanzl.giftme.social;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cz.pavelhanzl.giftme.Logic_DrawerFragment;
import cz.pavelhanzl.giftme.social.gift_tips.Activity_GiftTips;
import cz.pavelhanzl.giftme.R;

public class Fragment_Social extends Logic_DrawerFragment {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mAddedUsersReference;
    private Adapter_Added_User mAdapter_added_user;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(2);
        mView = inflater.inflate(R.layout.fragment_social,container,false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //získá kolekci jmen pro přihlášeného uživatele
        mAddedUsersReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("AddedUsers");

        setUpFloatingButton();
        setUpRecyclerView();
        setCardsOnClickAction();

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

        deleteItemFromRecyclerView(recyclerView);
        //setCardsOnClickAction();
    }


    /**
     * Odstraní položku z recyclerView při posunutí položky doprava nebo doleva.
     *
     * @param recyclerView
     */
    private void deleteItemFromRecyclerView(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;

            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAdapter_added_user.deleteItem(viewHolder.getAdapterPosition());
                snackbarUndoDelete();
            }

            private void snackbarUndoDelete() {
                Snackbar snackbar = Snackbar
                        .make(getView().findViewById(R.id.coordinatorLayoutSocial), getString(R.string.swipe_deleted), 6000);
                snackbar.setAction(getString(R.string.swipe_deleted_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText( getContext(),  getString(R.string.snackbar_restored), Toast.LENGTH_LONG ).show();
                        mAdapter_added_user.restoreItem();

                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                ColorDrawable background;
                Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_archive);

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX < 0) { // Swiping to the left
                    Log.d("Swiping:","Left");

                    //nastaví background a ikonku
                    background = new ColorDrawable(getResources().getColor(R.color.swipeToDelete));
                    icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_sweep_white);

                    //vypočítá pozici pro background
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());

                    //vypočítá pozici pro ikonku
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background = new ColorDrawable(getResources().getColor(R.color.transparent));
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);

            }

        }).attachToRecyclerView(recyclerView);
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
        setActiveMenuIcon(2);
    }

    /**
     * Nastavuje co se stane po kliknutí na kartu s uživatelem.
     */
    private void setCardsOnClickAction() {
        mAdapter_added_user.setOnItemClickListener(new Adapter_Added_User.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {

                String path = documentSnapshot.getReference().getPath(); //získá cestu ke kliknuté kartě
                //Toast.makeText(getContext(), "Position: " +position+" ID:"+ path, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), Activity_GiftTips.class).putExtra("path", path));
            }
        });
    }


}
