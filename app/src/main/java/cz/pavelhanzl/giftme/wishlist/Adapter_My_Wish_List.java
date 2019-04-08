package cz.pavelhanzl.giftme.wishlist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import cz.pavelhanzl.giftme.R;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 */
public class Adapter_My_Wish_List extends FirestoreRecyclerAdapter<GiftTip, Adapter_My_Wish_List.GiftTipHolder> {
    private OnItemClickListener mOnItemClickListener;
    private DocumentSnapshot mDeletedDocument;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Adapter_My_Wish_List(@NonNull FirestoreRecyclerOptions<GiftTip> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GiftTipHolder holder, int position, @NonNull GiftTip model) { //co chceme umístit do jakého view v našem cardview layoutu
        holder.textViewName.setText(model.getName());

        //pokud description není null a pokud se nerovná prázdnému řetězci, tak zviditelni popis a nastavu mu hodnotu z modelu
        if (model.getDescription() != null) {
            if (!model.getDescription().equals("")) {
                holder.textViewDescription.setVisibility(View.VISIBLE);
                holder.textViewDescription.setText(model.getDescription());
            }
        }
    }

    @NonNull
    @Override
    public GiftTipHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //jaký layout se má použít
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_my_gift_tip, viewGroup, false);
        return new GiftTipHolder(v);
    }

    /**
     * Odstaní položku na předané pozici v recycleview z databáze.
     *
     * @param position
     */
    public void deleteItem(int position) {
        mDeletedDocument = getSnapshots().getSnapshot(position);
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * Obnoví nedávno smazanou položku z databáze.
     */
    public void restoreItem() {
        //přidá smazanou položku zpět do databáze se stejným ID
        mDeletedDocument.getReference().set(mDeletedDocument.getData());
    }


    class GiftTipHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDescription;


        public GiftTipHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);
            textViewName = itemView.findViewById(R.id.card_my_gift_tip_name);
            textViewDescription = itemView.findViewById(R.id.card_my_gift_tip_desription);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) { //noposition je konstanta pro -1, může nastat, když omylem klikneme, např. na odstraňovanou kartu uprostřed animace
                        mOnItemClickListener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
