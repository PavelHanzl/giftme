package cz.pavelhanzl.giftme.social.gift_tips;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
       Log.d("Others tip","on create view, selected user email: " + mSelectedUserEmail);
        mView = inflater.inflate(R.layout.fragment_others_tips, container, false);

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

        setCardsOnClickAction();
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
