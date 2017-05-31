package net.sinancaliskan.maestropanel.adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import net.sinancaliskan.maestropanel.dialog.DatabaseChangeQuota;
import net.sinancaliskan.maestropanel.dialog.DatabaseDelete;
import net.sinancaliskan.maestropanel.dialog.DatabaseUserChangePassword;
import net.sinancaliskan.maestropanel.dialog.DatabaseUserDelete;
import net.sinancaliskan.maestropanel.dialog.DatabaseUserSetPermission;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.interfaces.Swipeable;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Database;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.DatabaseUser;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 4.03.2017.
 */

public class DatabaseUserAdapter extends RecyclerView.Adapter<DatabaseUserAdapter.DatabaseUserHolder> implements SwipeableItemAdapter<DatabaseUserAdapter.DatabaseUserHolder> {
    static final float OPTIONS_AREA_PROPORTION = 1.0f;
    static final float REMOVE_ITEM_THRESHOLD = 1.1f;

    final String key= SharedInformation.getData("serverKey");
    private IDomain service =null;

    //region viewHolder
    public static class DatabaseUserHolder extends AbstractSwipeableItemViewHolder {
        View swipeable_container,editPassword,editPermission,delete;
        DatabaseUser model;
        TextView username;
        float lastSwipeAmount;
        public DatabaseUserHolder(View itemView)
        {
            super(itemView);

            swipeable_container=itemView.findViewById(R.id.swipeable_container);
            editPassword=itemView.findViewById(R.id.editPassword);
            editPermission=itemView.findViewById(R.id.editPermission);
            delete=itemView.findViewById(R.id.delete);

            username=(TextView) itemView.findViewById(R.id.txtUsername);

            editPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseUserChangePassword(context,adapter,database,model);
                }
            });

            editPermission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseUserSetPermission(context,adapter,database,model);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseUserDelete(context,adapter,database,model);
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
            float optionItemWidth = itemWidth * OPTIONS_AREA_PROPORTION / 3;
            int offset = (int) (optionItemWidth + 0.5f);
            float p = Math.max(0, Math.min(OPTIONS_AREA_PROPORTION, -horizontalAmount)) / OPTIONS_AREA_PROPORTION;

            if (editPassword.getWidth() == 0) {
                setLayoutWidth(editPassword, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(editPermission, (int) (optionItemWidth + 0.5f));
                setLayoutWidth(delete, (int) (optionItemWidth + 0.5f));
            }
            editPassword.setTranslationX(-(int) (p * optionItemWidth * 3 + 0.5f) + offset);
            editPermission.setTranslationX(-(int) (p * optionItemWidth * 2 + 0.5f) + offset);
            delete.setTranslationX(-(int) (p * optionItemWidth * 1 + 0.5f) + offset);

            if (horizontalAmount < (-REMOVE_ITEM_THRESHOLD)) {
                swipeable_container.setVisibility(View.INVISIBLE);
                editPassword.setVisibility(View.INVISIBLE);
                editPermission.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            } else {
                swipeable_container.setVisibility(View.VISIBLE);
                editPassword.setVisibility(View.VISIBLE);
                editPermission.setVisibility(View.VISIBLE);
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
    static String TAG=DatabaseUserAdapter.class.getSimpleName();
    private static ArrayList<DatabaseUser> models=new ArrayList<>();
    public static Domain domain;
    public static Database database;
    private static Context context;
    private static DatabaseUserAdapter adapter;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerViewSwipeManager swipeManager;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    public DatabaseUserAdapter(Context context, Database db, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,Domain model){
        this.context=context;
        this.domain=model;
        this.database=db;
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
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(context,R.drawable.list_divider),false));

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.database_user_management)+ context.getString(R.string.isLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        refresh();
    }
    @Override
    public long getItemId(int position) {
        return models.get(position).id;
    }

    @Override
    public DatabaseUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_database_user,parent,false);
        return new DatabaseUserHolder(row);
    }

    @Override
    public void onBindViewHolder(DatabaseUserHolder holder, int position) {
        DatabaseUser model=models.get(position);
        holder.model=model;
        holder.username.setText(model.username.toString());

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
            if (models.get(i).username==model)
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
    public SwipeResultAction onSwipeItem(DatabaseUserHolder holder, int position, int result) {
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
    public int onGetSwipeReactionType(DatabaseUserHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(DatabaseUserHolder holder, int position, int type) {
        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(0xffff6666);
        }
    }

    static class SwipeLeftPinningAction extends SwipeResultActionMoveToSwipedDirection {
        DatabaseUserAdapter adapter;
        int position;

        public SwipeLeftPinningAction(DatabaseUserAdapter adapter, int position) {
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
        DatabaseUserAdapter adapter;
        int position;

        public SwipeCancelAction(DatabaseUserAdapter adapter, int position) {
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
        Call<Reply> call=service.GetDatabaseList(key,domain.name);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ObjectMapper mapper=new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ArrayList<Database> objects= ((ArrayList<Database>) response.body().details);
                models.clear();
                for (int i = 0; i < objects.size(); i++)
                {
                    Database model=mapper.convertValue(objects.get(i),Database.class);
                    if (model.name.equals(database.name) && model.dbType.equals(database.dbType)){
                        ArrayList<DatabaseUser> users=model.users;
                        for (int j = 0; j < users.size(); j++) {
                            DatabaseUser dbUser=users.get(j);
                            dbUser.id=j;
                            if (!checkItemExist(dbUser)) {
                                models.add(dbUser);
//                        adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(0);
                            }
                        }
                        break;
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
    boolean checkItemExist(DatabaseUser model){
        boolean exist=false;
        for (int i=0;i<models.size();i++){
            if (models.get(i).username.equals(model.username)){exist=true;break;}
        }
        return exist;
    }
    //endregion
}
