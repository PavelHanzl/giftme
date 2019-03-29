package cz.pavelhanzl.giftme.social.gift_tips;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Gift;
import cz.pavelhanzl.giftme.wishlist.GiftTip;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 */
public class Adapter_OwnTips extends FirestoreRecyclerAdapter<GiftTip, Adapter_OwnTips.OwnTipsHolder> {
    private OnItemClickListener mOnItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Adapter_OwnTips(@NonNull FirestoreRecyclerOptions<GiftTip> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OwnTipsHolder holder, int position, @NonNull GiftTip model) { //co chceme umístit do jakého view v našem cardview layoutu
        holder.textViewName.setText(model.getName());
        holder.checkBoxBookedByYou.setChecked(model.isBooked());

    }

    @NonNull
    @Override
    public OwnTipsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //onCreateViewHolder řeší jaký layout se má použít ; viewGroup je v našem případě recycleview
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_my_gift_tip_public, viewGroup, false);
        return new OwnTipsHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();


    }

    public void archiveItem(int position){
        //získá objekt dárku ze snapshotu na dané pozici
        Gift gift =getSnapshots().getSnapshot(position).toObject(Gift.class);

        //získá cestu k snapshotu na dané pozici a z něj vytvoří cestu do kolekce Archive
        CollectionReference giftlistArchive = FirebaseFirestore.getInstance().collection(getSnapshots().getSnapshot(position).getReference().getParent().getParent().collection("GiftlistArchive").getPath());

        //přidá objekt do předem definované kolekce v db
        giftlistArchive.add(gift);

        //smaže objekt z původního umístění;
        getSnapshots().getSnapshot(position).getReference().delete();



    }



    class OwnTipsHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        CheckBox checkBoxBookedByYou;



        public OwnTipsHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);
            textViewName = itemView.findViewById(R.id.card_my_gift_tip_public_name);
            checkBoxBookedByYou = itemView.findViewById(R.id.card_my_gift_tip_public_checkbox_booked_by_you);



            checkBoxBookedByYou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && mOnItemClickListener != null){ //noposition je konstanta pro -1, může nastat, když omylem klikneme, např. na odstraňovanou kartu uprostřed animace
                        mOnItemClickListener.OnItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
}
