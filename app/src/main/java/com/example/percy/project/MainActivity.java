package com.example.percy.project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.percy.project.R.id.send;

public class MainActivity extends Activity  {
    TextToSpeech t1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    DatabaseHelper db;
    String phoneNo, text;
    private EditText txtSpeechInput;
    private Button btnSpeak;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    //ArrayAdapter<String> adapter;
    //ArrayList<String> listItems;
    ListView listView;
    boolean user=true;
    boolean mach_l=false;
    int z=0;
    String ml[][]=new String[20][3];
    double ratingAI[]= new double[20];
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DatabaseHelper(this);
        txtSpeechInput = (EditText) findViewById(R.id.message);
        btnSpeak = (Button) findViewById(send);
        // hide the action bar
        //getActionBar().hide();
        speak("Hello. My name is Pebbles. Tap the bottom right of your screen to send a message.");
        listView = (ListView) findViewById(R.id.mobile_list);
        //txtSpeechInput.setText("");
        listItems=new ArrayList<String>();
        listItems.add("Hello. My name is Pebbles.");
        listItems.add("Tap the bottom right of your screen to send a message.");
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, listItems);
        //adapter = new MessageAdapter(this, R.layout.activity_listview, listItems);
        listView.setAdapter(adapter);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                user=false;
                if(txtSpeechInput.getText().toString().equals(""))
                    promptSpeechInput();
                else
                {
                    store(txtSpeechInput.getText().toString());
                    txtSpeechInput.setText("");
                }

            }
        });

    }
    private void speak(final String text){
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    //speak(listItems.get(listItems.size() - 1));
                    //speak(listItems.get(listItems.size() - 1));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    if(mach_l==true && user==false)
                    {
                        mach_l=false;
                        ml[z++][1]=text;
                        store("Thank you. I understood now.");
                        user=true;
                    }
                    if(!user)
                        getReply();
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }
    public  void store(String newMessage)
    {
        listItems.add(newMessage);
        adapter.notifyDataSetChanged();
        speak(newMessage);
        //getReply();
        //viewData();
    }

    public void getReply() {
        String lastMessage=listItems.get(listItems.size()-1);
        lastMessage.toLowerCase();
        int c=0;
        String what[][]={{"name","My name is Pebbles"},{"you look","i have no form"},{"mile to km","1 mile = 1.60934 Km"},{"time","Im sorry,that functionality has not yet been programmed into me"},{"doing","Chatting with you."},{"job","My job is to be an assistant for the visually challenged"},{"km to mile","1 km = 0.621371 Miles"},{"you","Im fine. Thank you"}};
        String why[][]={{"pebbles","They call me Pebbles though I prefer anything else"},{},{"exist","because I was created"},{"orange","it all depends on what ou want it to be"},{"talk","I am programmed to talk like that"},{}};
        String how[][]={{"are you","I'm fine. Thank you. "},{"doing","I dont really like the TV show Friends"},{"you look","i have no form"},{"is life","Pebbles is an AI.Can not yet answer that question"},{"eating","I cant"},{"old","I was born on 10th April"}};
        String who[][]={{"name","My name is Pebbles"},{"doing","Something productive. Unlike you."},{"thinking","Cannot think yet"},{"becoming","AI"}};
        String where[][]={{"you live","In Android, for now"},{"am i","I'm sorry,that functionality has not yet been programmed into me"}};
        String which[][]={{"languages you","English only"},{"country","India"}};
        String greeting[][]={{"hello","Hello. How are you today?"},{"hey","Lovely meeting you today"}};
        String res="";
        boolean replyFound=false;
        if(lastMessage.contains("call") || lastMessage.contains("Call")) {
            res="Enter a number";
            c=1;
        }

        if(listItems.get(listItems.size()-3).equals("call") || listItems.get(listItems.size()-3).equals("Call"))
        {
            placeCall();
            c=1;
        }

        if(lastMessage.contains("message") || lastMessage.contains("message")) {
            res="Enter a number and message.";



            c=1;
        }

        if(listItems.get(listItems.size()-4).equals("message") || listItems.get(listItems.size()-4).equals("message"))
        {
            sendSMSMessage();
            c=1;
        }

        if(lastMessage.contains("open camera") || lastMessage.contains("Open Camera")) {
            //res="Opening Camera";
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(this, ALLOW_KEY)) {
                    showSettingsAlert();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)

                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                        showAlert();
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
                c=1;
            } else {
                openCamera();
            }
            c=1;
        }



        if(lastMessage.contains("What") || lastMessage.contains("what"))
        {
            for(int i=0; i<8; i++)
            {
                if (lastMessage.contains(what[i][0]))
                {
                    res=what[i][1];
                    c=1;
                    break;
                }
            }
        }
        if(c==0 && (lastMessage.contains("Why") || lastMessage.contains("why")))
        {
            for(int i=0; i<6; i++)
            {
                if (lastMessage.contains(why[i][0]))
                {
                    res=why[i][1];
                    c=1;
                    break;
                }
            }

        }
        if(c==0 && (lastMessage.contains("How") || lastMessage.contains("how")))
        {
            for(int i=0; i<6; i++)
            {
                if (lastMessage.contains(what[i][0]))
                {
                    res=how[i][1];
                    c=1;
                    break;
                }
            }

        }
        if(c==0 && (lastMessage.contains("Where") || lastMessage.contains("where")))
        {
            for(int i=0; i<2; i++)
            {
                if (lastMessage.contains(where[i][0]))
                {
                    res=where[i][1];
                    c=1;
                    break;
                }
            }

        }
        if(c==0 && (lastMessage.contains("Which") || lastMessage.contains("which")))
        {
            for(int i=0; i<2; i++)
            {
                if (lastMessage.contains(which[i][0]))
                {
                    res=which[i][1];
                    c=1;
                    break;
                }
            }

        }
        if(c==0)
        {
            for(int i=0; i<2; i++)
            {
                if (lastMessage.contains(greeting[i][0]))
                {
                    res=greeting[i][1];
                    c=1;
                    break;
                }
            }

        }
        if(z>0 && c==0)
            {
                for(int i=0; i<z; i++)
                {
                    ml[i][0].replaceAll("\\.", "");
                    String ch[]=ml[i][0].split("\\s");
                    for(int j=0; j<ch.length; j++)
                    {
                        if(lastMessage.contains(ch[j]))
                        {
                            res=ml[i][1];
                            c=1;
                            break;
                        }
                    }
                }}
        if(c==0) {
            //String ch[]=lastMessage.split("\\s");

            if (lastMessage.contains("what")) {
                res = "I'm Sorry. I do not know " + lastMessage + ". Can you elaborate?";
            } else if (lastMessage.contains("who")) {
                res = "I'm Sorry. I do not know " + lastMessage + ". Can you elaborate?";
            } else
                res = "I'm Sorry. I cannot comprehend the question you are asking. Can you elaborate";

            ml[z][0] = lastMessage;
            mach_l = true;
        }

          //  String table="why";

        //String res=db.reply(lastMessage,table);
        listItems.add(res);
        adapter.notifyDataSetChanged();
        user=true;
        speak(res);
        //promptSpeechInput();
    }

    public double ai(String message, String dataB,int i)
    {
        double rate=0.0;
        dataB.replaceAll("\\.", "");
        String ch[]=dataB.split("\\s");
        for(int j=0; j<ch.length; j++)
        {
            if(message.contains(ch[j]))
            {
                rate+=1;
                rate*=0.5;
            }

        }
        ratingAI[i]=rate;
        return ratingAI[i];
    }

    public void viewData() {
        Cursor res=db.viewDatabase();
        if(res.getCount()==0)
            showData("Error","No data");
        else
        {
            StringBuffer buf=new StringBuffer();
            while (res.moveToNext()) {
                buf.append(res.getString(0)+"\n"+res.getString(1)+"\n");
            }
            showData("Data",buf.toString());
        }
    }

    public void showData(String title, String mes)
    {
        AlertDialog.Builder diag=new AlertDialog.Builder(this);
        diag.setCancelable(true);
        diag.setTitle(title);
        diag.setMessage(mes);
        diag.show();

    }

    protected void sendSMSMessage() {
        phoneNo = listItems.get(listItems.size()-3);
        text = listItems.get(listItems.size()-2);
        //phoneNo= "9819369019";
        //text= listItems.get(listItems.size() - 2);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, text, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean
                                showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                        this, permission);

                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(MainActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }
        /**
         * Showing google speech input dialog
         * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String toSpeak="";
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList <String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    toSpeak = result.get(0);
            }
                break;
            }

        }
        store(toSpeak);
        //Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
        ///boolean x=db.insertMessage(toSpeak);
            //if(x==true);
                //Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
        //listItems.add(toSpeak);
        //adapter.notifyDataSetChanged();
        if(listItems.get(listItems.size() - 1).contains("camera"))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(this, ALLOW_KEY)) {
                    showSettingsAlert();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)

                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                        showAlert();
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            } else {
                openCamera();
            }
        }
        if(listItems.get(listItems.size()-1).equalsIgnoreCase("send"))
        {
            sendSMSMessage();
            Toast.makeText(getApplicationContext(),
                    "Sending",
                    Toast.LENGTH_SHORT).show();
        }
        if(listItems.get(listItems.size()-1).equalsIgnoreCase("make call"))
        {
            /*ContentResolver resolver=getContentResolver();
            Cursor contacts=resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
            while(contacts.moveToNext())
            {
                String name=contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String num=contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(phoneNo.equals(num))
                {
                    listItems.add("Calling "+name);
                    adapter.notifyDataSetChanged();
                    speak(listItems.get(listItems.size() -1));
                }
            }*/
            placeCall();
        }
        //viewData();
        //speak(toSpeak);
    }
    public void placeCall()
    {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+listItems.get(listItems.size() - 1)));
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }


    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(MainActivity.this);
                    }
                });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
    }
}


/*
public class MainActivity extends AppCompatActivity {
    //String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};
    //ArrayList<String> listItems=new ArrayList<String>("Hello");
    DatabaseHelper myDb;
    ArrayAdapter<String> adapter;
    EditText message;
    Button send;
    String text;
    Button speak;
    ListView options;
    ArrayList<String> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb=new DatabaseHelper(this);
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mobileArray);
        //Spech to text
        speak = (Button) findViewById(R.id.send);
        options = (ListView) findViewById(R.id.mobile_list);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // This are the intents needed to start the Voice recognizer
                    SpeechToText sp=new SpeechToText();
                    sp.convert();
                }
        });

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
        message=(EditText)findViewById(R.id.message);
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text=message.getText().toString();
                message.setText("");
                listItems.add(text);
                adapter.notifyDataSetChanged();
            }
        });
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        ListView lv = (ListView)findViewById(R.id.mobile_list);
        lv.setAdapter(adapter);

    }
}
*/
