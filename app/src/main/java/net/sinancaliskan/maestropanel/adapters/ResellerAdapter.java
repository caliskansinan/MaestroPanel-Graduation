package net.sinancaliskan.maestropanel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.dialog.ResellerDelete;
import net.sinancaliskan.maestropanel.interfaces.IReseller;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.Reseller;
import net.sinancaliskan.maestropanel.ui.MainActivity;
import net.sinancaliskan.maestropanel.ui.ResellerActivity;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Dictionary;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SinanCaliskan on 4.03.2017.
 */

public class ResellerAdapter extends RecyclerView.Adapter<ResellerAdapter.ResellerHolder> implements SwipeableItemAdapter<ResellerAdapter.ResellerHolder> {
    static final float OPTIONS_AREA_PROPORTION = 1.0f;                                              //swipe fill size
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;                                                //maximum swipe size when it occurs item will remove

    final String key= SharedInformation.getData("serverKey");                                       //getting server key
    private IReseller service =null;                                                                //define domain service

    //creating row for reseller object
    //region viewHolder
    public static class ResellerHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container,information,delete;
        Reseller model;
        View status;
        TextView owner,username,expirationDate;
        float lastSwipeAmount;//last swipe amount for row
        public ResellerHolder(final View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);                    //assign swipe view in layout
            information=itemView.findViewById(R.id.information);                                    //assign information view in layout
            delete=itemView.findViewById(R.id.delete);

            status= itemView.findViewById(R.id.status);
            owner=(TextView) itemView.findViewById(R.id.txtOwner);
            username=(TextView) itemView.findViewById(R.id.txtUsername);
            expirationDate=(TextView) itemView.findViewById(R.id.txtExpirationDate);

            swipeable_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main=(MainActivity) itemView.getContext();                         //assign MainActivity with itemview context it need type of MainActivity context
                    Intent intent=new Intent(main, ResellerActivity.class);                         //creating new intent typeof ResellerActivity
                    intent.putExtra("model",model);                                                 //adding model for ResellerActivity for reuse reseller object
                    main.startActivity(intent);
                }
            });

            information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main=(MainActivity) itemView.getContext();                         //assign MainActivity with itemview context it need type of MainActivity context
                    Intent intent=new Intent(main, ResellerActivity.class);                         //creating new intent typeof ResellerActivity
                    intent.putExtra("model",model);                                                 //adding model for ResellerActivity for reuse reseller object
                    main.startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ResellerDelete(context,adapter,model.username);                             //creating ResellerDelete dialog with context,ResellerAdapter,username
                }
            });
        }
        @Override
        public View getSwipeableContainerView() {
            return swipeable_container;                                                             //assign swipeable view for swipe operations
        }
        @Override
        public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping) {
            int itemWidth = itemView.getWidth();                                                    //getting horizontal screen size
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 1;                        //define each option size
            int offset = (int) (optionItemWidth + 0.5f);                                            //define offset with option size
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;

            if (delete.getWidth() == 0) {                                                      //set options sizes
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(information, (int) (optionItemWidth + 0.5f));
            }
            delete.setTranslationX(-(int) (p * optionItemWidth * 2 + 0.5f) + offset);          //set option location
            information.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);          //set option location

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {                                      //when swipe view -1,1 lower than horizontal amount views will invisible
                swipeable_container.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                information.setVisibility(View.INVISIBLE);
            } else {                                                                                //when swipe view -1,1 higher than horizontal amount views will visible
                swipeable_container.setVisibility(View.VISIBLE);
                delete.setVisibility(View.INVISIBLE);
                information.setVisibility(View.VISIBLE);
            }

            lastSwipeAmount = horizontalAmount;                                                     //update last swipe amount
        }
        private static void setLayoutWidth(View v, int width) {                                     //set View width
            ViewGroup.LayoutParams lp = v.getLayoutParams();                                        //read view layout parameter
            lp.width = width;                                                                       //set new width
            v.setLayoutParams(lp);                                                                  //update layout parameter
        }
    }
    //endregion

    //region adapter


    static String TAG=ResellerAdapter.class.getSimpleName();
    private static ArrayList<Reseller> models=new ArrayList<>();                                    //create for storing DomainObject in list
    private static Context context;                                                                 //define context for reuse
    private static ResellerAdapter adapter;                                                           //define adapter for reuse
    private ProgressDialog progressDialog;                                                          //define ProgressDialog for showing refresh animation

    private RecyclerView recyclerView;                                                              //define RecyclerView for gettin on activity or fragment
    private RecyclerViewSwipeManager swipeManager;                                                  //define RecyclerViewSwipeManager for RecyclerView
    private RecyclerView.LayoutManager layoutManager;                                               //define RecyclerView.LayoutManager for RecyclerView
    private SwipeRefreshLayout swipeRefreshLayout;

    public ResellerAdapter(Context context,SwipeRefreshLayout swipeRefreshLayout,RecyclerView recyclerView){//custom constructor
        this.context=context;                                                                       //assing context for static context
        this.swipeRefreshLayout=swipeRefreshLayout;                                                 //assing swipeRefreshLayout for adapter swipeRefreshLayout
        this.recyclerView=recyclerView;                                                             //assing recyclerView for adapter recyclerView

        setHasStableIds(true);                                                                      //it define to swipe view each object has own has stable id

        //this method listen refresh swipe when swipe it call refresh method
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {refresh();    }
        });
        swipeRefreshLayout.setColorSchemeResources(                                                 //assign loading spin showing colors on resource file
                R.color.blue_bright,
                R.color.green_light,
                R.color.orange_light,
                R.color.red_light
        );
        swipeManager=new RecyclerViewSwipeManager();                                                //create new RecyclerViewSwipeManager for swipe actions
        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);        //create LinearLayoutManager for recyclerView
        recyclerView.setLayoutManager(layoutManager);                                               //assign to recyclerView

        adapter=this;                                                                               //assign static adapter
        recyclerView.setAdapter(swipeManager.createWrappedAdapter(this));                           //assign recycler view adapter DomainAdapter
        recyclerView.setItemAnimator(new SwipeDismissItemAnimator());                               //creating animator for swipe view
        swipeManager.attachRecyclerView(recyclerView);                                              //attach recyclerView to swipeManager
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(context,R.drawable.list_divider),true)); //row divider line


        progressDialog=new ProgressDialog(context);                                                 //creating new progressDialog for showing loading actions
        progressDialog.setMessage(context.getString(R.string.reseller)+ context.getString(R.string.isLoading));                                      //setting message on resource file
        progressDialog.setCancelable(false);                                                        //assign progressDialog is not cancelable
        progressDialog.show();                                                                      //showing progressDialog
        refresh();                                                                                  //calling refresh method
    }
    @Override                                                                                       //return stable id
    public long getItemId(int position) {return models.get(position).id;}
    @Override
    public ResellerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reseller,parent,false);
        return new ResellerHolder(row);
    }
    //set new row object details
    @Override
    public void onBindViewHolder(ResellerHolder holder, int position) {
        Reseller model=models.get(position);
        holder.model=model;
        if (model.status==1)
            holder.status.setBackgroundColor(Color.GREEN);
        else
            holder.status.setBackgroundColor(Color.RED);
        holder.owner.setText(model.firstName + " " + model.lastName);
        holder.username.setText(model.username);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date=model.expirationDate.replace("/Date(","").replace(")/","");
        holder.expirationDate.setText(dateFormat.format(Long.valueOf(date)));

        holder.setMaxLeftSwipeAmount(-OPTIONS_AREA_PROPORTION);                                     //set max left swipe amount
        holder.setMaxRightSwipeAmount(0);                                                           //set max right swipe amount
        holder.setSwipeItemHorizontalSlideAmount(
                model.pinned ? -OPTIONS_AREA_PROPORTION : 0);                                       //set default is pinned or not
    }
    @Override                                                                                       //returns list of objects count
    public int getItemCount() {return models.size();}
    public int getItemPosition(String model){                                                       //return object item position in list
        int position=-1;
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).username.toString().equals(model))
                return i;
        }
        return position;
    }
    public void removeItem(int position){                                                           //remove item in list position
        models.remove(position);                                                                    //removing in list
        notifyItemRemoved(position);                                                                //notify adapter to removed object
        notifyItemRangeChanged(position,models.size());                                             //notify itemrange change position between model.size
    }
    public void removeItem(String model){                                                           //remove item in list with string
        int position=getItemPosition(model);
        if (position>-1)
            removeItem(position);
    }
    @Override
    public SwipeResultAction onSwipeItem(ResellerHolder holder, int position, int result) {           //swipe action method it calls actions
        if (result == Swipeable.RESULT_SWIPED_LEFT) {
            if (holder.lastSwipeAmount < (-REMOVE_ITEM_THRESHOLD)) {
                return new SwipeCancelAction(this, position);
            } else {
                return new SwipeLeftPinningAction(this, position);
            }
        } else {
            return new SwipeCancelAction(this, position);
        }
    }
    @Override
    public int onGetSwipeReactionType(ResellerHolder holder, int position, int x, int y) {            //interface method returns swipe reaction type
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }
    @Override
    public void onSetSwipeBackground(ResellerHolder holder, int position, int type) {                 //set swipe view backcolor when doesnt have parent backcolor
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }
    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {            //left pinning action
        ResellerAdapter adapter;
        int position;

        public SwipeLeftPinningAction(ResellerAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }
        @Override
        protected void onPerformAction() {
            adapter.models.get(position).pinned = true;
            adapter.notifyItemChanged(position);
        }
    }
    static class SwipeCancelAction extends SwipeResultActionDefault {                               //swipe cancel action
        ResellerAdapter adapter;
        int position;

        public SwipeCancelAction(ResellerAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }
        @Override
        protected void onPerformAction() {
            adapter.models.get(position).pinned = false;
            adapter.notifyItemChanged(position);
        }
    }
    //endregion

    //region refresh
    public void refresh() {

        service = API.getClient().create(IReseller.class);
        Call<Reply> call=service.GetResellers(key);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ArrayList<Reseller> objects= ((ArrayList<Reseller>) response.body().details);
                ObjectMapper mapper=new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                for (int i = 0; i < objects.size(); i++)
                {
                    Reseller model=mapper.convertValue(objects.get(i),Reseller.class);
                    if (!checkItemExist(model)) {
                        models.add(model);
                        adapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Reply> call, Throwable throwable) {
                call.cancel();
                Toast.makeText(context,context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
    }

    boolean checkItemExist(Reseller model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).id==model.id){exist=true;break;}
        }
        return exist;
    }

    //endregion
}
