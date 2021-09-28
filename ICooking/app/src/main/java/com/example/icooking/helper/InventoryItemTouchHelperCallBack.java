package com.example.icooking.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.R;
import com.example.icooking.helper.ItemTouchHelperAdaptor;

public class InventoryItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdaptor adaptorCallBack;
    private final Context context;

    public InventoryItemTouchHelperCallBack(ItemTouchHelperAdaptor adaptorCallBack, Context context){
        this.adaptorCallBack = adaptorCallBack;
        this.context=context;

    }

       @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlag= ItemTouchHelper.DOWN | ItemTouchHelper.UP;
        final int swipeFlag= ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlag,swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adaptorCallBack.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //if (direction==ItemTouchHelper.LEFT){
            //adaptorCallBack.onItemDismiss(viewHolder.getAdapterPosition());
        }

    }

//    @Override
//    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        try {
//            Bitmap icon;
//            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                View itemView = viewHolder.itemView;
//                Paint p = new Paint ();
//                float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                float width = height / 5;
//                viewHolder.itemView.setTranslationX(dX / 5);
//
//                p.setColor(Color.parseColor("#D32F2F"));
//                RectF background = new RectF(
//                        (float) (itemView.getRight() + dX / 5),
//                        (float) itemView.getTop(),
//                        (float) itemView.getRight(),
//                        (float) itemView.getBottom());
//                c.drawRect(background, p);
//                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
//
//                RectF icon_dest = new RectF(
//                        (float) (itemView.getRight() + dX/5+ 60),
//                        (float) itemView.getTop()+width,
//                        (float) (itemView.getRight() + dX/5+ 60)+100,
//                        (float) itemView.getBottom()-width);
//
//                c.drawBitmap(icon, null, icon_dest, p);
//
//            } else {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }




