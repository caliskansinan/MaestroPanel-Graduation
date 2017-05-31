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
import net.sinancaliskan.maestropanel.interfaces.IServer;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.models.pojo.ServerObjects.Server;
import net.sinancaliskan.maestropanel.ui.MainActivity;
import net.sinancaliskan.maestropanel.ui.ServerActivity;
import net.sinancaliskan.maestropanel.ui.ServerIPActivity;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SinanCaliskan on 4.03.2017.
 */

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerHolder> implements SwipeableItemAdapter<ServerAdapter.ServerHolder>{
    static final float OPTIONS_AREA_PROPORTION = 1.0f;                                              //swipe fill size
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;                                                //maximum swipe size when it occurs item will remove

    final String key= SharedInformation.getData("serverKey");                                       //getting server key
    private IServer service =null;

    //region viewHolder
    public static class ServerHolder extends AbstractSwipeableItemViewHolder{
        View swipeable_container,ipAddress,information;
        Server model;
        View status;
        TextView computerName,operatingSystem,ip;
        float lastSwipeAmount;//last swipe amount for row
        public ServerHolder(final View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);                    //assign swipe view in layout
            ipAddress=itemView.findViewById(R.id.ipAddress);
            information=itemView.findViewById(R.id.information);                                    //assign information view in layout

            status=itemView.findViewById(R.id.status);
            computerName=(TextView) itemView.findViewById(R.id.txtComputerName);
            operatingSystem=(TextView) itemView.findViewById(R.id.txtOperatingSystem);
            ip=(TextView) itemView.findViewById(R.id.txtIPAddress);

            swipeable_container.setOnClickListener(new View.OnClickListener() {     //creating swipe view click listener
                @Override
                public void onClick(View v) {
                    MainActivity main=(MainActivity) itemView.getContext();                         //assign MainActivity with itemview context it need type of MainActivity context
                    Intent intent=new Intent(main, ServerActivity.class);                           //creating new intent typeof DomainActivity
                    intent.putExtra("model",model);                                                 //adding model for DomainActivity for reuse domain object
                    main.startActivity(intent);                                                     //starting DomainActivity
                }
            });

            ipAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main=(MainActivity) itemView.getContext();                         //assign MainActivity with itemview context it need type of MainActivity context
                    Intent intent=new Intent(main, ServerIPActivity.class);                           //creating new intent typeof DomainActivity
                    intent.putExtra("model",model);                                                 //adding model for DomainActivity for reuse domain object
                    main.startActivity(intent);                                                     //starting DomainActivity
                }
            });

            information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main=(MainActivity) itemView.getContext();                         //assign MainActivity with itemview context it need type of MainActivity context
                    Intent intent=new Intent(main, ServerActivity.class);                           //creating new intent typeof DomainActivity
                    intent.putExtra("model",model);                                                 //adding model for DomainActivity for reuse domain object
                    main.startActivity(intent);                                                     //starting DomainActivity
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
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 2;                        //define each option size
            int offset = (int) (optionItemWidth + 0.5f);                                            //define offset with option size
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;

            if (ipAddress.getWidth() == 0) {                                                      //set options sizes
                setLayoutWidth(ipAddress, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(information, (int) (optionItemWidth + 0.5f));
            }
            ipAddress.setTranslationX(-(int) (p * optionItemWidth * 2 + 0.5f) + offset);
            information.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);          //set option location

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {                                      //when swipe view -1,1 lower than horizontal amount views will invisible
                swipeable_container.setVisibility(View.INVISIBLE);
                ipAddress.setVisibility(View.INVISIBLE);
                information.setVisibility(View.INVISIBLE);

            } else {                                                                                //when swipe view -1,1 higher than horizontal amount views will visible
                swipeable_container.setVisibility(View.VISIBLE);
                ipAddress.setVisibility(View.VISIBLE);
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
    static String TAG=ServerAdapter.class.getSimpleName();
    private static ArrayList<Server> models=new ArrayList<>();                                      //create for storing DomainObject in list
    private static Context context;                                                                 //define context for reuse
    private static ServerAdapter adapter;                                                           //define adapter for reuse
    private ProgressDialog progressDialog;                                                          //define ProgressDialog for showing refresh animation

    private RecyclerView recyclerView;                                                              //define RecyclerView for gettin on activity or fragment
    private RecyclerViewSwipeManager swipeManager;                                                  //define RecyclerViewSwipeManager for RecyclerView
    private RecyclerView.LayoutManager layoutManager;                                               //define RecyclerView.LayoutManager for RecyclerView
    private SwipeRefreshLayout swipeRefreshLayout;

    public ServerAdapter(Context context,SwipeRefreshLayout swipeRefreshLayout,RecyclerView recyclerView){//custom constructor
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
        progressDialog.setMessage(context.getString(R.string.server)+ context.getString(R.string.isLoading));                                      //setting message on resource file
        progressDialog.setCancelable(false);                                                        //assign progressDialog is not cancelable
        progressDialog.show();                                                                      //showing progressDialog
        refresh();
    }
    @Override                                                                                       //return stable id
    public long getItemId(int position) {return models.get(position).id;}
    @Override
    public ServerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_server,parent,false);
        return new ServerHolder(row);
    }

    @Override
    public void onBindViewHolder(ServerHolder holder, int position) {
        Server model=models.get(position);
        holder.model=model;
        if (model.status)
            holder.status.setBackgroundColor(Color.GREEN);
        else
            holder.status.setBackgroundColor(Color.RED);
        holder.computerName.setText(model.computerName);
        holder.operatingSystem.setText(model.operatingSystem);
        holder.ip.setText(model.host);

        holder.setMaxLeftSwipeAmount(-OPTIONS_AREA_PROPORTION);                                     //set max left swipe amount
        holder.setMaxRightSwipeAmount(0);                                                           //set max right swipe amount
        holder.setSwipeItemHorizontalSlideAmount(
                model.pinned ? -OPTIONS_AREA_PROPORTION : 0);
    }

    @Override
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
    public SwipeResultAction onSwipeItem(ServerHolder holder, int position, int result) {           //swipe action method it calls actions
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
    public int onGetSwipeReactionType(ServerHolder holder, int position, int x, int y) {            //interface method returns swipe reaction type
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }
    @Override
    public void onSetSwipeBackground(ServerHolder holder, int position, int type) {                 //set swipe view backcolor when doesnt have parent backcolor
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }
    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {            //left pinning action
        ServerAdapter adapter;
        int position;

        public SwipeLeftPinningAction(ServerAdapter adapter, int position) {
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
        ServerAdapter adapter;
        int position;

        public SwipeCancelAction(ServerAdapter adapter, int position) {
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
    void refresh() {
        service = API.getClient().create(IServer.class);
        String key= SharedInformation.getData("serverKey");
        Call<Reply> call=service.GetServerList(key);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ArrayList<Server> objects= ((ArrayList<Server>) response.body().details);
                ObjectMapper mapper=new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);         //set doesnt fail when doesnt parse to class
                models.clear();
                for (int i = 0; i < objects.size(); i++)
                {
                    Server model=mapper.convertValue(objects.get(i),Server.class);
                    if (!checkItemExist(model)) {
                        models.add(model);
                        adapter.notifyDataSetChanged();
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
                Toast.makeText(context,context.getString(R.string.isLoading), Toast.LENGTH_SHORT).show();
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
    }

    boolean checkItemExist(Server model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).id==model.id){exist=true;break;}
        }
        return exist;
    }
    //endregion




}