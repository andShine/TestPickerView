package com.yao.testpickerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.yao.testpickerview.util.HandlerUtils;
import com.yao.testpickerview.util.ResourceUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/7/22.
 */

public class WorldPickerActivityTest2 extends AppCompatActivity {

    private RelativeLayout rlTop;
    private RelativeLayout rlBottom;
    private TextView tvFirstName;
    private TextView tvSecondName;

    private String xmlData;

    // 所有的国家
    List<String> optionsCoun = new ArrayList<>();
    // 得到当前所有的省份
    List<String> options1Items = new ArrayList<>();
    // 得到当前所有的城市
    List<List<String>> options2Items = new ArrayList<>();
    // 得到当前所有的区县
    List<List<List<String>>> options3Items = new ArrayList<>();

    List<String> options1ItemsChina = new ArrayList<>();
    List<List<String>> options2ItemsChina = new ArrayList<>();
    List<List<List<String>>> options3ItemsChina = new ArrayList<>();

    // 省份
    private PickData pickDataPro;
    private PickData pickDataProChina;
    private ProgressDialog dialog;
    // 默认中国
    private String selectedCountry = "中国";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

    public static Intent createIntent(Context context) {
        return new Intent(context, WorldPickerActivityTest2.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_picker);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvSecondName = (TextView) findViewById(R.id.tvSecondName);
        initEvent();
        initData();
    }

    private void initData() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        // 没啥用，可以删掉
        xmlData = ResourceUtil.getFileFromAssets(this, "LocList.xml");
        System.out.println(xmlData);
        // 初始化数据，比较耗时
        // 默认先加载中国的全部省市区，因为比较慢
        new Thread(() -> {
            // 写子线程中的操作
            optionsCoun = getCountryList();
            initChinaCountryData("中国");
            mHandler.sendEmptyMessage(0);
        }).start();
    }

    // 和下面方法重复，可优化
    private void initChinaCountryData(String selectedCountry) {
        pickDataProChina = getList1(selectedCountry);
        System.out.println("isOnly:" + pickDataProChina.isOnly);
        // 得到当前所有的省份
        options1ItemsChina = pickDataProChina.getDatas();
        // 得到当前所有的城市
        options2ItemsChina = new ArrayList<>();
        // 得到当前所有的区县
        options3ItemsChina = new ArrayList<>();
        for (int i = 0; i < options1ItemsChina.size(); i++) {
            List<String> cityList = getList2(selectedCountry, options1ItemsChina.get(i));
            List<List<String>> tempAreaList
                    = new ArrayList<>();
            for (int j = 0; j < cityList.size(); j++) {
                List<String> areaList = getList3(selectedCountry, options1ItemsChina.get(i), cityList.get(j));
                if (areaList == null || areaList.size() == 0) {
                    System.out.println("emptyList");
                    List<String> emptyList = new ArrayList<>();
                    emptyList.add("无");
                    tempAreaList.add(emptyList);
                }
                tempAreaList.add(areaList);
            }
            options2ItemsChina.add(cityList);
            options3ItemsChina.add(tempAreaList);
        }
    }

    private void initOtherCountryData(String selectedCountry) {
        if (!"中国".equals(selectedCountry)) {
            pickDataPro = getList1(selectedCountry);
            System.out.println("isOnly:" + pickDataPro.isOnly);
            // 得到当前所有的省份
            options1Items = pickDataPro.getDatas();
            // 得到当前所有的城市
            options2Items = new ArrayList<>();
            // 得到当前所有的区县
            options3Items = new ArrayList<>();
            for (int i = 0; i < options1Items.size(); i++) {
                List<String> cityList = getList2(selectedCountry, options1Items.get(i));
                List<List<String>> tempAreaList
                        = new ArrayList<>();
                for (int j = 0; j < cityList.size(); j++) {
                    List<String> areaList = getList3(selectedCountry, options1Items.get(i), cityList.get(j));
                    if (areaList == null || areaList.size() == 0) {
                        System.out.println("emptyList");
                        List<String> emptyList = new ArrayList<>();
                        emptyList.add("无");
                        tempAreaList.add(emptyList);
                    }
                    tempAreaList.add(areaList);
                }
                options2Items.add(cityList);
                options3Items.add(tempAreaList);
            }
        }
    }

    private void initEvent() {
        dialog = new ProgressDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("加载中...");

        rlTop.setOnClickListener(v -> {
            // 显示选择国家的滚动器
            showPickWorld();
        });

        rlBottom.setOnClickListener(v -> {
            // 显示选择省市的滚动器
            showPickCity();
        });
    }

    private void showPickWorld() {
        if (null != optionsCoun) {
            OptionsPickerView opv = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    selectedCountry = optionsCoun.get(options1);
                    tvFirstName.setText(selectedCountry);
                    tvSecondName.setText("");
                    HandlerUtils.runOnUiThreadDelay(new Runnable() {
                        @Override
                        public void run() {
                            initOtherCountryData(selectedCountry);
                        }
                    }, 500);
                }
            }).setTitleText("选择国家")
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(16)
                    .setOutSideCancelable(false)// default is true
                    .build();
            opv.setPicker(optionsCoun);
            opv.show();
        }
    }

    private void showPickCity() {
        if ("中国".equals(selectedCountry)) {
            pickDataPro = pickDataProChina;
        }
        if (null != pickDataPro) {
            if (pickDataPro.getDatas().size() == 0) {
                // 只有国家
                Toast.makeText(WorldPickerActivityTest2.this, "当前只有国家名", Toast.LENGTH_SHORT).show();
            } else {
                if (!pickDataPro.isOnly) {
                    // 现在有两种情况，1.省市县，2.省市

                    if ("中国".equals(selectedCountry)) {
                        // 使用缓存的中国数据
                        options1Items = options1ItemsChina;
                        options2Items = options2ItemsChina;
                        options3Items = options3ItemsChina;
                    }

                    // 有省市区
                    OptionsPickerView opv = new OptionsPickerView.Builder(this, (options1, options2, options3, v) -> {
                        String selectedPro = options1Items.get(options1);
                        String selectedCity = options2Items.get(options1).get(options2);
                        String selectedArea = "无";
                        if (-1 != options3) {
                            selectedArea = options3Items.get(options1).get(options2).get(options3);
                        }
                        if ("无".equals(selectedArea)) {
                            tvSecondName.setText(selectedPro + "," + selectedCity);
                        } else {
                            tvSecondName.setText(selectedPro + "," + selectedCity + "," + selectedArea);
                        }
                    }).setTitleText("选择")
                            .setDividerColor(Color.BLACK)
                            .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                            .setContentTextSize(16)
                            .setOutSideCancelable(false)// default is true
                            .build();
                    opv.setPicker(options1Items, options2Items, options3Items);
                    opv.show();

                } else {
                    // 只有省
                    options1Items = pickDataPro.getDatas();
                    OptionsPickerView opv = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            String selectedPro = options1Items.get(options1);
                            tvSecondName.setText(selectedPro);
                        }
                    }).setTitleText("选择")
                            .setDividerColor(Color.BLACK)
                            .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                            .setContentTextSize(16)
                            .setOutSideCancelable(false)// default is true
                            .build();
                    opv.setPicker(options1Items);
                    opv.show();
                }
            }
        }
    }

    private List<String> getList3(String selectedCountry, String selectedPro, String selectedCity) {
        List<String> cList = new ArrayList<>();
        if (!TextUtils.isEmpty(xmlData)) {
            try {
                // 是否是当前选择的国家
                boolean isCurrentCoun = false;
                // 是否是当前选择的省
                boolean isCurrentPro = false;
                // 是否是当前选择的城市
                boolean isCurrentCity = false;
                //创建xmlPull解析器
                XmlPullParser parser = Xml.newPullParser();
                ///初始化xmlPull解析器
                parser.setInput(getAssets().open("LocList.xml"), "utf-8");
                //读取文件的类型
                int type = parser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        //开始标签
                        case XmlPullParser.START_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "Name");
                                if (selectedCountry.equals(name)) {
                                    isCurrentCoun = true;
                                }
                            } else if ("State".equals(parser.getName())) {
                                if (isCurrentCoun) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    // 有省，有市
                                    if (selectedPro.equals(name)) {
                                        isCurrentPro = true;
                                    }
                                }
                            } else if ("City".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "Name");
                                if (isCurrentCoun && isCurrentPro) {
                                    // 是当前的国家和省
                                    if (TextUtils.equals(selectedCity, name)) {
                                        isCurrentCity = true;
                                    }
                                }
                            } else if ("Region".equals(parser.getName())) {
                                if (isCurrentCoun && isCurrentPro && isCurrentCity) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    System.out.println("area:" + name);
                                    if (!TextUtils.isEmpty(name))
                                        cList.add(name);
                                }
                            }
                            break;
                        //结束标签
                        case XmlPullParser.END_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                isCurrentCoun = false;
                            }
                            if ("State".equals(parser.getName())) {
                                isCurrentPro = false;
                            }
                            if ("City".equals(parser.getName())) {
                                isCurrentCity = false;
                            }
                            break;
                    }
                    //继续往下读取标签类型
                    type = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cList;
    }

    private List<String> getList2(String selectedCountry, String selectedPro) {
        List<String> cList = new ArrayList<>();
        if (!TextUtils.isEmpty(xmlData)) {
            try {
                // 是否是当前选择的国家
                boolean isCurrentCoun = false;
                // 是否是当前选择的省
                boolean isCurrentPro = false;
                //创建xmlPull解析器
                XmlPullParser parser = Xml.newPullParser();
                ///初始化xmlPull解析器
                parser.setInput(getAssets().open("LocList.xml"), "utf-8");
                //读取文件的类型
                int type = parser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        //开始标签
                        case XmlPullParser.START_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "Name");
                                if (selectedCountry.equals(name)) {
                                    isCurrentCoun = true;
                                }
                            } else if ("State".equals(parser.getName())) {
                                if (isCurrentCoun) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    // 有省，有市
                                    if (selectedPro.equals(name)) {
                                        isCurrentPro = true;
                                    }
                                }
                            } else if ("City".equals(parser.getName())) {
                                if (isCurrentCoun && isCurrentPro) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    // 是当前的国家和省
                                    if (!TextUtils.isEmpty(name)) {
                                        // 取得市的名字
                                        System.out.println("nameCity:" + name);
                                        cList.add(name);
                                    }
                                }
                            }
                            break;
                        //结束标签
                        case XmlPullParser.END_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                isCurrentCoun = false;
                            }
                            if ("State".equals(parser.getName())) {
                                isCurrentPro = false;
                            }
                            break;
                    }
                    //继续往下读取标签类型
                    type = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cList;
    }

    /**
     * 得到当前国家对应的省
     */
    private PickData getList1(String selectedCountry) {
        List<String> cList = new ArrayList<>();
        // 是否只有省
        boolean isOnlyHasPro = false;
        if (!TextUtils.isEmpty(xmlData)) {
            try {
                // 是否是当前选择的国家
                boolean isCurrent = false;
                // 是否只有省
                boolean isOnlyPro = false;
                //创建xmlPull解析器
                XmlPullParser parser = Xml.newPullParser();
                ///初始化xmlPull解析器
                parser.setInput(getAssets().open("LocList.xml"), "utf-8");
                //读取文件的类型
                int type = parser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        //开始标签
                        case XmlPullParser.START_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "Name");
                                if (selectedCountry.equals(name)) {
                                    isCurrent = true;
                                }
                            } else if ("State".equals(parser.getName())) {
                                if (isCurrent) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    if (!TextUtils.isEmpty(name)) {
                                        // 有省，有市
                                        System.out.println("name:" + name);
                                        cList.add(name);
                                    } else {
                                        // 只有省
                                        isOnlyPro = true;
                                        isOnlyHasPro = true;
                                    }
                                }
                            } else if ("City".equals(parser.getName())) {
                                if (isOnlyPro) {
                                    String name = parser.getAttributeValue(null, "Name");
                                    System.out.println("name1:" + name);
                                    cList.add(name);
                                }
                            }
                            break;
                        //结束标签
                        case XmlPullParser.END_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                isCurrent = false;
                            }
                            if ("State".equals(parser.getName())) {
                                isOnlyPro = false;
                            }
                            break;
                    }
                    //继续往下读取标签类型
                    type = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new PickData(cList, isOnlyHasPro);
    }

    private List<String> getCountryList() {
        List<String> cList = new ArrayList<>();
        if (!TextUtils.isEmpty(xmlData)) {
            try {
                //创建xmlPull解析器
                XmlPullParser parser = Xml.newPullParser();
                ///初始化xmlPull解析器
                parser.setInput(getAssets().open("LocList.xml"), "utf-8");
                //读取文件的类型
                int type = parser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        //开始标签
                        case XmlPullParser.START_TAG:
                            if ("CountryRegion".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "Name");
                                cList.add(name);
                            }
                            break;
                        //结束标签
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    //继续往下读取标签类型
                    type = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cList;
    }

    class PickData {
        private List<String> datas;
        private boolean isOnly;

        public PickData(List<String> datas, boolean isOnly) {
            this.datas = datas;
            this.isOnly = isOnly;
        }

        public List<String> getDatas() {
            return datas;
        }

        public void setDatas(List<String> datas) {
            this.datas = datas;
        }

        public boolean isOnly() {
            return isOnly;
        }

        public void setOnly(boolean only) {
            isOnly = only;
        }
    }
}
