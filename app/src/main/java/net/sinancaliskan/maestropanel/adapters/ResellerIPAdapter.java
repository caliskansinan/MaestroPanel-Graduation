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
import android.widget.CheckBox;
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
import net.sinancaliskan.maestropanel.interfaces.IReseller;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.IPAddress;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.Reseller;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 20.05.2017.
 */

public class ResellerIPAdapter extends RecyclerView.Adapter<ResellerIPAdapter.IPAddressHolder> implements SwipeableItemAdapter<ResellerIPAdapter.IPAddressHolder> {
    static final float OPTIONS_AREA_PROPORTION = 0f;
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;

    final String key= SharedInformation.getData("serverKey");
    private IReseller service =null;

    //region viewHolder
    public static class IPAddressHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container;
        IPAddress model;
        TextView nic,ipAddress;
        CheckBox isShared,isDedicated,isExclusive;
        float lastSwipeAmount;
        public IPAddressHolder(View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);

            nic=(TextView) itemView.findViewById(R.id.txtNic);
            ipAddress=(TextView) itemView.findViewById(R.id.txtIPAddress);
            isShared=(CheckBox) itemView.findViewById(R.id.isShared);
            isDedicated=(CheckBox) itemView.findViewById(R.id.isDedicated);
            isExclusive=(CheckBox) itemView.findViewById(R.id.isExclusive);
        }
        @Override
        public View getSwipeableContainerView() {
            return swipeable_container;
        }

        @Override
        public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping) {
            /*int itemWidth = itemView.getWidth();
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 0;
            int offset = (int) (optionItemWidth + 0.5f);
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;*/

            /*if (editPassword.getWidth() == 0) {
                setLayoutWidth(editPassword, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
            }
            editPassword.setTranslationX(-(int) (p * optionItemWidth * 2 + 0.5f) + offset);
            delete.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);*/

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {
                swipeable_container.setVisibility(View.INVISIBLE);
                /*editPassword.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);*/
            } else {
                swipeable_container.setVisibility(View.VISIBLE);
                /*editPassword.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);*/
            }

            lastSwipeAmount = horizontalAmount;
        }

        private static void setLayoutWidth(View v, int width) {
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            lp.width = width;
            v.setLayoutParams(lp);
        }
    }
    //endregion
    //region adapter
    static String TAG=FtpAdapter.class.getSimpleName();
    private static ArrayList<IPAddress> models=new ArrayList<>();
    public static Reseller reseller;
    private static Context context;
    private static ResellerIPAdapter adapter;

    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerViewSwipeManager swipeManager;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ResellerIPAdapter(Context context,SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,Reseller model){
        this.context=context;
        this.reseller=model;
        this.swipeRefreshLayout=swipeRefreshLayout;
        this.recyclerView=recyclerView;

        setHasStableIds(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue_bright,
                R.color.green_light,
                R.color.orange_light,
                R.color.red_light
        );
        swipeManager=new RecyclerViewSwipeManager();
        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter=this;
        recyclerView.setAdapter(swipeManager.createWrappedAdapter(this));
        recyclerView.setItemAnimator(new SwipeDismissItemAnimator());
        swipeManager.attachRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(context,R.drawable.list_divider),true));


        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.ip_address)+ context.getString(R.string.isLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        refresh();
    }
    @Override
    public long getItemId(int position) {
        return models.get(position).id;
    }

    @Override
    public IPAddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ip_address,parent,false);
        return new IPAddressHolder(row);
    }

    @Override
    public void onBindViewHolder(IPAddressHolder holder, int position) {
        IPAddress model=models.get(position);
        holder.model=model;

        holder.nic.setText(model.nic);
        holder.ipAddress.setText(model.ipAddr);

        holder.isShared.setChecked(model.isShared);
        holder.isDedicated.setChecked(model.isDedicated);
        holder.isExclusive.setChecked(model.isExclusive);

        holder.setMaxLeftSwipeAmount(-OPTIONS_AREA_PROPORTION);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(
                model.pinned ? -OPTIONS_AREA_PROPORTION : 0);
    }

    @Override
    public int getItemCount() {return models.size();}

    public int getItemPosition(String model){
        int position=-1;
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).nic==model)
                return i;
        }
        return position;
    }
    public void removeItem(int position){
        models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,models.size());
    }

    public void removeItem(String model){
        int position=getItemPosition(model);
        if (position>-1)
            removeItem(position);
    }

    @Override
    public SwipeResultAction onSwipeItem(IPAddressHolder holder, int position, int result) {
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
    public int onGetSwipeReactionType(IPAddressHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(IPAddressHolder holder, int position, int type) {
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }
    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {
        ResellerIPAdapter adapter;
        int position;

        public SwipeLeftPinningAction(ResellerIPAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            adapter.models.get(position).pinned = true;
            adapter.notifyItemChanged(position);
        }
    }
    static class SwipeCancelAction extends SwipeResultActionDefault {
        ResellerIPAdapter adapter;
        int position;

        public SwipeCancelAction(ResellerIPAdapter adapter, int position) {
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
        Call<Reply> call=service.GetIPAddrList(key,reseller.username);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ObjectMapper mapper=new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ArrayList<IPAddress> objects= ((ArrayList<IPAddress>) response.body().details);     //creating list with json
                models.clear();
                for (int i = 0; i <objects.size(); i++) {
                    IPAddress model=mapper.convertValue(objects.get(i),IPAddress.class);
                    model.id=i;
                    if (!checkItemExist(model)) {                                                   //check object in list
                        models.add(model);                                                          //add in list
                        adapter.notifyDataSetChanged();                                             //notify adapter data set changed
                        recyclerView.scrollToPosition(0);                                           //set recyclerView scroll position
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
    boolean checkItemExist(IPAddress model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).nic.equals(model.nic)){exist=true;break;}
        }
        return exist;
    }
    //endregion
}
