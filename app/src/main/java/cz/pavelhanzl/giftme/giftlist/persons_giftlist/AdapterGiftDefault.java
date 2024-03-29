package cz.pavelhanzl.giftme.giftlist.persons_giftlist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */


public class AdapterGiftDefault extends FirestoreRecyclerAdapter<Gift, AdapterGiftDefault.GiftHolder> {
    private OnItemClickListener mOnItemClickListener;
    private DocumentSnapshot mDeletedDocument;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterGiftDefault(@NonNull FirestoreRecyclerOptions<Gift> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GiftHolder holder, int position, @NonNull Gift model) { //co chceme umístit do jakého view v našem cardview layoutu
        holder.textViewName.setText(model.getName());
        holder.textViewPrice.setText(String.valueOf(model.getPrice()));
        holder.checkBoxBought.setChecked(model.isBought());

    }

    @NonNull
    @Override
    public GiftHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //onCreateViewHolder řeší jaký layout se má použít ; viewGroup je v našem případě recycleview
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_gift_default, viewGroup, false);
        return new GiftHolder(v);
    }

    /**
     * Odstaní položku na předané pozici v recycleview z databáze.
     * @param position
     */
    public void deleteItem(int position){
        mDeletedDocument = getSnapshots().getSnapshot(position);
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * Obnoví nedávno smazanou položku z databáze.
     */
    public void restoreItem(){
        //přidá smazanou položku zpět do databáze se stejným ID
        mDeletedDocument.getReference().set(mDeletedDocument.getData());
    }


    /**
     * Přesune položku na předané pozici z normálního giftlistu do archivního giftlistu zvolené osoby.
     * @param position
     */
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



    class GiftHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewPrice;
        CheckBox checkBoxBought;



        public GiftHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);
            textViewName = itemView.findViewById(R.id.card_giftDefault_name);
            textViewPrice = itemView.findViewById(R.id.card_giftDefault_price);
            checkBoxBought = itemView.findViewById(R.id.card_giftDefault_checkbox_bought);

            checkBoxBought.setOnClickListener(new View.OnClickListener() {
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
