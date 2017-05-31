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
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.dialog.SubdomainChangeFTPUser;
import net.sinancaliskan.maestropanel.dialog.SubdomainDelete;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Ftp;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.SubDomain;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubdomainAdapter extends RecyclerView.Adapter<SubdomainAdapter.SubdomainHolder> implements SwipeableItemAdapter<SubdomainAdapter.SubdomainHolder> {
    static final float OPTIONS_AREA_PROPORTION = 1.0f;
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;

    final String key= SharedInformation.getData("serverKey");
    private IDomain service =null;

    //region viewHolder
    public static class SubdomainHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container,edit,delete;
        SubDomain model;
        View status;
        TextView name,ftpUser;
        float lastSwipeAmount;
        public SubdomainHolder(final View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);

            status= itemView.findViewById(R.id.status);
            name=(TextView) itemView.findViewById(R.id.txtName);
            ftpUser=(TextView) itemView.findViewById(R.id.txtFtpUser);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SubdomainChangeFTPUser(context,adapter,model);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   new SubdomainDelete(context,adapter,model.name);
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
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 2;
            int offset = (int) (optionItemWidth + 0.5f);
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;

            if (edit.getWidth() == 0) {
                setLayoutWidth(edit, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
            }
            edit.setTranslationX(-(int) (p * optionItemWidth * 2 + 0.5f) + offset);
            delete.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {
                swipeable_container.setVisibility(View.INVISIBLE);
                edit.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            } else {
                swipeable_container.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
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

    //region Adapter
    static String TAG=DomainAdapter.class.getSimpleName();
    private static ArrayList<SubDomain> models=new ArrayList<>();
    public List<String> ftpUsers=new ArrayList<>();
    public static Domain domain;
    private static Context context;
    private static SubdomainAdapter adapter;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerViewSwipeManager swipeManager;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    public SubdomainAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, Domain model){
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
        progressDialog.setMessage(context.getString(R.string.domain)+ context.getString(R.string.isLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        refresh();
    }
    @Override
    public long getItemId(int position) {
        return models.get(position).id;
    }

    @Override
    public SubdomainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subdomain,parent,false);
        return new SubdomainHolder(row);
    }

    @Override
    public void onBindViewHolder(SubdomainHolder holder, int position) {
        SubDomain model=models.get(position);
        holder.model=model;

        holder.name.setText(model.name);
        holder.ftpUser.setText(model.ftpUser);

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
            if (models.get(i).name==model)
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

    public void updateItem(SubDomain model,String newFtpUser){
        model.ftpUser=newFtpUser;
        adapter.notifyItemChanged(getItemPosition(model.name));
    }

    @Override
    public SwipeResultAction onSwipeItem(SubdomainHolder holder, int position, int result) {
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
    public int onGetSwipeReactionType(SubdomainHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(SubdomainHolder holder, int position, int type) {
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }

    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {
        SubdomainAdapter adapter;
        int position;

        public SwipeLeftPinningAction(SubdomainAdapter adapter, int position) {
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
        SubdomainAdapter adapter;
        int position;

        public SwipeCancelAction(SubdomainAdapter adapter, int position) {
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
        Thread thread=new Thread() {
            @Override
            public void run() {
                Call<Reply> call=null;
                service = API.getClient().create(IDomain.class);
                call=service.GetFtpAccounts(key,domain.name);
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        ObjectMapper mapper=new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        Ftp ftp=mapper.convertValue(response.body().details,Ftp.class);
                        for (int i = 0; i < ftp.users.size(); i++)
                        {
                            String username=ftp.users.get(i).username;
                            if (!checkUserExist(username)){
                                ftpUsers.add(username);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<Reply> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(context,context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                call=null;
                service = API.getClient().create(IDomain.class);
                call=service.GetSubDomains(key,domain.name);
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        if (response.body()!=null) {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            ArrayList<SubDomain> objects = ((ArrayList<SubDomain>) response.body().details);
                            models.clear();
                            for (int i = 0; i < objects.size(); i++) {
                                SubDomain model = mapper.convertValue(objects.get(i), SubDomain.class);
                                model.id=models.size()+1;
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
                        else {
                            refresh();
                        }
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
        };
        thread.start();
    }
    boolean checkItemExist(SubDomain model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).name.equals(model.name)){exist=true;break;}
        }
        return exist;
    }
    boolean checkUserExist(String model){
        boolean exist=false;
        for (int i=0;i<ftpUsers.size();i++){
            if (ftpUsers.get(i).equals(model)){exist=true;break;}
        }
        return exist;
    }
    //endregion
}
