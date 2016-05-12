package com.victor;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.victor.R;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择@的人的界面
 * 
 * @author Victor
 * @email 468034043@qq.com
 * @time 2016年5月11日 下午3:15:49
 */
public class AtPeopleListActivity extends Activity implements OnItemClickListener {

	private ListView mListView;
	private AtPeopleListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_at_people_list);

		mListView = (ListView) findViewById(R.id.mListView);
		mAdapter = new AtPeopleListAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EventBus.getDefault().post(mAdapter.getItem(position).toString());
		finish();
	}

	/**
	 * 选择@的人的列表适配器
	 * 
	 * @author Victor
	 * @email 468034043@qq.com
	 * @time 2016年5月11日 下午3:18:42
	 */
	private class AtPeopleListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return "@张三" + position + " ";
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 只是测试demo，没有对ListView进行优化
			if (convertView == null) {
				convertView = View.inflate(AtPeopleListActivity.this, android.R.layout.simple_list_item_1, null);
			}
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			text1.setText("@张三" + position);
			return convertView;
		}

	}

}
