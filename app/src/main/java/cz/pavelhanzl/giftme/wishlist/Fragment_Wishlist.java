package cz.pavelhanzl.giftme.wishlist;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.stats.StatsManagerSingleton;

public class Fragment_Wishlist extends Logic_DrawerFragment {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mMyGiftTipsReference;
    private Adapter_My_Wish_List mAdapter_my_wish_list;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(1);
        mView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        setUpFloatingButton();
        setUpRecyclerView();

        return mView;
    }

    private void getDataForStatistics() {
        //získá data potřebná pro fragment se statistikami (realizováno singletonem)
        StatsManagerSingleton.getInstance().getStatsData();
    }


    @Override
    public void onResume() {

        super.onResume();
        setActiveMenuIcon(1);
    }

    /**
     * Nastavuje recyclerView. Řadí podle jména.
     */
    private void setUpRecyclerView() {
        mMyGiftTipsReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("OwnGiftTips");
        Query query = mMyGiftTipsReference.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<GiftTip> options = new FirestoreRecyclerOptions.Builder<GiftTip>().setQuery(query, GiftTip.class).build();
        mAdapter_my_wish_list = new Adapter_My_Wish_List(options);

        RecyclerView recyclerView = mView.findViewById(R.id.activity_my_wish_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter_my_wish_list);

        deleteItemFromRecyclerView(recyclerView);
        setCardsOnClickAction();
    }

    /**
     * Odstraní položku z recyclerView při posunutí položky doprava nebo doleva.
     *
     * @param recyclerView
     */
    private void deleteItemFromRecyclerView(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;

            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                if (i == ItemTouchHelper.LEFT) {
                    mAdapter_my_wish_list.deleteItem(viewHolder.getAdapterPosition());
                    Toast.makeText(getContext(), getString(R.string.swipe_deleted), Toast.LENGTH_SHORT ).show();
                }


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
    /**
     * Znovu inicializuje obrazovku při statu této aktivity. Důléžité při přechodu zpět z vytvoření nové item, aby se znovu spustilo poslochání na Adaptéru.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAdapter_my_wish_list.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter_my_wish_list.stopListening();
    }

    /**
     * Nastaví floating button pro přidání dárku na giftlist. A v extra odešle ID otevřené osoby, pro kterou je určen otevřený giftlist.
     */
    private void setUpFloatingButton() {
        FloatingActionButton buttonAddGift = mView.findViewById(R.id.activity_my_wish_list_floatingButton_add_gift);
        buttonAddGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Activity_NewOwnGiftTip.class));
            }
        });
    }

    /**
     * Nastavuje co se stane po kliknutí na na checkbox u itemu.
     */
    private void setCardsOnClickAction() {
        mAdapter_my_wish_list.setOnItemClickListener(new Adapter_My_Wish_List.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {

            }
        });
    }

}
