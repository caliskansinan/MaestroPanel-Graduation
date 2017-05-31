package net.sinancaliskan.maestropanel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
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
import net.sinancaliskan.maestropanel.dialog.DnsRecordDelete;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.DNS;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.DNSRecord;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SinanCaliskan on 4.03.2017.
 */

public class DNSAdapter extends RecyclerView.Adapter<DNSAdapter.DNSHolder> implements SwipeableItemAdapter<DNSAdapter.DNSHolder> {
    static final float OPTIONS_AREA_PROPORTION = 1.0f;
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;

    final String key= SharedInformation.getData("serverKey");
    private IDomain service =null;

    //region viewHolder
    public static class DNSHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container,delete;
        DNSRecord model;
        View status;
        TextView type,name,hostValue;
        float lastSwipeAmount;
        public DNSHolder(View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);
            delete=itemView.findViewById(R.id.delete);

            status=itemView.findViewById(R.id.status);
            type=(TextView) itemView.findViewById(R.id.txtType);
            name=(TextView) itemView.findViewById(R.id.txtName);
            hostValue=(TextView) itemView.findViewById(R.id.txtHostValue);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DnsRecordDelete(context,adapter,model);
                }
            });
        }
        @Override
        public View getSwipeableContainerView() {
            return swipeable_container;
        }

        @Override
        public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping) {
            int itemWidth = itemView.getWidth();
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 1;
            int offset = (int) (optionItemWidth + 0.5f);
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;

            if (delete.getWidth() == 0) {
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
            }
            delete.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {
                swipeable_container.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            } else {
                swipeable_container.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
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
    static String TAG=DNSAdapter.class.getSimpleName();
    private static ArrayList<DNSRecord> models=new ArrayList<>();
    public static Domain domain;
    private static Context context;
    private static DNSAdapter adapter;

    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerViewSwipeManager swipeManager;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    public DNSAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, Domain model){
        this.context=context;
        this.domain=model;
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
        progressDialog.setMessage(context.getString(R.string.dns)+ context.getString(R.string.isLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        refresh();
    }
    @Override
    public long getItemId(int position) {
        return models.get(position).id;
    }

    @Override
    public DNSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dns,parent,false);
        return new DNSHolder(row);
    }

    @Override
    public void onBindViewHolder(DNSHolder holder,int position) {
        DNSRecord model=models.get(position);
        holder.model=model;

        if (model.recordType.equals("A"))
            holder.status.setBackgroundColor(Color.GRAY);
        else if (model.recordType.equals("CNAME"))
            holder.status.setBackgroundColor(Color.YELLOW);
        else if (model.recordType.equals("MX"))
            holder.status.setBackgroundColor(Color.MAGENTA);
        else if (model.recordType.equals("NS"))
           holder.status.setBackgroundColor(Color.CYAN);
        else if (model.recordType.equals("TXT"))
            holder.status.setBackgroundColor(Color.BLUE);

        holder.type.setText(model.recordType.toString());
        holder.name.setText(model.name.toString());
        holder.hostValue.setText(model.value.toString());

        holder.setMaxLeftSwipeAmount(-OPTIONS_AREA_PROPORTION);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(
                model.pinned ? -OPTIONS_AREA_PROPORTION : 0);
    }

    @Override
    public int getItemCount() {return models.size();}

    public int getItemPosition(DNSRecord model){
        int position=-1;
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).name.equals(model.name) && models.get(i).recordType.equals(model.recordType)&& models.get(i).value.equals(model.value))
                return i;
        }
        return position;
    }

    public void removeItem(int position){
        models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,models.size());
    }

    public void removeItem(DNSRecord model){
        int position=getItemPosition(model);
        if (position>-1)
            removeItem(position);
    }

    @Override
    public SwipeResultAction onSwipeItem(DNSHolder holder, int position, int result) {
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
    public int onGetSwipeReactionType(DNSHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(DNSHolder holder, int position, int type) {
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }

    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {
        DNSAdapter adapter;
        int position;

        public SwipeLeftPinningAction(DNSAdapter adapter, int position) {
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
        DNSAdapter adapter;
        int position;

        public SwipeCancelAction(DNSAdapter adapter, int position) {
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
        service = API.getClient().create(IDomain.class);
        Call<Reply> call=service.GetDnsRecords(key,domain.name);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ObjectMapper mapper=new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                DNS dns=mapper.convertValue(response.body().details,DNS.class);
                models.clear();
                for (int i = 0; i < dns.records.size(); i++)
                {
                    DNSRecord record=mapper.convertValue(dns.records.get(i),DNSRecord.class);
                    record.id=i;
                    if (record.name.equals("@"))
                        record.name=domain.name;
                    if (!checkItemExist(record)) {
                        models.add(record);
//                        adapter.notifyItemInserted(0);
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
                Toast.makeText(context,context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
    }
    boolean checkItemExist(DNSRecord model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).name.equals(model.name)&& models.get(i).recordType.equals(model.recordType) && models.get(i).value.equals(model.value)){exist=true;break;}
        }
        return exist;
    }
    //endregion
}
