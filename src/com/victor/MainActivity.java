package com.victor;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.victor.widget.WeiboTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText et_content;
	private WeiboTextView tv_result;

	private List<String> mAtList = new ArrayList<String>();// @的人的列表
	private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<ForegroundColorSpan>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		EventBus.getDefault().register(this);

		et_content = (EditText) findViewById(R.id.et_content);
		tv_result = (WeiboTextView) findViewById(R.id.tv_result);

		// // 一,实现话题变色
		// et_content.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// if (TextUtils.isEmpty(s))
		// return;
		// String content = s.toString();
		//
		// // 2,为查找出的变色
		// // 首先要为editable,去除之前设置的colorSpan
		// Editable editable = et_content.getText();
		// for (int i = 0; i < mColorSpans.size(); i++) {
		// editable.removeSpan(mColorSpans.get(i));
		// }
		// mColorSpans.clear();
		// int findPos = 0;
		// for (int i = 0; i < mAtList.size(); i++) {// 遍历at的人
		// String at = mAtList.get(i);
		// findPos = content.indexOf(at, findPos);
		// if (findPos != -1) {
		// ForegroundColorSpan colorSpan = new
		// ForegroundColorSpan(getResources().getColor(R.color.red));
		// editable.setSpan(colorSpan, findPos, findPos = findPos + at.length(),
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// mColorSpans.add(colorSpan);
		// }
		// }
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });

		// 三,实现话题选中删除效果
//		et_content.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
//
//					int selectionStart = et_content.getSelectionStart();
//					int selectionEnd = et_content.getSelectionEnd();
//					// 如果光标起始和结束在同一位置,说明是选中效果,直接返回 false 交给系统执行删除动作
//					if (selectionStart != selectionEnd) {
//						return false;
//					}
//
//					Editable editable = et_content.getText();
//					String content = editable.toString();
//					int lastPos = 0;
//					// 遍历判断光标的位置
//					for (int i = 0; i < mAtList.size(); i++) {
//						String at = mAtList.get(i);
//						lastPos = content.indexOf(at, lastPos);
//						if (lastPos != -1) {
//							if (selectionStart != 0 && selectionStart >= lastPos
//									&& selectionStart <= (lastPos + at.length())) {// 此循环的作用是设置要被删除的部分选中
//								// 选中@的人
//								et_content.setSelection(lastPos, lastPos + at.length());
//								// 从集合中删除
//								mAtList.remove(i);
//								return false;
//							}
//						}
//						lastPos += at.length();
//					}
//				}
//				return false;
//			}
//		});

		// 四,实现点击话题,光标在话题之后
		// et_content.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// int selectionStart = et_content.getSelectionStart();
		// int lastPos = 0;
		// for (int i = 0; i < mAtList.size(); i++) {
		// String at = mAtList.get(i);
		// lastPos = et_content.getText().toString().indexOf(at, lastPos);
		//
		// if (lastPos != -1) {
		// if (selectionStart >= lastPos && selectionStart <= (lastPos +
		// at.length())) {
		// // 在这position 区间就移动光标
		// et_content.setSelection(lastPos + at.length());
		// }
		// }
		// lastPos = lastPos + at.length();
		// }
		// }
		// });

		String source = "@张三 超链接测试#正在发生#幽默搞笑#victor#毕业照走@victor 红网络 http://www.baidu.com";
		tv_result.setText(source);
		tv_result.setOnLinkClickListener(new WeiboTextView.OnLinkClickListener() {

			@Override
			public void onUrlClick(String url) {
				showToast("点击了链接：" + url);
			}

			@Override
			public void onTopicClick(String topic) {
				showToast("点击了话题：" + topic);
			}

			@Override
			public void onAtClick(String at) {
				showToast("点击了@：" + at);
			}
		});
	}

	private void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	@Subscribe
	public void onGetAtOrTopic(String text) {
		mAtList.add(text);
		et_content.append(text);
		et_content.requestFocus();
	}

	/**
	 * 去选择@的人
	 */
	public void gotoSelectAtPeople(View v) {
		startActivity(new Intent(this, AtPeopleListActivity.class));
	}

	/**
	 * 去选择##话题
	 */
	public void gotoSelectTopic(View v) {
		startActivity(new Intent(this, TopicListActivity.class));
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
