package com.tony.ReadCardDemo;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ming.util.android.AUtil;
import com.ming.util.common.Util;

public class LineInputView extends RelativeLayout
{
    private TextView titleView;
    public EditText messageView;
    public LineInputView(Context context) {
        super(context);

        int margin = AUtil.getDip(context,1);
        titleView = new TextView(context);
        titleView.setId(AUtil.getId());
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        messageView = new EditText(context);
        messageView.setId(AUtil.getId());
        messageView.setGravity(Gravity.CENTER_VERTICAL);


        LayoutParams titleView_params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        titleView_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        titleView_params.addRule(RelativeLayout.CENTER_VERTICAL);
        titleView_params.setMargins(margin,margin,margin,margin);
        titleView.setLayoutParams(titleView_params);

        LayoutParams messageView_params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        messageView_params.addRule(RelativeLayout.RIGHT_OF,titleView.getId());
        messageView_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        messageView_params.addRule(RelativeLayout.CENTER_VERTICAL);
        messageView_params.setMargins(margin,margin,margin,margin);
        messageView.setLayoutParams(messageView_params);

        this.addView(titleView);
        this.addView(messageView);

        titleView.setBackgroundColor(0xFFE5DED3);
        titleView.setSingleLine();
        titleView.setTextColor(0xFF333333);


        messageView.setBackgroundColor(0xFFE5DED3);
        messageView.setTextColor(0xFF333333);

    }

    public void setTitle(String text) {
        titleView.setText(" "+text+" ");
    }

    boolean isRelayout = false;
    public void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed,l,t,r,b);
        if(!isRelayout)
        {
            int width = getWidth();

            LayoutParams titleView_params = (LayoutParams) titleView.getLayoutParams();
            titleView_params.width = width*20/100;
            titleView.setLayoutParams(titleView_params);

//        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,(width*20/100)/5);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
            isRelayout = true;
        }
    }

    public String getText() {

        String value = "";
        if(messageView.getText()!=null)
        {
            value = messageView.getText().toString().trim();

        }
//        if(Util.isNull(value))
//        {
//            value = "6666";
//        }

        return value;
    }

    public void setText(String text) {
        messageView.setText(" "+text);
    }
}
