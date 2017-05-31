package net.sinancaliskan.maestropanel.fragments.Servers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.ServerAdapter;

/**
 * Created by SinanCaliskan on 2.03.2017.
 */

public class ServerListFragment extends Fragment{

    private SwipeRefreshLayout swipeRefreshLayout;                                                  //define SwipeRefreshLayout
    private RecyclerView recyclerView;                                                              //define RecyclerView
    private FloatingActionButton fabAdd;                                                            //define FloatingActionButton

    private ServerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_server_list,container,false);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);                     //set swipeRefreshLayout component
        recyclerView= (RecyclerView) view.findViewById(R.id.recycler);                              //set recyclerView component
        adapter=new ServerAdapter(getContext(),swipeRefreshLayout,recyclerView);                    //set adapter with parameters
        fabAdd= (FloatingActionButton) view.findViewById(R.id.fabAdd);                              //set FloatingActionButton component
        //region domain create
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                          //create fabAdd on click listener
                //new DomainRegister(getContext(),adapter);                                           //create DomainRegister dialog with parameters
            }
        });
        //endregion
        return view;
    }
}
