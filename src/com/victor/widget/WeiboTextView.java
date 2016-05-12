package com.victor.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.victor.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * 仿新浪微博TextView
 * 
 * @author Victor
 * @email 468034043@qq.com
 * @time 2016年5月11日 下午4:20:57
 */
public class WeiboTextView extends TextView {
	private static final int DEFAULT_LINK_HIGHLIGHT_COLOR = Color.BLUE;// 默认链接高亮颜色
	// 定义正则表达式
	private static final String AT = "@[\u4e00-\u9fa5\\w]+";// @人
	private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
	private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
	private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
	private static final String REGEX = "(" + AT + ")|(" + TOPIC + ")|(" + EMOJI + ")|(" + URL + ")";

	private int mLinkHighlightColor;// 链接高亮的颜色，默认蓝色

	private OnLinkClickListener mOnLinkClickListener;

	public WeiboTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public WeiboTextView(Context context) {
		super(context);
		initView(context, null);
	}

	private void initView(Context context, AttributeSet attrs) {
		// 要实现文字的点击效果，这里需要做特殊处理
		setMovementMethod(LinkMovementMethod.getInstance());

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
	 * 因为父类的setText(CharSequence text)是final的，只能重写该方法
	 * 
	 * 注：实际上setText(CharSequence text)内部也是调用了该方法
	 * 
	 * @param text
	 * @param type
	 */
	@Override
	public void setText(CharSequence text, BufferType type) {
		// super.setText(text, type);
		super.setText(getWeiboContent(text), type);
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

		// 设置正则
		Pattern mPattern = Pattern.compile(REGEX);
		Matcher matcher = mPattern.matcher(spannableString);

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
					public void onClick(View v) {
						if (mOnLinkClickListener != null) {
							mOnLinkClickListener.onAtClick(at);
						}
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
					public void onClick(View v) {
						if (mOnLinkClickListener != null) {
							mOnLinkClickListener.onTopicClick(topic);
						}
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
					public void onClick(View v) {
						if (mOnLinkClickListener != null) {
							mOnLinkClickListener.onUrlClick(url);
						}
					}
				};
				spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return spannableString;
	}

	public OnLinkClickListener getOnLinkClickListener() {
		return mOnLinkClickListener;
	}

	public void setOnLinkClickListener(OnLinkClickListener mOnLinkClickListener) {
		this.mOnLinkClickListener = mOnLinkClickListener;
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

	/**
	 * 链接点击的监听器
	 * 
	 * @author Victor
	 * @email 468034043@qq.com
	 * @time 2016年5月11日 下午5:15:23
	 */
	public static interface OnLinkClickListener {
		/**
		 * 点击了@的人， 如"@victor"
		 * 
		 * @param at
		 */
		void onAtClick(String at);

		/**
		 * 点击了话题，如"#中国#"
		 * 
		 * @param topic
		 */
		void onTopicClick(String topic);

		/**
		 * 点击了url，如"http://www.google.com"
		 * 
		 * @param url
		 */
		void onUrlClick(String url);
	}
}
