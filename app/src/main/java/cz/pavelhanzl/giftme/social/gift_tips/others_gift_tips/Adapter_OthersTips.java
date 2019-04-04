package cz.pavelhanzl.giftme.social.gift_tips.others_gift_tips;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.stats.StatsManagerSingleton;
import cz.pavelhanzl.giftme.wishlist.GiftTip;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 */
public class Adapter_OthersTips extends FirestoreRecyclerAdapter<GiftTip, Adapter_OthersTips.OthersTipsHolder> {
    private OnItemClickListener mOnItemClickListener;
    private DocumentSnapshot mDeletedDocument;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Adapter_OthersTips(@NonNull FirestoreRecyclerOptions<GiftTip> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OthersTipsHolder holder, int position, @NonNull GiftTip model) { //co chceme umístit do jakého view v našem cardview layoutu
        String email=model.getBookedBy();

        //pokud model.getBookedBy() a aktivní přihlášený uživatel nejsou null, tak se ptá zda-li se hodnota bookedBy v gifttipu rovná přihlášenému
        //uživateli - pokud se nerovná, tak skryje kartu (nastaví její velikost na 0), pokud se rovná, tak nastaví velikost karty na původní hodnoty a zobrazí ji
        if (model.getBookedBy()==null ? FirebaseAuth.getInstance().getCurrentUser().getEmail()==null : !model.getBookedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            //holder.rootView.setLayoutParams(holder.zeroParams);  //skryje celou položku - splňuje zadání BP
            holder.checkBoxBookedByYou.setVisibility(View.GONE); //skryje check box pro bookování
            holder.linearLayoutBookedBy.setVisibility(View.VISIBLE); //zobrazí informace o tom,  jaký email si rezervoval dárek
            holder.textViewBookedBy.setText(model.getBookedBy()); //nastaví email, kdo si rezervoval dárek

            holder.itemView.setBackgroundColor(Color.argb(100,255,255,255)); //nastaví šedý background

            holder.textViewName.setText(model.getName());
            holder.textViewTipBy.setText(model.getTipBy());
        }else {
            //holder.rootView.setLayoutParams(holder.defaultParams);

            holder.checkBoxBookedByYou.setVisibility(View.VISIBLE); //zobrazí check box pro bookování
            holder.linearLayoutBookedBy.setVisibility(View.GONE); //skryje informace o tom,  jaký email si rezervoval dárek

            holder.itemView.setBackgroundColor(Color.argb(255,255,255,255)); //nastaví bílý background

            holder.textViewName.setText(model.getName());
            holder.textViewTipBy.setText(model.getTipBy());
            holder.checkBoxBookedByYou.setChecked(model.isBooked());

        }

    }

    @NonNull
    @Override
    public OthersTipsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //onCreateViewHolder řeší jaký layout se má použít ; viewGroup je v našem případě recycleview
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_others_gift_tip_public, viewGroup, false);
        return new OthersTipsHolder(v);
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



    class OthersTipsHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewTipBy;
        TextView textViewBookedBy;
        CheckBox checkBoxBookedByYou;
        LinearLayout linearLayoutBookedBy;

        public LinearLayout.LayoutParams zeroParams;
        public ViewGroup.LayoutParams defaultParams;
        public CardView rootView;



        public OthersTipsHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);

            zeroParams = new LinearLayout.LayoutParams(0, 0); //nastaví nulovou velikost pro tento itemview - používá se v metodě OnBindViewHolder
            rootView = itemView.findViewById(R.id.card_others_gift_tip_public_root_view); //nejvíce vnější view z cardlayoutu - používá se v metodě OnBindViewHolder
            defaultParams = rootView.getLayoutParams(); //původní velikost view před zmenšením na 0 - používá se v metodě OnBindViewHolder

            textViewName = itemView.findViewById(R.id.card_others_gift_tip_public_name);
            textViewTipBy = itemView.findViewById(R.id.card_others_gift_tip_public_textview_tip_by);
            checkBoxBookedByYou = itemView.findViewById(R.id.card_others_gift_tip_public_checkbox_booked_by_you);
            linearLayoutBookedBy = itemView.findViewById(R.id.card_others_gift_tip_public_booked_by_linear_layout);
            textViewBookedBy = itemView.findViewById(R.id.card_others_gift_tip_public_textview_booked_by);



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
