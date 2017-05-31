package net.sinancaliskan.maestropanel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
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
import net.sinancaliskan.maestropanel.dialog.AliasDelete;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Alias;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SinanCaliskan on 4.03.2017.
 */
                               //reference class                                 //interface methods
public class AliasAdapter extends RecyclerView.Adapter<AliasAdapter.AliasHolder> implements SwipeableItemAdapter<AliasAdapter.AliasHolder> {
    static final float OPTIONS_AREA_PROPORTION = 1.0f;                                              //swipe fill size
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;                                                //maximum swipe size when it occurs item will remove

    final String key= SharedInformation.getData("serverKey");                                       //getting server key
    private IDomain service =null;                                                                  //define domain

    //region viewHolder
    public static class AliasHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container,delete;                                                            //define swipe view,delete view
        Alias model;//define row Alias model
        TextView alias;//define textview for alias object
        float lastSwipeAmount;//last swipe amount for row
        public AliasHolder(View itemView)
        {
            super(itemView);                                                                        //calling default AbstractSwipeableItemViewHolder constructor

            swipeable_container=itemView.findViewById(R.id.swipeable_container);                    //assign swipe view in layout
            delete=itemView.findViewById(R.id.delete);                                              //assign delete view in layout

            alias=(TextView) itemView.findViewById(R.id.txtAlias);                                  //assign alias textview in layout

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AliasDelete(context,adapter,model.name);                                    //creating AliasDelete dialog with parameters
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

            if (delete.getWidth() == 0) {                                                           //set options sizes
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
            }
            delete.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);               //set option location

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {                                      //when swipe view -1,1 lower than horizontal amount views will invisible
                swipeable_container.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            } else {                                                                                //when swipe view -1,1 higher than horizontal amount views will visible
                swipeable_container.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
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
    static String TAG=AliasAdapter.class.getSimpleName();
    private static ArrayList<Alias> models=new ArrayList<>();                                       //create for storing AliasObject in list
    public static Domain domain;                                                                    //define domain for reuse
    private static Context context;                                                                 //define context for reuse
    private static AliasAdapter adapter;                                                            //define adapter for reuse
    private ProgressDialog progressDialog;                                                          //define ProgressDialog for showing refresh animation

    private RecyclerView recyclerView;                                                              //define RecyclerView for gettin on activity or fragment
    private RecyclerViewSwipeManager swipeManager;                                                  //define RecyclerViewSwipeManager for RecyclerView
    private RecyclerView.LayoutManager layoutManager;                                               //define RecyclerView.LayoutManager for RecyclerView
    private SwipeRefreshLayout swipeRefreshLayout;                                                  //define SwipeRefreshLayout for gettin on activity or fragment it will use for refresh page

    public AliasAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,Domain model){//custom constructor
        this.domain=model;                                                                          //assign context for static context
        this.context=context;                                                                       //assign context for static context
        this.swipeRefreshLayout=swipeRefreshLayout;                                                 //assign swipeRefreshLayout for adapter swipeRefreshLayout
        this.recyclerView=recyclerView;                                                             //assign recyclerView for adapter recyclerView

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
        progressDialog.setMessage(context.getString(R.string.alias)+ context.getString(R.string.isLoading));//setting message on resource file
        progressDialog.setCancelable(false);                                                        //assign progressDialog is not cancelable
        progressDialog.show();                                                                      //showing progressDialog
        refresh();                                                                                  //calling refresh method
    }
    @Override                                                                                       //return stable id
    public long getItemId(int position) {
        return models.get(position).id;
    }
    @Override                                                                                       //create new row for Alias object
    public AliasHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alias,parent,false);
        return new AliasHolder(row);
    }
    @Override
    public void onBindViewHolder(AliasHolder holder, int position) {
        Alias model=models.get(position);
        holder.model=model;

        holder.alias.setText(model.name);

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
            if (models.get(i).name==model)
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
    public SwipeResultAction onSwipeItem(AliasHolder holder, int position, int result) {            //swipe action method it calls actions
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
    public int onGetSwipeReactionType(AliasHolder holder, int position, int x, int y) {             //interface method returns swipe reaction type
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }
    @Override
    public void onSetSwipeBackground(AliasHolder holder, int position, int type) {                  //set swipe view backcolor when doesnt have parent backcolor
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }
    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {            //left pinning action
        AliasAdapter adapter;
        int position;

        public SwipeLeftPinningAction(AliasAdapter adapter, int position) {
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
        AliasAdapter adapter;
        int position;

        public SwipeCancelAction(AliasAdapter adapter, int position) {
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
        service = API.getClient().create(IDomain.class);                                            //creating new Domain service api
        Call<Reply> call=service.GetDomainAliases(key,domain.name);                                 //creating GetList method with api key
        call.enqueue(new Callback<Reply>() {                                                        //adding queue to callback
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {                    //when callback return response
                ObjectMapper mapper=new ObjectMapper();                                             //creating objectmapper for convert json to class
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);         //set doesnt fail when doesnt parse to class
                ArrayList<Alias> objects= ((ArrayList<Alias>) response.body().details);             //creating list with json
                models.clear();
                for (int i = 0; i < objects.size(); i++)
                {
                    String value=mapper.convertValue(objects.get(i),String.class);
                    Alias model=new Alias();
                    model.name=value;
                    if (!checkItemExist(model)) {                                                   //check object in list
                        models.add(model);                                                          //add in list
                        adapter.notifyDataSetChanged();                                             //notify adapter data set changed
                        recyclerView.scrollToPosition(0);                                           //set recyclerView scroll position
                    }
                }
                swipeRefreshLayout.setRefreshing(false);                                            //set swipeRefreshLayout setRefreshing status
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Reply> call, Throwable throwable) {                          //when callback return failure
                call.cancel();
                Toast.makeText(context,context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();//showing error message
                if (progressDialog!=null)
                    progressDialog.dismiss();                                                       //progressDialog close
            }
        });
    }
    boolean checkItemExist(Alias model){                                                            //checking object in the list
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).name.equals(model.name)){exist=true;break;}
        }
        return exist;
    }
    //endregion
}
