package com.example.icooking.helper;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.helper.ItemTouchHelperAdaptor;

public class InventoryItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdaptor adaptorCallBack;

    public InventoryItemTouchHelperCallBack(ItemTouchHelperAdaptor adaptorCallBack){
        this.adaptorCallBack = adaptorCallBack;

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
         adaptorCallBack.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
           swipedItem(dX, viewHolder);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    private void swipedItem(float dX,RecyclerView.ViewHolder viewHolder){
        float alpha= 1-Math.abs(dX)/viewHolder.itemView.getWidth();
        System.out.println("hi there");
    }
}
