package com.daoyis.shennghuang;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daoyis.shennghuang.Adapters.BlueConnectAdapter;
import com.daoyis.shennghuang.Bean.BlueConnectBean;
import com.daoyis.shennghuang.Views.WrapContentLinearLayoutManager;
import com.daoyis.shennghuang.utils.MacUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author:"道翼(yanwen)"
 * @params:"蓝牙列表"
 * @data:20-8-18 下午4:05
 * @email:1966287146@qq.com
 */
public class BlueConnectListActivity  extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager m_manager;
    private BlueConnectAdapter m_adpter;
    private List<BlueConnectBean> m_list;
    private Button buttons_ok;



    // Member fields
    private BluetoothAdapter mBtAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MacUtils.initWindow(this, 0xffffffff, false, null, true);
        setContentView(R.layout.activity_blue_connect);
        init_date();
        init_view();
    }
    protected void init_date(){
        m_list=new ArrayList<>();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BlueConnectBean blueConnectBean=new BlueConnectBean();
                blueConnectBean.setName(device.getName());
                blueConnectBean.setAddress(device.getAddress());
                m_list.add(blueConnectBean);
            }
        }

    }
    protected void init_view(){
        buttons_ok=findViewById(R.id.buttons_ok);
        recyclerView=findViewById(R.id.blue_list);
        m_manager=new WrapContentLinearLayoutManager(this);
        m_manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(m_manager);
        m_adpter=new BlueConnectAdapter(m_list);
        recyclerView.setAdapter(m_adpter);

        m_adpter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                returnToPreviousActivity(m_list.get(position).getAddress(),false,m_list.get(position).getName());
            }
        });
        buttons_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });

    }

    private void returnToPreviousActivity(String address, boolean re_pair, String name) {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        Intent intent = new Intent();
        intent.putExtra("mac", address);
        intent.putExtra("is_ok", re_pair);
        intent.putExtra("name", name);
        setResult(0x12, intent);
        finish();
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d("daoyi", "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        // if (mBtAdapter.isDiscovering()) {
        // mBtAdapter.cancelDiscovery();
        // }

        m_list.clear();
        m_adpter.notifyDataSetChanged();
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BlueConnectBean blueConnectBean=new BlueConnectBean();
                blueConnectBean.setName(device.getName());
                blueConnectBean.setAddress(device.getAddress());
                m_list.add(blueConnectBean);
                m_adpter.notifyDataSetChanged();
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (m_list.size() == 0) {
                    Toast.makeText(context,"没有发现设备",Toast.LENGTH_LONG).show();
                }

            }

        }
    };
    @Override
    protected void onStop() {
        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
        super.onStop();
    }


    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        super.onResume();
    }

}
