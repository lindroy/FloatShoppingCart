package com.lindroid.floatshoppingcart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView ivCart;
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 60; i++) {
            titles.add(new StringBuffer("这是一条数据").append(i).toString());
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        ivCart = (ImageView) findViewById(R.id.iv_cart);
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,titles));
    }
}
