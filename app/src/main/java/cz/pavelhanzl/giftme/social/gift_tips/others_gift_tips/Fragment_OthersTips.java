package cz.pavelhanzl.giftme.social.gift_tips.others_gift_tips;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
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

import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.social.gift_tips.own_gift_tips.Fragment_OwnTips;
import cz.pavelhanzl.giftme.wishlist.GiftTip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_OthersTips.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_OthersTips#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_OthersTips extends Fragment {
    private String mSelectedUserEmail;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mOthersGiftTipsPublicCollection;
    private Adapter_OthersTips mAdapter_othersTips;
    private View mView;

    private OnFragmentInteractionListener mListener;

    public Fragment_OthersTips() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_OthersTips.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_OthersTips newInstance(String param1, String param2) {
        Fragment_OthersTips fragment = new Fragment_OthersTips();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final Bundle args = getArguments();
            mSelectedUserEmail = args.getString("selectedEmail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_others_tips, container, false);
        setUpFloatingButton();
        setUpRecyclerView();
        return mView;

    }

    private void setUpRecyclerView() {
        mOthersGiftTipsPublicCollection = mDb.collection("Users").document(mSelectedUserEmail).collection("OthersGiftTips");
        Query query = mOthersGiftTipsPublicCollection.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<GiftTip> options = new FirestoreRecyclerOptions.Builder<GiftTip>().setQuery(query,GiftTip.class).build();
        mAdapter_othersTips = new Adapter_OthersTips(options);
        RecyclerView recyclerView =  mView.findViewById(R.id.frag_others_tips_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter_othersTips);

        deleteItemFromRecyclerView(recyclerView);
        setCardsOnClickAction();
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
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {


                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getContext());
                builder.setTitle(getString(R.string.frag_others_gifttips_alert_title));
                builder.setMessage(getString(R.string.frag_others_gifttips_alert_message));
                builder.setIcon(R.drawable.ic_warning);
                builder.setNegativeButton(getString(R.string.frag_others_gifttips_alert_button_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                Toast.makeText(getContext(),"No is clicked",Toast.LENGTH_LONG).show();
                                mAdapter_othersTips.notifyDataSetChanged(); // refreshne recycleview
                            }
                        });
                builder.setPositiveButton(getString(R.string.frag_others_gifttips_alert_button_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mAdapter_othersTips.deleteItem(viewHolder.getAdapterPosition());
                                snackbarUndoDelete();
                            }
                        });
                builder.show();














            }

            private void snackbarUndoDelete() {
                Snackbar snackbar = Snackbar
                        .make(getView().findViewById(R.id.coordinatorLayout_others_tips), getString(R.string.swipe_deleted_for_all), 6000);
                snackbar.setAction(getString(R.string.swipe_deleted_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText( getContext(),  getString(R.string.snackbar_restored), Toast.LENGTH_LONG ).show();
                        mAdapter_othersTips.restoreItem();

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


    /**
     * Nastaví floating button pro přidání gifttipu.
     */
    private void setUpFloatingButton() {
        FloatingActionButton buttonAddGiftTip = mView.findViewById(R.id.frag_others_tips_floatingButton_add_gifttip);
        buttonAddGiftTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Activity_NewOthersGiftTip.class).putExtra("selectedUserEmail", mSelectedUserEmail));
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAdapter_othersTips.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter_othersTips.stopListening();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Fragment_OwnTips.OnFragmentInteractionListener) {
            mListener = (Fragment_OthersTips.OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Nastavuje co se stane po kliknutí na na checkbox u itemu.
     */
    //TODO: přejmenovat tuto metodu, aby odpovídala svému záměru
    private void setCardsOnClickAction() {
        mAdapter_othersTips.setOnItemClickListener(new Adapter_OthersTips.OnItemClickListener() {


            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                GiftTip giftTip = documentSnapshot.toObject(GiftTip.class);

                //pokud není checkbox "bookedBy" zaškrtlý, tak ho zaškrtne a naopak...
                if(giftTip.getBookedBy()==null){
                    giftTip.setBookedBy(mAuth.getCurrentUser().getEmail());
                    Toast.makeText(getContext(),getString(R.string.checkbox_isBookedByYou_true_toast), Toast.LENGTH_SHORT).show();
                }else {
                    giftTip.setBookedBy(null);
                    Toast.makeText(getContext(),getString(R.string.checkbox_isBookedByYou_false_toast), Toast.LENGTH_SHORT).show();
                }

                documentSnapshot.getReference().set(giftTip);

            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
