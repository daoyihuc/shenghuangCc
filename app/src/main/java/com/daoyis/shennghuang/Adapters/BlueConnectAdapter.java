package com.daoyis.shennghuang.Adapters;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyis.shennghuang.Bean.BlueConnectBean;
import com.daoyis.shennghuang.R;

import java.util.List;

/**
 * @author:"道翼(yanwen)"
 * @params:"代理升级"
 * @data:20-8-18 下午4:12
 * @email:1966287146@qq.com
 */
public class BlueConnectAdapter  extends BaseQuickAdapter<BlueConnectBean, BaseViewHolder> {
    public BlueConnectAdapter(int layoutResId, @Nullable List<BlueConnectBean> data) {
        super(layoutResId, data);
    }

    public BlueConnectAdapter(@Nullable List<BlueConnectBean> data) {
        super(R.layout.adpter_blue_list,data);
    }

    public BlueConnectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BlueConnectBean item) {

        helper.setText(R.id.blue_list_name,item.getName());
        helper.setText(R.id.blue_list_address,item.getAddress());

    }
}
