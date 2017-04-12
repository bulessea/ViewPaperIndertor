package comspreadwin.viewpaperindertor;

import com.spreadwin.viewpaperindertor.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FlymeTabStrip extends HorizontalScrollView {
	private static final String TAG = "TABSTRIP";
	/**
	 * ָʾ������
	 */
	private LinearLayout container;
	/**
	 * ָʾ������
	 */
	private int tabCount;
	/**
	 * ��ǰtabλ�ã�Ĭ��Ϊ0
	 */
	private int currentPosition = 0;
	/**
	 * ѡ�е�tabλ��
	 */
	private int selectedPosition;
	/**
	 *
	 */
	private float currentPositionOffset = 0f;
	/**
	 *
	 */
	private int lastScrollX = 0;
	/**
	 * LayoutParams�������ָʾ����ָʾ��������ʱʹ�ã�����Ȩ�ط���ָʾ�����
	 */
	private LinearLayout.LayoutParams expandedTabLayoutParams;
	/**
	 * ָʾ����ɫ
	 */
	private int indicatorColor;
	/**
	 * ������ɫ
	 */
	private int textColor;
	/**
	 * ���ִ�С
	 */
	private int textSize;
	/**
	 * ѡ��λ�õ����ִ�С
	 */
	private int selectedTextSize;
	/**
	 * ָʾ���߶�
	 */
	private int indicatorHeight;
	/**
	 * ָʾ�����Ҽ��
	 */
	private int indicatorMargin;
	/**
	 * ViewPager
	 */
	private ViewPager viewPager;
	/**
	 * viewpager��������
	 */
	private PagerAdapter pagerAdapter;

	/**
	 * page�ı������
	 */
	private final PagerStateChangeListener pagerStateChangeListener = new PagerStateChangeListener();
	/**
	 * ����
	 */
	private Paint paint;
	private Context context;

//	public FlymeTabStrip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//		init(context, attrs, defStyleAttr, defStyleRes);
//	}

	public FlymeTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	public FlymeTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public FlymeTabStrip(Context context) {
		super(context);
		init(context, null, 0, 0);
	}

	/**
	 * ��ʼ��
	 *
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 * @param defStyleRes
	 */
	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		this.context = context;
		// ȡ������Ĺ�����
		setHorizontalScrollBarEnabled(false);
		// ָʾ��������ʼ��
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setLayoutParams(new LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		// ���ָʾ��������scrollview
		addView(container);

		// ��ȡ��Ļ�����Ϣ
		DisplayMetrics dm = getResources().getDisplayMetrics();

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.FlymeTabStrip, defStyleAttr, defStyleRes);
		int n = typedArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = typedArray.getIndex(i);
			if (attr == R.styleable.FlymeTabStrip_indicatorColor) {
				indicatorColor = typedArray.getColor(attr, Color.YELLOW);

				// ָʾ���߶ȣ�Ĭ��2
			} else if (attr == R.styleable.FlymeTabStrip_indicatorHeight) {
				indicatorHeight = typedArray.getDimensionPixelSize(attr, 2);

				// ָʾ�����Ҽ�࣬Ĭ��20
			} else if (attr == R.styleable.FlymeTabStrip_indicatorMargin) {
				indicatorMargin = typedArray.getDimensionPixelSize(attr,30);

				// ������ɫ,Ĭ�Ϻ�ɫ
			} else if (attr == R.styleable.FlymeTabStrip_indicatorTextColor) {
				textColor = typedArray.getColor(attr, Color.BLACK);

				// ���ִ�С��Ĭ��15
			} else if (attr == R.styleable.FlymeTabStrip_indicatorTextSize) {
				textSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
										TypedValue.COMPLEX_UNIT_SP, 15, dm)) / 3;

				// ѡ��������ִ�С��Ĭ��18
			} else if (attr == R.styleable.FlymeTabStrip_selectedIndicatorTextSize) {
				selectedTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
										TypedValue.COMPLEX_UNIT_SP, 18, dm)) / 3;

			} else {
			}
		}
		// typedArray����
		typedArray.recycle();

		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1.0f);
		// ��ʼ������
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// ���ָʾ������Ϊ0��ֱ�ӽ����滭
		if (tabCount == 0) {
			return;
		}
		// ��ȡonMeasure��ĸ�
		final int height = getHeight();
		/*
		 * ��ָʾ���·�����
		 */
		// ������ɫ
		paint.setColor(indicatorColor);
		// ��ǰָʾtabλ��
		View currentTab = container.getChildAt(currentPosition);
		// ��ǰtab�������Ը���������߾�
		float leftPadding = currentTab.getLeft();
		// ��ǰtab���ұ�����ڸ�������߾�
		float rightPadding = currentTab.getRight();
		float tempPadding = 20;
		// �������λ��

		float centerPosition = 0.0f;

		if (currentPositionOffset >= 0f && currentPosition < tabCount - 1) {
			View nextTab = container.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();
			// λ�����ڻ��������в��ϱ仯��
			leftPadding = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * leftPadding);
			rightPadding = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * rightPadding);
		}
		centerPosition = (rightPadding - leftPadding)/2 + leftPadding;
		float left = centerPosition - tempPadding - formatPercent(currentPositionOffset)*tempPadding;
		float right = centerPosition + tempPadding + formatPercent(currentPositionOffset)*tempPadding;

		// ����
		canvas.drawRect(left, height - indicatorHeight, right, height, paint);

	}

	/**
	 * ����ViewPager
	 *
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		if (viewPager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not has a adapter instance");
		} else {
			pagerAdapter = viewPager.getAdapter();
		}
		viewPager.setOnPageChangeListener(pagerStateChangeListener);
		update();
	}

	/**
	 * ���½���
	 */
	private void update() {
		// ָʾ�������Ƴ�������view
		container.removeAllViews();
		// ��ȡָʾ������
		tabCount = pagerAdapter.getCount();
		// ������ָʾ��
		for (int i = 0; i < tabCount; i++) {
			addTab(i, pagerAdapter.getPageTitle(i));
		}
		// ����Tab��ʽ
		updateTabStyle();
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				currentPosition = viewPager.getCurrentItem();
				scrollToChild(currentPosition, 0);
			}
		});
	}

	/**
	 * ����ScrollView
	 *  position
	 *  offset
	 */
	private void scrollToChild(int position, int offset) {
		if (tabCount == 0) {
			return;
		}
		int newScrollX = container.getChildAt(position).getLeft() + offset;
		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}
	}

	/**
	 * ���ָʾ��
	 *
	 * @param position
	 * @param title
	 */
	private void addTab(final int position, CharSequence title) {
		TextView tvTab = new TextView(context);
		tvTab.setText(title);
		tvTab.setTextColor(textColor);
		tvTab.setTextSize(textSize);
		tvTab.setGravity(Gravity.CENTER);
		tvTab.setSingleLine();
		tvTab.setFocusable(true);
		tvTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(position);
			}
		});
		tvTab.setPadding(indicatorMargin, 0, indicatorMargin, 0);
		container.addView(tvTab, position, expandedTabLayoutParams);
	}

	/**
	 * ����ָʾ����ʽ
	 */
	private void updateTabStyle() {
		for (int i = 0; i < tabCount; i++) {
			TextView tab = (TextView) container.getChildAt(i);
			if (i == selectedPosition) {
				// ����ѡ�е�ָʾ��������ɫ�ʹ�С
				tab.setTextColor(indicatorColor);
				tab.setTextSize(selectedTextSize);
			} else {
				tab.setTextColor(textColor);
				tab.setTextSize(textSize);
			}
		}
	}

	/**
	 * ��ƫ�ưٷֱȽ��и�ʽ�� ��֤��һ��С���� Ȼ��С��һ���շ����
	 * �������ܱ�֤����ָʾ����һ�����������Ź���ƫ�Ʋ��ϱ仯������
	 * @param percent
	 * @return
     */
	private float formatPercent(float percent){

		float adsP = (float) Math.abs(percent - 0.5f);

		float valueP = Math.abs(0.5f - adsP);
		return valueP;
	}

	/**
	 * viewPager״̬�ı����
	 *
	 */
	private class PagerStateChangeListener implements OnPageChangeListener {

		/**
		 * viewpager״̬����
		 * @param state
         */
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {  // 0 ����״̬  pager���ڿ���״̬
				scrollToChild(viewPager.getCurrentItem(), 0);
			}else if(state == ViewPager.SCROLL_STATE_SETTLING){ // 2 �����Զ��������൱�����ֺ�pager�ָ���һ������pager�Ĺ���

			}else if(state == ViewPager.SCROLL_STATE_DRAGGING){  // 1 viewpager���ڱ�����,����������ק��

			}
		}

		/**
		 * viewpager���ڻ�������ص�һЩƫ����
		 * ����ʱ��ֻҪ����ָʾ���·����ߵĹ���
		 * @param position ��ǰҳ��
		 * @param positionOffset  ��ǰҳ��ƫ�Ƶİٷֱ�
		 * @param positionOffsetPixels ��ǰҳ��ƫ�Ƶ�����ֵ
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			currentPosition = position;
			currentPositionOffset = positionOffset;
			// ����ָʾ���·����ߵĹ���,scrollToChild�᲻�ϵ���ondraw�������������ػ��»��ߣ�������ƶ�����Ч��
			scrollToChild(position, (int) (positionOffset * container.getChildAt(position).getWidth()));
			invalidate();
		}

		/**
		 * page��������
		 * @param position  ����������ѡ�е�ҳ��
		 */
		@Override
		public void onPageSelected(int position) {
			// �����������δ֪
			selectedPosition = position;
			// ����ָʾ��״̬
			updateTabStyle();
		}

	}
}
