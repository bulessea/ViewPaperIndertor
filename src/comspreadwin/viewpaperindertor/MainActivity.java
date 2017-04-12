package comspreadwin.viewpaperindertor;

import com.spreadwin.viewpaperindertor.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MainActivity extends Activity {
	private static final String TAG="MAINACTIVITY";
    private FlymeTabStrip tabStrip;
    private String[] titles = new String[] { "Flyme", "ú��", "����", "������", "����", "��ȫ����", "��ҳ",
            "��ϵ��", "�绰" };
//private String[] titles = new String[] { "Flyme", "ú��", "����","������"};
    /**
     * ָʾ��ƫ�ƿ��
     */
    private int offsetWidth = 0;

    private ViewPager mViewPager;

    /**
     * viewPager���
     */
    private int screenWith = 0;
    /**
     * viewPager�߶�
     */
    private int screeHeight = 0;

//    private int[] drawableResIds = {R.mipmap.mm1,R.mipmap.mm2,R.mipmap.mm3,R.mipmap.mm4,R.mipmap.mm5,
//            R.mipmap.mm6,R.mipmap.mm7,R.mipmap.mm8};
    private int[] drawableResIds = {R.drawable.mm1,R.drawable.mm2,R.drawable.mm3,R.drawable.mm4,R.drawable.mm1,R.drawable.mm2};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tabStrip = (FlymeTabStrip) findViewById(R.id.tabstrip);
        screenWith = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        screeHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight()-dip2px(this, 45);
     
        mViewPager.setAdapter(new ViewPagerAdapter());
        tabStrip.setViewPager(mViewPager);
    }
    private class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return drawableResIds.length;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            ImageView imageView = (ImageView) LayoutInflater.from(MainActivity.this).inflate(R.layout.image_display, null);
            imageView.setImageBitmap(adjustBitmapSimpleSize(drawableResIds[position]));
            imageView.setTag(position);
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            ImageView image = (ImageView)((ViewPager) container).findViewWithTag(position);
            ((ViewPager) container).removeView(image);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0==arg1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
            return titles[position];
        }
    }
    /**
     * ����ѹ��������
     * @param resId
     * @return
     */
    private Bitmap adjustBitmapSimpleSize(int resId)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resId, opts);
        int visibleHeight = screeHeight;
        int visibleWidth = screenWith;
        if(opts.outWidth>visibleWidth ||opts.outHeight>visibleHeight)
        {
            float wRatio =  opts.outWidth/visibleWidth;
            float hRatio =  opts.outHeight/visibleHeight;
            opts.inSampleSize = (int) Math.max(wRatio, hRatio);
        }
        opts.inJustDecodeBounds = false;
        if(bitmap!=null){
            bitmap.recycle();
        }
        return BitmapFactory.decodeResource(getResources(),resId, opts);
    }


    /**
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

