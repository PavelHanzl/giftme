package cz.pavelhanzl.giftme.social.gift_tips.own_gift_tips;

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
import cz.pavelhanzl.giftme.wishlist.GiftTip;

/**
 * Adaptér dostává data ze zdroje dat do recycleviev. Extendujeme FirestoreRecyclerAdapter,
 * který extenduje obyčejný RecyclerView a stará se např. o nahrávání dat z firestore,
 * reagování na změny v datasetu atp...
 */
public class AdapterOwnTips extends FirestoreRecyclerAdapter<GiftTip, AdapterOwnTips.OwnTipsHolder> {
    private OnItemClickListener mOnItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterOwnTips(@NonNull FirestoreRecyclerOptions<GiftTip> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OwnTipsHolder holder, int position, @NonNull GiftTip model) { //co chceme umístit do jakého view v našem cardview layoutu
        /*Pokud model.getBookedBy() a aktivní přihlášený uživatel nejsou null, tak se ptá,
         zdali se hodnota bookedBy v GiftTip modelu rovná přihlášenému uživateli - pokud se nerovná,
         tak skryje kartu (nastaví její velikost na 0), pokud se rovná, tak nastaví velikost
         karty na původní hodnoty a zobrazí ji. */
        if (model.getBookedBy() == null ? FirebaseAuth.getInstance().getCurrentUser().getEmail() == null : !model.getBookedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            //skryje kompletně celou položku - původní záměr aplikace (proto zakomentováno)
            //holder.rootView.setLayoutParams(holder.zeroParams);

            holder.checkBoxBookedByYou.setVisibility(View.GONE); //skryje check box pro bookování
            holder.linearLayoutBookedBy.setVisibility(View.VISIBLE); //zobrazí informace o tom,  jaký email si rezervoval dárek
            holder.textViewBookedBy.setText(model.getBookedBy()); //nastaví email, kdo si rezervoval dárek

            holder.itemView.setBackgroundColor(Color.argb(100, 255, 255, 255)); //nastaví šedý background

            holder.textViewName.setText(model.getName());
            getDescription(holder, model);


        } else {
            //holder.rootView.setLayoutParams(holder.defaultParams);

            holder.checkBoxBookedByYou.setVisibility(View.VISIBLE); //zobrazí check box pro bookování
            holder.linearLayoutBookedBy.setVisibility(View.GONE); //skryje informace o tom,  jaký email si rezervoval dárek

            holder.itemView.setBackgroundColor(Color.argb(255, 255, 255, 255)); //nastaví bílý background

            holder.textViewName.setText(model.getName());
            holder.checkBoxBookedByYou.setChecked(model.isBooked());
            getDescription(holder, model);
        }
    }

    /**
     * Pokud description v modelu není null a pokud se nerovná prázdnému řetězci, tak zviditelní
     * popis a nastaví mu hodnotu z modelu z databáze, pokud naopak je null nebo prázdný řetězec,
     * pak jej skryje i s titlem
     *
     * @param holder holder předaný z onBindHolder
     * @param model  model, ve kterém se nachází description
     */
    private void getDescription(@NonNull OwnTipsHolder holder, @NonNull GiftTip model) {
        if (model.getDescription() != null) {
            if (!model.getDescription().equals("")) {
                holder.textViewDescription.setVisibility(View.VISIBLE);
                holder.textViewDescription.setText(model.getDescription());
                holder.textViewDescriptionTitle.setVisibility(View.VISIBLE);
            } else {
                holder.textViewDescription.setVisibility(View.GONE);
                holder.textViewDescriptionTitle.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public OwnTipsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //onCreateViewHolder řeší jaký layout se má použít ; viewGroup je v našem případě recycleview
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_my_gift_tip_public, viewGroup, false);
        return new OwnTipsHolder(v);
    }

    /**
     * Odstaní položku na předané pozici v recycleview z databáze.
     * @param position
     */
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    class OwnTipsHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewDescriptionTitle;
        TextView textViewDescription;
        TextView textViewBookedBy;
        CheckBox checkBoxBookedByYou;
        LinearLayout linearLayoutBookedBy;

        public LinearLayout.LayoutParams zeroParams;
        public ViewGroup.LayoutParams defaultParams;
        public CardView rootView;



        public OwnTipsHolder(@NonNull View itemView) { //konstruktor ;  itemView který jsme dostali je instance karty jako takové
            super(itemView);

            zeroParams = new LinearLayout.LayoutParams(0, 0); //nastaví nulovou velikost pro tento itemview - používá se v metodě OnBindViewHolder
            rootView = itemView.findViewById(R.id.card_my_gift_tip_public_root_view); //nejvíce vnější view z cardlayoutu - používá se v metodě OnBindViewHolder
            defaultParams = rootView.getLayoutParams(); //původní velikost view před zmenšením na 0 - používá se v metodě OnBindViewHolder

            textViewName = itemView.findViewById(R.id.card_my_gift_tip_public_name);
            textViewDescription = itemView.findViewById(R.id.card_my_gift_tip_public_desription);
            textViewDescriptionTitle=itemView.findViewById(R.id.card_my_gift_tip_public_desription_title);
            checkBoxBookedByYou = itemView.findViewById(R.id.card_my_gift_tip_public_checkbox_booked_by_you);
            linearLayoutBookedBy = itemView.findViewById(R.id.card_my_gift_tip_public_booked_by_linear_layout);
            textViewBookedBy = itemView.findViewById(R.id.card_my_gift_tip_public_textview_booked_by);

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
