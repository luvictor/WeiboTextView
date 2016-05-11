package com.victor.edittext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 仿新浪微博TextView
 * 
 * @author Victor
 * @email 468034043@qq.com
 * @time 2016年5月11日 下午4:20:57
 */
public class WeiboTextView extends TextView {
	// 定义正则表达式
	private static final String AT = "@[\u4e00-\u9fa5\\w]+";// @人
	private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
	private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
	private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url

	@TargetApi(21)
	public WeiboTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initView(context);
	}

	public WeiboTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public WeiboTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public WeiboTextView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		// 要实现文字的点击效果，这里需要做特殊处理
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// super.setText(text, type);
		super.setText(getWeiboContent(getContext(), text, this), type);
	}

	/**
	 * 设置微博内容样式
	 * 
	 * @param context
	 * @param source
	 * @param textView
	 * @return
	 */
	public SpannableString getWeiboContent(final Context context, CharSequence source, TextView textView) {
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
				MyClickableSpan clickableSpan = new MyClickableSpan() {

					@Override
					public void onClick(View widget) {
						// 这里需要做跳转用户的实现，先用一个Toast代替
						Toast.makeText(context, "点击了用户：" + at, Toast.LENGTH_LONG).show();
					}
				};
				spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// 处理话题##符号
			if (topic != null) {
				int start = matcher.start(2);
				int end = start + topic.length();
				MyClickableSpan clickableSpan = new MyClickableSpan() {

					@Override
					public void onClick(View widget) {
						Toast.makeText(context, "点击了话题：" + topic, Toast.LENGTH_LONG).show();
					}
				};
				spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
				MyClickableSpan clickableSpan = new MyClickableSpan() {

					@Override
					public void onClick(View widget) {
						Toast.makeText(context, "点击了网址：" + url, Toast.LENGTH_LONG).show();
					}
				};
				spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
	public static class MyClickableSpan extends ClickableSpan {

		@Override
		public void onClick(View widget) {

		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(Color.BLUE);
			ds.setUnderlineText(false);
		}

	}
}
