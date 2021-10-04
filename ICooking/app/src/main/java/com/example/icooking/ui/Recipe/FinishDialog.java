package com.example.icooking.ui.Recipe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.icooking.R;

public class FinishDialog extends Dialog implements View.OnClickListener{
    private TextView tvTitle, tvMessage;
    private Button btnYes, btnNo;
    private IOnNoListener noListener;
    private IOnYesListener yesListener;
    //private String title, message, yes, no;

    public FinishDialog(@NonNull Context context) {
        super(context);
    }

    public FinishDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setNoListener(IOnNoListener noListener) {
        this.noListener = noListener;
    }

    public void setYesListener(IOnYesListener yesListener) {
        this.yesListener = yesListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_dialog);
        tvTitle = findViewById(R.id.tv_recipe_dialog_title);
        tvMessage = findViewById(R.id.tv_recipe_dialog_message);
        btnYes = findViewById(R.id.btn_recipe_dialog_yes);
        btnNo = findViewById(R.id.btn_recipe_dialog_no);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_recipe_dialog_yes:
                yesListener.onYes(this);
                dismiss();
                break;
            case R.id.btn_recipe_dialog_no:
                noListener.onNo(this);
                dismiss();
                break;
        }
    }

    public interface IOnYesListener{
        void onYes(FinishDialog dialog);
    }

    public interface IOnNoListener{
        void onNo(FinishDialog dialog);
    }

}
