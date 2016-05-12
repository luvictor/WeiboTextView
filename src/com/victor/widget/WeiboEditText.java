package com.victor.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.victor.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 
 * @author Victor
 * @email 468034043@qq.com
 * @time 2016年5月12日 上午9:30:41
 */
public class WeiboEditText extends EditText {
	private static final int DEFAULT_LINK_HIGHLIGHT_COLOR = Color.BLUE;// 默认链接高亮颜色
	private int mLinkHighlightColor = DEFAULT_LINK_HIGHLIGHT_COLOR;// 链接高亮的颜色，默认蓝色
	// 定义正则表达式
	private static final String AT = "@[\u4e00-\u9fa5\\w]+\\s";// @人（末尾含空格）
	private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
	private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
	private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
	private static final String REGEX = "(" + AT + ")|(" + TOPIC + ")|(" + EMOJI + ")|(" + URL + ")";

	private MyClickableSpan clickableSpan = new MyClickableSpan();
	private Pattern pattern;

	public WeiboEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public WeiboEditText(Context context) {
		super(context);
		initView(context, null);
	}

	private void initView(Context context, AttributeSet attrs) {
		// 要实现文字的点击效果，这里需要做特殊处理
		setMovementMethod(LinkMovementMethod.getInstance());

		pattern = Pattern.compile(REGEX);

		// 解析自定义属性
		applyAttributes(attrs);

		// 一、实现高亮颜色
		applyHighlightColor();

		// 三、实现话题选中删除效果
		/**
		 * 原理：点击键盘上删除按钮的时候，要判断光标前是普通字符，还是特殊字符串(@,#),
		 * 1、如果是普通字符，那么直接交由系统删除（return false;）
		 * 2、若是特殊字符串(@,#)， 则先设置特殊字符串文本选中，消耗掉删除事件(return true), 再次点击的时候，执行删除操作
		 */
		this.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

					int selectionStart = getSelectionStart();
					int selectionEnd = getSelectionEnd();
					// 如果光标起始和结束在同一位置,说明是选中效果,直接返回 false 交给系统执行删除动作
					if (selectionStart != selectionEnd) {
						return false;// false表示交由系统执行删除
					}

					Editable editable = getText();
					String content = editable.toString();
					SpannableString spannableString = new SpannableString(content);
					int lastPos = 0;
					// 遍历判断光标的位置
					if (TextUtils.isEmpty(content)) {
						return false;
					}
					Matcher matcher = pattern.matcher(content);

//					if (matcher.find()) {
//						// 重置正则位置
//						matcher.reset();
//					}

					while (matcher.find()) {
						// 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
						final String at = matcher.group(1);
						final String topic = matcher.group(2);
						String emoji = matcher.group(3);
						final String url = matcher.group(4);

						// 处理@符号
						if (at != null) {
							// 获取匹配位置
							int start = matcher.start(1);
							int end = start + at.length();
							lastPos = content.indexOf(at, lastPos);
							if (lastPos != -1) {
								if (selectionStart != 0 && selectionStart >= lastPos
										&& selectionStart <= (lastPos + at.length())) {
									// 选中@的人
//									setSelection(lastPos, lastPos + at.length());
									setSelection(start, end);
									return false;
								}
								lastPos += at.length();
							}
							setSelection(start, end);
							return true;
						}

						// 处理话题##符号
						if (topic != null) {
							int start = matcher.start(2);
							int end = start + topic.length();
							spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}

						if (emoji != null) {

							int start = matcher.start(3);
							int end = start + emoji.length();
							// int ResId = EmotionUtils.getImgByName(emoji);
							// Bitmap bitmap =
							// BitmapFactory.decodeResource(context.getResources(),
							// ResId);
							// if (bitmap != null) {
							// // 获取字符的大小
							// int size = (int) textView.getTextSize();
							// // 压缩Bitmap
							// bitmap = Bitmap.createScaledBitmap(bitmap, size,
							// size, true);
							// // 设置表情
							// ImageSpan imageSpan = new ImageSpan(context,
							// bitmap);
							// spannableString.setSpan(imageSpan, start, end,
							// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							// }
						}

						// 处理url地址
						if (url != null) {
							int start = matcher.start(4);
							int end = start + url.length();
							spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
					// for (int i = 0; i < mAtList.size(); i++) {
					// String at = mAtList.get(i);
					// lastPos = content.indexOf(at, lastPos);
					// if (lastPos != -1) {
					// if (selectionStart != 0 && selectionStart >= lastPos
					// && selectionStart <= (lastPos + at.length())) {
					// // 选中@的人
					// setSelection(lastPos, lastPos + at.length());
					// return false;
					// }
					// }
					// lastPos += at.length();
					// }
				}
				return false;
			}
		});

	}

	private void applyHighlightColor() {
		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int startw, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				SpannableString spannableString = new SpannableString(s);

				// 设置正则
				Pattern pattern = Pattern.compile(REGEX);
				Matcher matcher = pattern.matcher(s);

				if (matcher.find()) {
					// 重置正则位置
					matcher.reset();
				}

				while (matcher.find()) {
					// 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
					final String at = matcher.group(1);
					final String topic = matcher.group(2);
					String emoji = matcher.group(3);
					final String url = matcher.group(4);

					// 处理@符号
					if (at != null) {
						// 获取匹配位置
						int start = matcher.start(1);
						int end = start + at.length();
						spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						getText().setSpan(spannableString, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}

					// 处理话题##符号
					if (topic != null) {
						int start = matcher.start(2);
						int end = start + topic.length();
						spannableString.setSpan(null, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						getText().setSpan(spannableString, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}

					if (emoji != null) {

						int start = matcher.start(3);
						int end = start + emoji.length();
						// int ResId = EmotionUtils.getImgByName(emoji);
						// Bitmap bitmap =
						// BitmapFactory.decodeResource(context.getResources(),
						// ResId);
						// if (bitmap != null) {
						// // 获取字符的大小
						// int size = (int) textView.getTextSize();
						// // 压缩Bitmap
						// bitmap = Bitmap.createScaledBitmap(bitmap, size,
						// size, true);
						// // 设置表情
						// ImageSpan imageSpan = new ImageSpan(context, bitmap);
						// spannableString.setSpan(imageSpan, start, end,
						// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						// }
					}

					// 处理url地址
					if (url != null) {
						int start = matcher.start(4);
						int end = start + url.length();
						spannableString.setSpan(null, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						getText().setSpan(spannableString, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		});
	}

	/**
	 * 解析自定义属性
	 * 
	 * @param attrs
	 */
	private void applyAttributes(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WeiboTextView);

			// 设置链接高亮颜色
			int mLinkHighlightColor = typedArray.getColor(R.styleable.WeiboTextView_linkHighlightColor,
					DEFAULT_LINK_HIGHLIGHT_COLOR);
			if (mLinkHighlightColor != 0) {
				setLinkHighlightColor(mLinkHighlightColor);
			}

			typedArray.recycle();
		}
	}

	public void setLinkHighlightColor(int mLinkHighlightColor) {
		this.mLinkHighlightColor = mLinkHighlightColor;
	}

	public int getLinkHightlightColor() {
		return this.mLinkHighlightColor;
	}

	/**
	 * 设置微博内容样式
	 * 
	 * @param context
	 * @param source
	 * @param textView
	 * @return
	 */
	public SpannableString getWeiboContent(CharSequence source) {
		SpannableString spannableString = new SpannableString(source);

		String REGEX = "(" + AT + ")|(" + TOPIC + ")|(" + EMOJI + ")|(" + URL + ")";
		// 设置正则
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(spannableString);

		if (matcher.find()) {
			// 重置正则位置
			matcher.reset();
		}

		while (matcher.find()) {
			// 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
			final String at = matcher.group(1);
			final String topic = matcher.group(2);
			String emoji = matcher.group(3);
			final String url = matcher.group(4);

			// 处理@符号
			if (at != null) {
				// 获取匹配位置
				int start = matcher.start(1);
				int end = start + at.length();
				spannableString.setSpan(null, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// 处理话题##符号
			if (topic != null) {
				int start = matcher.start(2);
				int end = start + topic.length();
				spannableString.setSpan(null, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (emoji != null) {

				int start = matcher.start(3);
				int end = start + emoji.length();
				// int ResId = EmotionUtils.getImgByName(emoji);
				// Bitmap bitmap =
				// BitmapFactory.decodeResource(context.getResources(), ResId);
				// if (bitmap != null) {
				// // 获取字符的大小
				// int size = (int) textView.getTextSize();
				// // 压缩Bitmap
				// bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
				// // 设置表情
				// ImageSpan imageSpan = new ImageSpan(context, bitmap);
				// spannableString.setSpan(imageSpan, start, end,
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// }
			}

			// 处理url地址
			if (url != null) {
				int start = matcher.start(4);
				int end = start + url.length();
				spannableString.setSpan(null, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return spannableString;
	}

	/**
	 * 继承ClickableSpan复写updateDrawState方法，自定义所需样式
	 * 
	 * @author Rabbit_Lee
	 *
	 */
	private class MyClickableSpan extends ClickableSpan {

		@Override
		public void onClick(View v) {

		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(mLinkHighlightColor);
			ds.setUnderlineText(false);
		}

	}
}
