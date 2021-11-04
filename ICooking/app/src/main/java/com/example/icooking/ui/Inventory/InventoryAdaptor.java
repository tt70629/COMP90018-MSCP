package com.example.icooking.ui.Inventory;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.MainActivity;
import com.example.icooking.R;
import com.example.icooking.utility.NotificationReceiver;


import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;

public class InventoryAdaptor extends RecyclerView.Adapter<InventoryAdaptor.ViewHolder>  {
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    OnItemClickHandler mOnItemClickListener;
    Context context;
    final String EXPIRED="EXPIRED";
    final String WARNING_NOTIFICATION="Let's cook";
    static final int WARNING_DAYS=3;
    final int HOUR=14;


    public InventoryAdaptor(OnItemClickHandler onItemClickHandler, Context context) {
        this.mOnItemClickListener=onItemClickHandler;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list_item,parent,false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String expiryDate=inventoryList.get(position).getExpiryDate();
        String DayLeft = getDayLeft(expiryDate);
        holder.ingredientName.setText(inventoryList.get(position).getIngredientName());
        holder.leftDay.setText(DayLeft);
        String ingredientName = (String) holder.ingredientName.getText();
        Calendar calendar_expired=setCalendar(expiryDate,HOUR);
        Calendar calendar_warning=setCalendar(expiryDate,HOUR);
        calendar_warning.add(Calendar.DAY_OF_YEAR,-3);
        Calendar calendar_now=Calendar.getInstance();
        if(Integer.parseInt(DayLeft)<=WARNING_DAYS){
            holder.leftDay.setTextColor(Color.RED);
            if(Integer.parseInt(DayLeft)<=0){
                holder.leftDay.setText(EXPIRED);
            }
        }else{
            holder.leftDay.setTextColor(Color.BLACK);
        }

        if(calendar_expired.after(calendar_now)){
            scheduleNotification(getNotification(
                    "The ingredient "+ingredientName+" has been expired!",calendar_expired),
                    calendar_expired,
                    position);
            Log.d("Schedule_notify","notification_ex for "+ingredientName+" will alarm at "+calendar_expired.getTime());
            if(calendar_warning.after(calendar_now)){
                scheduleNotification(getNotification(
                        "The ingredient "+ingredientName+" will be expired in " +WARNING_DAYS+" days!",calendar_warning),
                        calendar_warning,
                        position);
                Log.d("Schedule_notify","notification_war for "+ingredientName+" will alarm at "+calendar_warning.getTime());
            }

        }


    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView ingredientName;
        private TextView leftDay;
        private OnItemClickHandler mOnItemClickHandler;

        public ViewHolder(@NonNull View itemView, OnItemClickHandler mOnItemClickListener) {
            super(itemView);
            ingredientName=itemView.findViewById(R.id.ingredientName);
            leftDay=itemView.findViewById(R.id.leftDay);
            this.mOnItemClickHandler=mOnItemClickListener;

        }

    }

    /*
     * This interface is used when data is processed in activity.
     * Not in used for now.
     */

    interface OnItemClickHandler {
        void show (Inventory inventory);
    }

    /*
     * These getters and setter are for activities or fragments to
     * operate with inventory list from DB
     */

    public String getKey(int position){
        return inventoryList.get(position).getKey();
    }
    public Inventory getInventory(int position){
        return inventoryList.get(position);
    }
    public boolean doesInventoryNameExist(String name){
        boolean exist=false;
        for (int i=0;i<inventoryList.size();i++){
            Inventory inv=inventoryList.get(i);
            //also remove all whitespaces 
            if (name.toLowerCase().replaceAll("\\s+","").equals(inv.getIngredientName())){
                exist=true;
            }
        }
        return exist;
    }
    public void setInventory(ArrayList<Inventory> inventoryList){
        this.inventoryList=inventoryList;
        notifyDataSetChanged();
    }
    public static String getDayLeft(String expiryDate) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 24)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            LocalDateTime _expiryDate = LocalDateTime.parse(expiryDate, formatter);
            LocalDateTime todayDate = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(todayDate,_expiryDate);
            String daysBetween_str = Long.toString(daysBetween);
            return daysBetween_str;
        } catch (Exception e) {
            return e.toString();
        }

    }


    private void scheduleNotification(Notification notification, Calendar calendar, int key) {
        Intent notificationIntent = new Intent(this.context, NotificationReceiver.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, key);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, key, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    private Notification getNotification(String content, Calendar calendar) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, MainActivity.CHANNEL_ID);
        builder.setContentTitle(WARNING_NOTIFICATION);
        builder.setContentText(content);
        builder.setWhen(calendar.getTimeInMillis());
        builder.setShowWhen(true);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        return builder.build();
    }

    private Calendar setCalendar(String expiryDate, int hour){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY,0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDateTime _expiryDate = LocalDateTime.parse(expiryDate, formatter);
        int day=_expiryDate.getDayOfMonth();
        int year=_expiryDate.getYear();
        int month =_expiryDate.getMonthValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DATE,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar;
    };


}
