package cz.pavelhanzl.giftme;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 */
public class Adapter_Gift_Default extends FirestoreRecyclerAdapter<Gift, Adapter_Gift_Default.GiftHolder> {
    private OnItemClickListener mOnItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Adapter_Gift_Default(@NonNull FirestoreRecyclerOptions<Gift> options) {
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

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();


    }




    class GiftHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewPrice;
        CheckBox checkBoxBought;
        Button buttonArchivate;


        public GiftHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);
            textViewName = itemView.findViewById(R.id.card_giftDefault_name);
            textViewPrice = itemView.findViewById(R.id.card_giftDefault_price);
            checkBoxBought = itemView.findViewById(R.id.card_giftDefault_checkbox_bought);
//            buttonArchivate = itemView.findViewById(R.id.card_giftDefault_button_archivate);


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