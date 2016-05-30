package io.oversky524.androidui;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    private static final String ACTIVITY_ACTION_DEMO = "android.intent.action.UiDemos";
    private static class Data{
        String label;
        String className;
        String packageName;
    }
    private ArrayList<Data> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = getPackageManager();
        Intent intent = new Intent(ACTIVITY_ACTION_DEMO);
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        for(ResolveInfo info : infos){
            Data data = new Data();
            ActivityInfo activityInfo = info.activityInfo;
            try{
                data.label = getString(activityInfo.labelRes);
            }catch (Throwable t) {
                data.label = activityInfo.name.substring(activityInfo.name.lastIndexOf('.') + 1);
            }
            data.packageName = activityInfo.packageName;
            data.className = activityInfo.name;
            datas.add(data);
        }

        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return datas.size();
            }

            @Override
            public Object getItem(int position) {
                return datas.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                Data d = datas.get(position);
                convertView.setTag(d);
                ((TextView)convertView).setText(d.label);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Data data = (Data)v.getTag();
                        startActivity(new Intent()
                                .setComponent(new ComponentName(data.packageName, data.className)));
                    }
                });
                return convertView;
            }
        });
    }
}
