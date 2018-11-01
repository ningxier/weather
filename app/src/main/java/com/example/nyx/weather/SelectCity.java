package com.example.nyx.weather;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nyx.bean.City;

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView listView=null;
    private TextView cityselected=null;
    private List<City>listcity=MyApplication.getInstance().getCityList();//获取实例，调用getCityList方法，返回mCityList
    private int listSize=listcity.size();
    private String[]city=new String[listSize];//建立listSize长度的数组
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        cityselected=(TextView)findViewById(R.id.title_name);//将选择的城市信息与顶部的显示内容绑定
        mBackBtn.setOnClickListener(this);
        Log.i("City",listcity.get(1).getCity());
        for (int i=0;i<listSize;i++){
            city[i]=listcity.get(i).getCity();
            Log.d("City",city[i]);
        }//利用for循环语句写入城市信息
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,city);
        listView=findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//为列表项目设置监听器
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i,long l) {
                Toast.makeText(SelectCity.this, "你已经选择" + city[i], Toast.LENGTH_SHORT).show();
                cityselected.setText("当前城市: " + city[i]);
            }
        });

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                int position =listView.getCheckedItemPosition();
                String select_cityCode=listcity.get(position).getNumber();
                Intent i=new Intent();
                i.putExtra("cityCode",select_cityCode);
                setResult(RESULT_OK,i);
                Log.d("citycode",select_cityCode);
            finish();
            break;
            default:
                break;
        }
    }
}
