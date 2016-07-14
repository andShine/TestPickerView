package com.yao.testpickerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

    OptionsPickerView pvOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(view -> alertPickerView());
    }

    // 弹出选择器
    private void alertPickerView() {
        LocationEntity area = new Gson().
                fromJson(ResourceUtil.getFileFromAssets(this, "city.json"), LocationEntity.class);
        System.out.println("size:" + area.getLocation().size());

        // 选项选择器
        pvOptions = new OptionsPickerView(this);

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

        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        // 设置选择的三级单位
        // pwOptions.setLabels("省", "市", "区");
        pvOptions.setTitle("选择城市");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setOnoptionsSelectListener((options1, option2, options3) -> {
            //返回的分别是三个级别的选中位置
            String tx = options1Items.get(options1)
                    + options2Items.get(options1).get(option2)
                    + options3Items.get(options1).get(option2).get(options3);
            System.out.println("" + tx);
        });
        pvOptions.show();
    }
}
