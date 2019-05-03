package cz.pavelhanzl.giftme.giftlist;

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
import cz.pavelhanzl.giftme.stats.StatsManagerSingleton;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class AdapterName extends FirestoreRecyclerAdapter<Name, AdapterName.NameHolder> {
    private OnItemClickListener mOnItemClickListener;
    private DocumentSnapshot mDeletedDocument;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterName(@NonNull FirestoreRecyclerOptions<Name> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NameHolder holder, int position, @NonNull Name model) { //co chceme umístit do jakého view v našem cardview layoutu
        holder.textViewName.setText(model.getName());
        holder.textViewBudget.setText(String.valueOf(model.getBudget()));
    }

    @NonNull
    @Override
    public NameHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //jaký layout se má použít
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_name, viewGroup, false);
        return new NameHolder(v);
    }

    /**
     * Odstaní položku na předané pozici v recycleview z databáze. A znovu spustí získávání dat pro
     * statistiky.
     * @param position
     */
    public void deleteItem(int position){
        mDeletedDocument = getSnapshots().getSnapshot(position);
        getSnapshots().getSnapshot(position).getReference().delete();

        StatsManagerSingleton.getInstance().getStatsData(); //získávání dat pro statistiky
    }

    /**
     * Obnoví nedávno smazanou položku z databáze.
     */
    public void restoreItem(){
        //přidá smazanou položku zpět do databáze se stejným ID
        mDeletedDocument.getReference().set(mDeletedDocument.getData());
    }


    class NameHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewBudget;


        public NameHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);
            textViewName = itemView.findViewById(R.id.card_name_name);
            textViewBudget = itemView.findViewById(R.id.card_name_budget);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && mOnItemClickListener != null){ //noposition je konstanta pro -1, může nastat, když omylem klikneme, např. na odstraňovanou kartu uprostřed animace
                        mOnItemClickListener.OnItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    mOnItemClickListener.OnItemLongClick(getSnapshots().getSnapshot(position),position);
                    return false;
                }
            });


        }
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
        void OnItemLongClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
}
