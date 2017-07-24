package com.yao.testpickerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.yao.testpickerview.data.LocationEntity;
import com.yao.testpickerview.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(view -> alertPickerView());
        findViewById(R.id.button2).setOnClickListener(view -> startActivity(WorldPickerActivity.createIntent(this)));
        findViewById(R.id.button3).setOnClickListener(view -> startActivity(TestPullActivity.createIntent(this)));
    }

    // 弹出选择器
    private void alertPickerView() {
        LocationEntity area = new Gson().
                fromJson(ResourceUtil.getFileFromAssets(this, "city.json"), LocationEntity.class);
        System.out.println("size:" + area.getLocation().size());

        // 选项选择器

        // 初始化数据
        for (int i = 0; i < area.getLocation().size(); i++) {
            // 省份
            options1Items.add(area.getLocation().get(i).getAreaName());
            List<LocationEntity.Location.CitiesEntity> citiesList
                    = area.getLocation().get(i).getCities();
            ArrayList<String> showCities
                    = new ArrayList<>();
            ArrayList<ArrayList<String>> showCountiesList
                    = new ArrayList<>();
            for (int j = 0; j < citiesList.size(); j++) {
                showCities.add(citiesList.get(j).getAreaName());
                List<LocationEntity.Location.CitiesEntity.CountiesEntity> counties = citiesList.get(j).getCounties();
                ArrayList<String> showCounList = new ArrayList<>();
                for (int k = 0; k < counties.size(); k++) {
                    showCounList.add(counties.get(k).getAreaName());
                }
                showCountiesList.add(showCounList);
            }
            // 市
            options2Items.add(showCities);
            // 区
            options3Items.add(showCountiesList);
        }

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String tx = options1Items.get(options1) +
                    options2Items.get(options1).get(options2) +
                    options3Items.get(options1).get(options2).get(options3);

            Toast.makeText(MainActivity.this, tx, Toast.LENGTH_SHORT).show();
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, options3Items);
        // 设置选择的三级单位
        // pwOptions.setLabels("省", "市", "区");
        // 设置默认选中的三级项目
        // pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }
}
