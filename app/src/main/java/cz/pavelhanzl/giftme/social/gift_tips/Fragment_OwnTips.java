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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.wishlist.GiftTip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_OwnTips.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_OwnTips#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_OwnTips extends Fragment {
    private String mSelectedUserEmail;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private CollectionReference mOwnGiftTipsPublicCollection;
    private Adapter_OwnTips mAdapter_ownTips;
    private View mView;

    private OnFragmentInteractionListener mListener;

    public Fragment_OwnTips() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_OwnTips.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_OwnTips newInstance(String param1, String param2) {
        Fragment_OwnTips fragment = new Fragment_OwnTips();
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
        mView = inflater.inflate(R.layout.fragment_own_tips, container, false);
        Log.d("own tips","on create view, selected user email:" + mSelectedUserEmail);


        setUpRecyclerView();

        return mView;
    }

    private void setUpRecyclerView() {
        mOwnGiftTipsPublicCollection = mDb.collection("Users").document(mSelectedUserEmail).collection("OwnGiftTips");
        Query query = mOwnGiftTipsPublicCollection.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<GiftTip> options = new FirestoreRecyclerOptions.Builder<GiftTip>().setQuery(query,GiftTip.class).build();
        mAdapter_ownTips = new Adapter_OwnTips(options);
        RecyclerView recyclerView =  mView.findViewById(R.id.frag_own_tips_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter_ownTips);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter_ownTips.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter_ownTips.stopListening();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

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
