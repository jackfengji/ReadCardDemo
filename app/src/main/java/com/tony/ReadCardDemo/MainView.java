package com.tony.ReadCardDemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ming.util.android.AUtil;
import com.ming.util.common.Base64;
import com.ming.util.common.Util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class MainView extends RelativeLayout
{
    public MainView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context)
    {
        int margin = AUtil.getDip(context,5);

        ImageView bgView = new ImageView(context);
        bgView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg));
        bgView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams bgView_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        bgView_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bgView_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        bgView.setLayoutParams(bgView_params);
        this.addView(bgView);

        TextView copyrightView = new TextView(context);
        copyrightView.setText("科技战疫 - 北京增强智能科技有限公司技术支持");
        copyrightView.setTextColor(Color.WHITE);
        copyrightView.setShadowLayer(10,10,10,Color.BLACK);
        LayoutParams copyrightView_params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        copyrightView_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        copyrightView_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        copyrightView_params.setMargins(margin,margin,margin*5,margin);
        copyrightView.setLayoutParams(copyrightView_params);
        this.addView(copyrightView);


        TextView titleView = new TextView(context);
        titleView.setId(AUtil.getId());
        titleView.setTextColor(Color.WHITE);
        titleView.setShadowLayer(10,10,10,Color.BLACK);
        titleView.setGravity(Gravity.CENTER);
        titleView.setText("智慧锦江·小锦 - 访客登记系统");

        View topView = makeTopView(context);
        topView.setId(AUtil.getId());

        View middleView = makeMiddleView(context);
        middleView.setId(AUtil.getId());

        View bottomView = makeBottomView(context);
        bottomView.setId(AUtil.getId());


        int titleHeight = AUtil.getDeviceHeight(context)*10/100;
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleHeight*50/100);
        copyrightView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleHeight*20/100);



        LayoutParams titleView_params = new LayoutParams(LayoutParams.MATCH_PARENT,titleHeight);
        titleView_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        titleView_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleView.setLayoutParams(titleView_params);


        int topViewHeight = AUtil.getDeviceHeight(context)*30/100;
        LayoutParams topView_params = new LayoutParams(LayoutParams.MATCH_PARENT,topViewHeight);
        topView_params.addRule(RelativeLayout.BELOW,titleView.getId());
        topView_params.setMargins(margin,margin,margin,margin);
        topView.setLayoutParams(topView_params);


        int middleViewHeight = AUtil.getDeviceHeight(context)*30/100;
        LayoutParams middleView_params = new LayoutParams(LayoutParams.MATCH_PARENT,middleViewHeight);
        middleView_params.addRule(RelativeLayout.BELOW,topView.getId());
        middleView_params.setMargins(margin,margin,margin,margin);
        middleView.setLayoutParams(middleView_params);

        int bottomViewHeight = AUtil.getDeviceHeight(context)*15/100;
        LayoutParams bottomView_params = new LayoutParams(LayoutParams.MATCH_PARENT,bottomViewHeight);
        bottomView_params.addRule(RelativeLayout.BELOW,middleView.getId());
        bottomView_params.setMargins(margin,margin,margin,margin);
        bottomView.setLayoutParams(bottomView_params);



//        titleView.setBackgroundColor(Color.RED);
        topView.setBackgroundColor(Color.WHITE);
        middleView.setBackgroundColor(Color.WHITE);

        this.addView(titleView);
        this.addView(topView);
        this.addView(middleView);
        this.addView(bottomView);

    }




    ImageView photoView;

    LineView nameView;
    LineView genderView;

    LineView nationView;
    LineView birthdayView;

    LineView codeView;
    LineView verifyTimeView;

    LineView publishDepartmentView;
    LineView addressView;

    LineInputView phoneView;
    LineView tripView;
    LineInputView temperatureView;

    private View makeTopView(Context context)
    {
        nameView = new LineView(context);
        nameView.setTitle("姓名");
        genderView = new LineView(context);
        genderView.setTitle("性别");
        nationView = new LineView(context);
        nationView.setTitle("民族");
        birthdayView = new LineView(context);
        birthdayView.setTitle("生日");
        codeView = new LineView(context);
        codeView.setTitle("身份证号");
        verifyTimeView = new LineView(context);
        verifyTimeView.setTitle("有效期");
        publishDepartmentView = new LineView(context);
        publishDepartmentView.setTitle("发证机关");
        addressView = new LineView(context);
        addressView.setTitle("地址");

        Vector<LineView> lines = new Vector<LineView>();
        lines.addElement(nameView);
        lines.addElement(genderView);
        lines.addElement(nationView);
        lines.addElement(birthdayView);
        lines.addElement(codeView);
        lines.addElement(verifyTimeView);
        lines.addElement(publishDepartmentView);
        lines.addElement(addressView);



        RelativeLayout layout = new RelativeLayout(context);

        int iconWidth = AUtil.getDeviceWidth(context)*20/100;
        int iconHeight = AUtil.getDeviceHeight(context)*40/100;

        int detailWidth = AUtil.getDeviceWidth(context)*80/100;
        int detailHeight = iconHeight;


        photoView = new ImageView(context);
        photoView.setBackgroundColor(Color.WHITE);
        photoView.setId(AUtil.getId());
        photoView.setImageDrawable(context.getResources().getDrawable(R.drawable.head));
//        photoView.setScaleType(ImageView.ScaleType.FIT_XY);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(AUtil.getId());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams photoView_params = new LayoutParams(iconWidth,iconHeight);
        photoView_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        photoView_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        photoView.setLayoutParams(photoView_params);


        LayoutParams linearLayout_params = new LayoutParams(detailWidth,detailHeight);
//        linearLayout_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearLayout_params.addRule(RelativeLayout.LEFT_OF,photoView.getId());
        linearLayout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        linearLayout.setLayoutParams(linearLayout_params);

        int lineWidth = detailWidth/2;
        int lineHeight = detailHeight/4;


        for(int i=0;i<lines.size();i+=2)
        {
            LineView leftView = lines.elementAt(i);
            LineView rightView = lines.elementAt(i+1);


            LinearLayout eachLine = new LinearLayout(context);
            eachLine.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(lineWidth,lineHeight);
            eachLine.addView(leftView,layoutParams);
            eachLine.addView(rightView,layoutParams);


            layoutParams = new LinearLayout.LayoutParams(detailWidth,lineHeight);
            linearLayout.addView(eachLine,layoutParams);
        }


        layout.addView(photoView);
        layout.addView(linearLayout);
        return layout;
    }

    private View makeMiddleView(Context context)
    {
        phoneView = new LineInputView(context);
        phoneView.setTitle("联系电话");

        phoneView.messageView.setInputType(TYPE_CLASS_NUMBER);//添加识别只能输入电话号码
        phoneView.messageView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});

        tripView = new LineView(context);
        tripView.setTitle("事由");


        temperatureView = new LineInputView(context);
        temperatureView.setTitle("体温");
        temperatureView.setText("36.5℃");
        temperatureView.messageView.setInputType( TYPE_NUMBER_FLAG_DECIMAL | TYPE_CLASS_NUMBER);//添加识别只能输入电话号码
        temperatureView.messageView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});



        tripView.setOnClickListener(v->{

            phoneView.clearFocus();
            temperatureView.clearFocus();
            try
            {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                imm.showSoftInput(MainView.this,InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(getWindowToken(), 0); //强制隐藏键盘
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }




            final String[] gender = new String[]{        "财政",
                    "工程建设",
                    "公安出入境业务",
                    "公安户政业务",
                    "教育",
                    "企业变更",
                    "企业核名",
                    "企业领证",
                    "企业设立",
                    "社保业务",
                    "医保业务",
                    "食品经营许可",
                    "药品经营许可",
                    "印章刻制",
                    "税务",
                    "社会事务（文娱，教育）",
                    "社会事务（卫生，医疗）",
                    "烟草",
                    "消防"  };

            AlertDialog.Builder builder1=new  AlertDialog.Builder(getContext());
            builder1.setTitle("请选择往来事由");
            builder1.setItems(gender, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tripView.setText(gender[which]);
                }
            });
            builder1.show();
        });

        Vector<View> lines = new Vector<View>();
        lines.addElement(phoneView);
        lines.addElement(temperatureView);
        lines.addElement(tripView);

        RelativeLayout layout = new RelativeLayout(context);

        int detailWidth = AUtil.getDeviceWidth(context)*100/100;
        int detailHeight = AUtil.getDeviceHeight(context)*30/100;



        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(AUtil.getId());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams linearLayout_params = new LayoutParams(detailWidth,detailHeight);
        linearLayout_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearLayout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        linearLayout.setLayoutParams(linearLayout_params);

        int lineHeight = detailHeight/3;


        for(int i=0;i<lines.size();i++)
        {
            View lineInputView = lines.elementAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(detailWidth,lineHeight);
            linearLayout.addView(lineInputView,layoutParams);
        }


        layout.addView(linearLayout);
        return layout;
    }

    private View makeBottomView(Context context)
    {
        Button uploadBtn = new Button(context);
        uploadBtn.setText("上传信息");
        uploadBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX,40);
        uploadBtn.setOnClickListener(v -> {
            uploadData();
        });

        Button readBtn = new Button(context);
        readBtn.setText("重新读取");
        readBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX,40);

        Vector<View> lines = new Vector<View>();
        lines.addElement(uploadBtn);
        lines.addElement(readBtn);


        RelativeLayout layout = new RelativeLayout(context);

        int detailWidth = AUtil.getDeviceWidth(context)*100/100;
        int detailHeight = AUtil.getDeviceHeight(context)*15/100;



        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(AUtil.getId());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams linearLayout_params = new LayoutParams(LayoutParams.WRAP_CONTENT,detailHeight);
        linearLayout_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        linearLayout.setLayoutParams(linearLayout_params);

        int Height = detailHeight/3;

        int buttonWidth = detailWidth/4;
        int buttonHeight = detailHeight*90/100;

        for(int i=0;i<lines.size();i++)
        {
            View lineInputView = lines.elementAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonWidth,buttonHeight);
            linearLayout.addView(lineInputView,layoutParams);
        }


        layout.addView(linearLayout);
        return layout;
    }

    boolean isPosting;

    private AlertDialog alertDialog;

    public void showLoadingDialog() {
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        alertDialog.show();

        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void uploadData()
    {

        if(!isPosting)
        {
            String code = codeView.getText();
            if(Util.isNull(code))
            {
                AlertDialog.Builder builder = new  AlertDialog.Builder(getContext());
                builder.setTitle("提示");
                builder.setMessage("未获取访客信息");
                builder.show();
            }
            else
            {
                isPosting = true;
                showLoadingDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postData();
//                        test();
                        isPosting = false;
                        ((Activity)(getContext())).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                phoneView.setText("");
                                tripView.setText("");
                                temperatureView.setText("36.5℃");
                                dismissLoadingDialog();
                            }
                        });
                    }
                }).start();
            }
        }
    }


    public static void test()  {
        try
        {
            String name = "1111";
            String gender = "1111";
            String nation = "1111";
            String birthday = "1111";
            String code = "1111";
            String verify = "1111";
            String publish = "1111";
            String address = "1111";

            String phone = "1111";
            String trip = "1111";
            String temperature = "1111";

            byte datas[] = new byte[10];
            String json = "{";
            json += "\"xm\":\""+java.net.URLEncoder.encode(name,"utf-8")+"\",";
            json += "\"xb\":\""+java.net.URLEncoder.encode(gender,"utf-8")+"\",";
            json += "\"mz\":\""+java.net.URLEncoder.encode(nation,"utf-8")+"\",";
            json += "\"sr\":\""+java.net.URLEncoder.encode(birthday,"utf-8")+"\",";
            json += "\"sfz\":\""+java.net.URLEncoder.encode(code,"utf-8")+"\",";
            json += "\"yxq\":\""+java.net.URLEncoder.encode(verify,"utf-8")+"\",";
            json += "\"fzjg\":\""+java.net.URLEncoder.encode(publish,"utf-8")+"\",";
            json += "\"zz\":\""+java.net.URLEncoder.encode(address,"utf-8")+"\",";
            json += "\"photo\":\""+java.net.URLEncoder.encode(Base64.encode(datas),"utf-8")+"\",";
            json += "\"phoneno\":\""+java.net.URLEncoder.encode(phone,"utf-8")+"\",";
            json += "\"temperature\":\""+java.net.URLEncoder.encode(temperature,"utf-8")+"\",";
            json += "\"triptype\":\""+java.net.URLEncoder.encode(trip,"utf-8")+"\"";
            json += "}";
            byte[] bs = postHttpData("https://pm.aitekapp.com/api/facegate/visitor_log_callback/4",json.getBytes("utf-8"));
            Util.debug(bs+"");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongThread")
    private void postData()
    {
        String name = nameView.getText();
        String gender = genderView.getText();
        String nation = nationView.getText();
        String birthday = birthdayView.getText();
        String code = codeView.getText();
        String verify = verifyTimeView.getText();
        String publish = publishDepartmentView.getText();
        String address = addressView.getText();

        String phone = phoneView.getText();
        String trip = tripView.getText();
        String temperature = temperatureView.getText();


        temperature = Util.replaceAll(temperature,"℃","",true);
        if(Util.getFloat(temperature)==0)
        {
            temperature = "36.5";
        }

        byte[] datas = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            datas = baos.toByteArray();
            baos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(datas==null)
        {
            datas = new byte[10];
        }


        try {


            String json = "{";
            json += "\"xm\":\""+java.net.URLEncoder.encode(name,"utf-8")+"\",";
            json += "\"xb\":\""+java.net.URLEncoder.encode(gender,"utf-8")+"\",";
            json += "\"mz\":\""+java.net.URLEncoder.encode(nation,"utf-8")+"\",";
            json += "\"sr\":\""+java.net.URLEncoder.encode(birthday,"utf-8")+"\",";
            json += "\"sfz\":\""+java.net.URLEncoder.encode(code,"utf-8")+"\",";
            json += "\"yxq\":\""+java.net.URLEncoder.encode(verify,"utf-8")+"\",";
            json += "\"fzjg\":\""+java.net.URLEncoder.encode(publish,"utf-8")+"\",";
            json += "\"zz\":\""+java.net.URLEncoder.encode(address,"utf-8")+"\",";
            json += "\"photo\":\""+java.net.URLEncoder.encode(Base64.encode(datas),"utf-8")+"\",";
            json += "\"phoneno\":\""+java.net.URLEncoder.encode(phone,"utf-8")+"\",";
            json += "\"temperature\":\""+java.net.URLEncoder.encode(temperature,"utf-8")+"\",";
            json += "\"triptype\":\""+java.net.URLEncoder.encode(trip,"utf-8")+"\"";
            json += "}";

            Util.debug(json);

            byte[] bs = postHttpData("https://pm.aitekapp.com/api/facegate/visitor_log_callback/4",json.getBytes("utf-8"));
            if(bs==null)
            {
                AUtil.showTips(getContext(),"上传失败请重试");
            }
            else
            {
                AUtil.showTips(getContext(),"上传完毕");
//                AUtil.showTips(getContext(),new String(bs));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }

    private static byte[] postHttpData(String s, byte[] data) {
        try {
            int size = data.length;
            URL url = new URL(s);
            HttpURLConnection url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            url_con.setRequestProperty("content-type", "application/json;charsetset=UTF-8");
            url_con.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(url_con.getOutputStream());
            for (int i = 0; i < size; i++) {
                dos.write(data[i]);
            }
            dos.flush();
            dos.close();
            InputStream is = url_con.getInputStream();
            byte[] bs = read(is);
            is.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] read(InputStream is) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int c = 0;
            while (c != -1) {
                c = is.read();
                bos.write(c);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    Bitmap bm;
    public void setPhotoBitmap(Bitmap bm) {
        this.bm = bm;
    }
}
