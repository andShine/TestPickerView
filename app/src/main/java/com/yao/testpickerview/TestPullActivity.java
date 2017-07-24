package com.yao.testpickerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class TestPullActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, TestPullActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pull);
        OptionsData optionsData = parseXml("中国");
        List<String> list1 = optionsData.getOptions1Items();
        for (int i = 0; i < list1.size(); i++) {
            System.out.println("pro:" + list1.get(i));
        }
    }

    private OptionsData parseXml(String country) {
        try {
            List<String> options1Items = new ArrayList<>();
            List<List<String>> options2Items = new ArrayList<>();
            List<List<List<String>>> options3Items = new ArrayList<>();

            List<String> temp2List = null;// 对应options2Items
            List<List<String>> temp3List = null;// 对应options3Items
            List<String> tempAreaList = null;// 对应temp3List
            boolean isChina = false;

            //创建xmlPull解析器
            XmlPullParser parser = Xml.newPullParser();
            ///初始化xmlPull解析器
            parser.setInput(getAssets().open("LocList.xml"), "utf-8");
            //读取文件的类型
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //开始标签
                    case XmlPullParser.START_TAG:
                        if ("CountryRegion".equals(parser.getName())) {
                            // 国家
                            String name = parser.getAttributeValue(null, "Name");
                            if (TextUtils.equals(name, country))
                                isChina = true;
                        } else if ("State".equals(parser.getName())) {
                            // 省
                            if (isChina) {
                                String nameState = parser.getAttributeValue(null, "Name");
                                options1Items.add(nameState);
                                temp2List = new ArrayList<>();
                                temp3List = new ArrayList<>();
                            }
                        } else if ("City".equals(parser.getName())) {
                            // 市
                            if (isChina) {
                                String nameCity = parser.getAttributeValue(null, "Name");
                                if (!TextUtils.isEmpty(nameCity)) {
                                    temp2List.add(nameCity);
                                } else {
                                    temp2List.add("无");
                                }
                                tempAreaList = new ArrayList<>();
                            }
                        } else if ("Region".equals(parser.getName())) {
                            // 区/县
                            if (isChina) {
                                String nameRegion = parser.getAttributeValue(null, "Name");
                                if (!TextUtils.isEmpty(nameRegion)) {
                                    tempAreaList.add(nameRegion);
                                } else {
                                    tempAreaList.add("无");
                                }
                            }
                        }
                        break;
                    //结束标签
                    case XmlPullParser.END_TAG:
                        if ("CountryRegion".equals(parser.getName())) {
                            isChina = false;
                        } else if ("State".equals(parser.getName())) {
                            if (isChina){
                                options2Items.add(temp2List);
                                options3Items.add(temp3List);
                            }
                        } else if ("City".equals(parser.getName())) {
                            if (isChina) {
                                if (null != tempAreaList && tempAreaList.size() > 0) {
                                    temp3List.add(tempAreaList);
                                } else {
                                    tempAreaList = new ArrayList<>();
                                    tempAreaList.add("无");
                                    temp3List.add(tempAreaList);
                                }
                            }
                        }
                        break;
                }
                //继续往下读取标签类型
                type = parser.next();
            }
            // 得到想要的结果
            return new OptionsData(options1Items, options2Items, options3Items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class OptionsData {
        List<String> options1Items;
        List<List<String>> options2Items;
        List<List<List<String>>> options3Items;

        public OptionsData(List<String> options1Items, List<List<String>> options2Items, List<List<List<String>>> options3Items) {
            this.options1Items = options1Items;
            this.options2Items = options2Items;
            this.options3Items = options3Items;
        }

        public List<String> getOptions1Items() {
            return options1Items;
        }

        public void setOptions1Items(List<String> options1Items) {
            this.options1Items = options1Items;
        }

        public List<List<String>> getOptions2Items() {
            return options2Items;
        }

        public void setOptions2Items(List<List<String>> options2Items) {
            this.options2Items = options2Items;
        }

        public List<List<List<String>>> getOptions3Items() {
            return options3Items;
        }

        public void setOptions3Items(List<List<List<String>>> options3Items) {
            this.options3Items = options3Items;
        }
    }
}
