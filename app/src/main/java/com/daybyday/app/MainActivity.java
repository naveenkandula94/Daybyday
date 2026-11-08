package com.daybyday.app;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
    static final String PREF="daybyday_local", DATA="challenges";
    static final int GREEN=1, RED=2, GREY=0;
    ArrayList<JSONObject> tasks = new ArrayList<>();
    LinearLayout root, list;
    boolean dark=false;
    int bg, card, fg, muted;
    @Override public void onCreate(Bundle b) { super.onCreate(b); dark=getPreferences(0).getBoolean("dark", false); load(); render(); requestNotifyPermission(); }
    void colors() { bg=Color.parseColor(dark?"#11121A":"#F6F5FA"); card=Color.parseColor(dark?"#1D1F2B":"#FFFFFF"); fg=Color.parseColor(dark?"#F5F5FA":"#20212A"); muted=Color.parseColor(dark?"#A8A9B8":"#777987"); getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg); if(Build.VERSION.SDK_INT>=23)getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); }
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    TextView text(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void pad(View v,int a){v.setPadding(dp(a),dp(a),dp(a),dp(a));}
    void load(){tasks.clear();try{JSONArray a=new JSONArray(getPreferences(0).getString(DATA,"[]"));for(int i=0;i<a.length();i++)tasks.add(a.getJSONObject(i));}catch(Exception ignored){}}
    void save(){JSONArray a=new JSONArray();for(JSONObject o:tasks)a.put(o);getPreferences(0).edit().putString(DATA,a.toString()).apply();}
    void render(){
        colors(); root=column();root.setBackgroundColor(bg); ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);LinearLayout page=column();page.setPadding(dp(20),dp(22),dp(20),dp(24));
        LinearLayout top=row();TextView brand=text("DAY BY DAY",13,true);brand.setLetterSpacing(.12f);top.addView(brand,new LinearLayout.LayoutParams(0,-2,1));Button settings=smallButton("⚙ Settings");top.addView(settings);settings.setOnClickListener(v->settingsDialog());page.addView(top);
        TextView heading=text("Build better habits.",29,true);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=dp(22);page.addView(heading,hp);
        TextView sub=text("Small actions. Consistent progress.",14,false);sub.setTextColor(muted);page.addView(sub);
        int done=0,miss=0,active=0;for(JSONObject o:tasks){try{if(!o.optBoolean("archived",false))active++;JSONArray st=o.getJSONArray("states");for(int i=0;i<st.length();i++){if(st.optInt(i)==GREEN)done++;if(st.optInt(i)==RED)miss++;}}catch(Exception ignored){}}
        LinearLayout stats=row();stats.setPadding(0,dp(20),0,dp(18));stats.addView(statCard(""+active,"Active"),new LinearLayout.LayoutParams(0,dp(82),1));stats.addView(space(10),new LinearLayout.LayoutParams(dp(10),1));stats.addView(statCard(""+done,"Completed"),new LinearLayout.LayoutParams(0,dp(82),1));stats.addView(space(10),new LinearLayout.LayoutParams(dp(10),1));stats.addView(statCard(""+miss,"Missed"),new LinearLayout.LayoutParams(0,dp(82),1));page.addView(stats);
        LinearLayout titleRow=row();titleRow.addView(text("My challenges",19,true),new LinearLayout.LayoutParams(0,-2,1));Button add=smallButton("+ Add task");titleRow.addView(add);add.setOnClickListener(v->addDialog());page.addView(titleRow);
        list=column();LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(12);page.addView(list,lp);
        if(tasks.isEmpty()){LinearLayout empty=column();empty.setGravity(Gravity.CENTER);empty.setPadding(dp(18),dp(35),dp(18),dp(35));empty.setBackground(shape(card,18));TextView e=text("No challenges yet",18,true);empty.addView(e);TextView e2=text("Add your first task and start a challenge.",13,false);e2.setTextColor(muted);empty.addView(e2);list.addView(empty);}
        else for(JSONObject o:tasks)if(!o.optBoolean("archived",false))list.addView(taskCard(o));
        TextView foot=text("Your data stays on this device.",12,false);foot.setTextColor(muted);foot.setGravity(Gravity.CENTER);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.topMargin=dp(24);page.addView(foot,fp);
        scroll.addView(page);root.addView(scroll);setContentView(root);
    }
    View space(int h){View v=new View(this);return v;}
    View statCard(String n,String label){LinearLayout c=column();c.setPadding(dp(12),dp(12),dp(8),dp(10));c.setBackground(shape(card,15));TextView a=text(n,23,true),b=text(label,11,false);b.setTextColor(muted);c.addView(a);c.addView(b);return c;}
    Button smallButton(String s){Button b=new Button(this);b.setText(s);b.setTextSize(12);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setPadding(dp(10),0,dp(10),0);b.setBackground(shape(Color.parseColor("#6D5CE8"),12));return b;}
    View taskCard(JSONObject o){
        LinearLayout c=column();c.setPadding(dp(15),dp(14),dp(15),dp(14));c.setBackground(shape(card,18));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(12);c.setLayoutParams(cp);
        String name=o.optString("name","Untitled");int days=o.optInt("days",20);JSONArray states=o.optJSONArray("states");if(states==null)states=new JSONArray();
        int done=0,miss=0;for(int i=0;i<states.length();i++){if(states.optInt(i)==GREEN)done++;if(states.optInt(i)==RED)miss++;}
        LinearLayout r=row();r.addView(text(name,17,true),new LinearLayout.LayoutParams(0,-2,1));TextView pct=text((done*100/days)+"%",16,true);pct.setTextColor(Color.parseColor("#269B68"));r.addView(pct);c.addView(r);
        TextView meta=text(days+"-day challenge  •  "+done+" done  •  "+miss+" missed",12,false);meta.setTextColor(muted);LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,-2);mp.topMargin=dp(5);c.addView(meta,mp);
        LinearLayout grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);grid.setPadding(0,dp(13),0,dp(8));
        for(int start=0;start<days;start+=7){LinearLayout line=row();for(int j=start;j<Math.min(start+7,days);j++){final int idx=j;int state=states.optInt(j,GREY);TextView cell=text(""+(j+1),12,true);cell.setGravity(Gravity.CENTER);cell.setTextColor(state==GREY?muted:Color.WHITE);cell.setBackground(shape(state==GREEN?Color.parseColor("#16834B"):state==RED?Color.parseColor("#C83E46"):Color.parseColor(dark?"#343644":"#E9E9EF"),9));LinearLayout.LayoutParams dp=new LinearLayout.LayoutParams(0,dp(38),1);dp.setMargins(dp(2),dp(2),dp(2),dp(2));line.addView(cell,dp);cell.setOnClickListener(v->{cycleDay(o,idx);render();});}grid.addView(line);}
        c.addView(grid);
        LinearLayout actions=row();TextView progress=text("Progress  "+(done*100/days)+"%  ·  "+(days-done-miss)+" unmarked",12,false);progress.setTextColor(muted);actions.addView(progress,new LinearLayout.LayoutParams(0,-2,1));
        Button more=smallButton("•••");more.setMinWidth(dp(42));actions.addView(more);more.setOnClickListener(v->taskMenu(o));c.addView(actions);return c;
    }
    void cycleDay(JSONObject o,int idx){try{JSONArray a=o.getJSONArray("states");int old=a.optInt(idx,GREY);a.put(idx,old==GREY?GREEN:old==GREEN?RED:GREY);save();}catch(Exception ignored){}}
    void addDialog(){
        LinearLayout box=column();EditText name=new EditText(this);name.setSingleLine(true);name.setHint("Task name (e.g. Exercise)");box.addView(name);
        EditText days=new EditText(this);days.setInputType(2);days.setText("20");days.setHint("Challenge duration in days");box.addView(days);
        new AlertDialog.Builder(this).setTitle("Create challenge").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Create",(d,w)->{String n=name.getText().toString().trim();int count=20;try{count=Integer.parseInt(days.getText().toString());}catch(Exception ignored){}if(n.isEmpty()||count<1||count>365){toast("Enter a name and duration from 1 to 365 days.");return;}try{JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("name",n);o.put("days",count);o.put("created",System.currentTimeMillis());JSONArray a=new JSONArray();for(int i=0;i<count;i++)a.put(GREY);o.put("states",a);o.put("archived",false);tasks.add(0,o);save();render();}catch(Exception ignored){}}).show();
    }
    void taskMenu(JSONObject o){String[] opts={"Edit task","Archive challenge","Delete challenge"};new AlertDialog.Builder(this).setTitle(o.optString("name")).setItems(opts,(d,w)->{if(w==0)editTask(o);if(w==1){o.remove("archived");try{o.put("archived",true);}catch(Exception ignored){}save();render();}if(w==2)new AlertDialog.Builder(this).setMessage("Delete this challenge and its history?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(x,y)->{tasks.remove(o);save();render();}).show();}).show();}
    void editTask(JSONObject o){EditText n=new EditText(this);n.setText(o.optString("name"));new AlertDialog.Builder(this).setTitle("Edit task name").setView(n).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{try{o.put("name",n.getText().toString().trim());save();render();}catch(Exception ignored){}}).show();}
    void settingsDialog(){
        LinearLayout box=column();Switch theme=new Switch(this);theme.setText("Dark mode");theme.setChecked(dark);box.addView(theme);Switch remind=new Switch(this);remind.setText("Daily reminders");remind.setChecked(getPreferences(0).getBoolean("reminder",false));box.addView(remind);
        TextView time=text("Reminder time: "+getPreferences(0).getString("time","09:00"),15,false);time.setPadding(0,dp(12),0,dp(8));box.addView(time);time.setOnClickListener(v->{Calendar c=Calendar.getInstance();String[] p=getPreferences(0).getString("time","09:00").split(":");c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(p[0]));c.set(Calendar.MINUTE,Integer.parseInt(p[1]));new TimePickerDialog(this,(view,h,m)->{String t=String.format(Locale.US,"%02d:%02d",h,m);getPreferences(0).edit().putString("time",t).apply();time.setText("Reminder time: "+t);if(remind.isChecked())scheduleReminder(this,h,m);},c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();});
        new AlertDialog.Builder(this).setTitle("Settings").setView(box).setNegativeButton("Close",null).setPositiveButton("Save",(d,w)->{dark=theme.isChecked();getPreferences(0).edit().putBoolean("dark",dark).putBoolean("reminder",remind.isChecked()).apply();if(remind.isChecked()){String[] p=getPreferences(0).getString("time","09:00").split(":");scheduleReminder(this,Integer.parseInt(p[0]),Integer.parseInt(p[1]));}else cancelReminder(this);render();}).show();
    }
    static void scheduleReminder(Context c,int hour,int minute){AlarmManager am=(AlarmManager)c.getSystemService(ALARM_SERVICE);Intent i=new Intent(c,ReminderReceiver.class);PendingIntent pi=PendingIntent.getBroadcast(c,55,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Calendar cal=Calendar.getInstance();cal.set(Calendar.HOUR_OF_DAY,hour);cal.set(Calendar.MINUTE,minute);cal.set(Calendar.SECOND,0);if(cal.getTimeInMillis()<=System.currentTimeMillis())cal.add(Calendar.DAY_OF_YEAR,1);if(am!=null)am.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);}
    static void cancelReminder(Context c){AlarmManager am=(AlarmManager)c.getSystemService(ALARM_SERVICE);PendingIntent pi=PendingIntent.getBroadcast(c,55,new Intent(c,ReminderReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);if(am!=null)am.cancel(pi);}
    static void scheduleReminderFromSavedSettings(Context c){
        android.content.SharedPreferences p=c.getSharedPreferences("MainActivity_preferences",0);
        if(p.getBoolean("reminder",false)){
            String[] t=p.getString("time","09:00").split(":");
            try { scheduleReminder(c,Integer.parseInt(t[0]),Integer.parseInt(t[1])); } catch(Exception ignored) {}
        }
    }
    void requestNotifyPermission(){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},9);}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
