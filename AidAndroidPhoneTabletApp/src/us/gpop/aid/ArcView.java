package us.gpop.aid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ArcView extends View {

	public ArcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public ArcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ArcView(Context context) {
		super(context);
		init(context);
	}

	private Bitmap mRedBitmap;
	private Bitmap mGreenBitmap;
	private Bitmap mYellowBitmap;

	private Paint mRedPaint;
	private Paint mGreenPaint;
	private Paint mYellowPaint;

	private RectF mOval;
	private float mAngle = 0;
	
	private String mText = "";

	private Paint mTextPaint;
	
	public void setText(final String newText) {
		mText = newText;
	}

	private void init(Context context) {
		
		mTextPaint = new Paint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(32f);
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));
		
		// use your bitmap insted of R.drawable.ic_launcher
		mRedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.progress_circular_background_red);
		mGreenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.progress_circular_background_green);
		mYellowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.progress_circular_background_yellow);

		mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mYellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mOval = new RectF();

		// mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// mTextPaint.setTextSize(48);
		// mTextPaint.setTextAlign(Align.CENTER);
		// mTextPaint.setColor(0xffffaa00);
		// mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}

	public float getAngle() {
		return mAngle;
	}

	public void setAngle(float mAngle) {
		this.mAngle = mAngle;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		{
			Matrix m = new Matrix();
			RectF src = new RectF(0, 0, mRedBitmap.getWidth(), mRedBitmap.getHeight());
			RectF dst = new RectF(0, 0, w, h);
			m.setRectToRect(src, dst, ScaleToFit.CENTER);
			Shader shader = new BitmapShader(mRedBitmap, TileMode.CLAMP, TileMode.CLAMP);
			shader.setLocalMatrix(m);
			mRedPaint.setShader(shader);
			m.mapRect(mOval, src);
		}
		{

			Matrix m = new Matrix();
			RectF src = new RectF(0, 0, mGreenBitmap.getWidth(), mGreenBitmap.getHeight());
			RectF dst = new RectF(0, 0, w, h);
			m.setRectToRect(src, dst, ScaleToFit.CENTER);
			Shader shader = new BitmapShader(mGreenBitmap, TileMode.CLAMP, TileMode.CLAMP);
			shader.setLocalMatrix(m);
			mGreenPaint.setShader(shader);
			m.mapRect(mOval, src);
		}
		{
			Matrix m = new Matrix();
			RectF src = new RectF(0, 0, mYellowBitmap.getWidth(), mYellowBitmap.getHeight());
			RectF dst = new RectF(0, 0, w, h);
			m.setRectToRect(src, dst, ScaleToFit.CENTER);
			Shader shader = new BitmapShader(mYellowBitmap, TileMode.CLAMP, TileMode.CLAMP);
			shader.setLocalMatrix(m);
			mYellowPaint.setShader(shader);
			m.mapRect(mOval, src);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mAngle < 120) {
		// canvas.drawColor(0xff0000aa);
		canvas.drawArc(mOval, -90, mAngle, true, mRedPaint);
		} else if (mAngle < 240) {
			canvas.drawArc(mOval, -90, mAngle, true, mYellowPaint);
			
		} else {
			canvas.drawArc(mOval, -90, mAngle, true, mGreenPaint);
			
		}
		
		canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
	}

	public interface ArcTouchListener {
		public boolean onArcTouch(float newAngle) ;
	}
	
	public ArcTouchListener listener;
		
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float w2 = getWidth() / 2f;
		float h2 = getHeight() / 2f;
		float newmAngle = (float) Math.toDegrees(Math.atan2(event.getY() - h2, event.getX() - w2));
		newmAngle += 90 + 360;
		newmAngle %= 360;

		if ( null != listener ) {
			return listener.onArcTouch(newmAngle);
		}
		
		return super.onTouchEvent(event);
	}
	

}
