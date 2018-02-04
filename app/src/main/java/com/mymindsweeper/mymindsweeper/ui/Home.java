package com.mymindsweeper.mymindsweeper.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mymindsweeper.mymindsweeper.R;
import com.mymindsweeper.mymindsweeper.sms.SMSOutgoingDetector;
import com.mymindsweeper.mymindsweeper.sms.SMSText;
import com.mymindsweeper.mymindsweeper.sms.SMSUtils;
import com.mymindsweeper.mymindsweeper.utils.ScreenStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity {

    public static final int RC_GOOGLE_SIGN_UP = 1;
    public static final int RC_GOOGLE_UPLOAD_SMS = 2;
    public static final String SERVER_HOST = "http://54.163.167.120:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignin(RC_GOOGLE_SIGN_UP);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenStatus screenStatus = new ScreenStatus();
        registerReceiver(screenStatus, filter);

        SMSOutgoingDetector smsod = new SMSOutgoingDetector(this);
        smsod.start();
    }

    public void uploadSMS(String token) {
        Map<Integer, List<SMSText>> threads = getAllSMSThreads();
        for(Integer threadId: threads.keySet()) {
            JSONObject req = new JSONObject();
            try {
                req.put("token", token);
            } catch (JSONException e) {

            }
            List<SMSText> thread = threads.get(threadId).stream().sorted(Comparator.comparing(SMSText::getDate)).collect(Collectors.toList());
            //get 2nd number because android may reformat the phone number after the first message
            String phoneNumber;
            if(thread.size() > 1)
                phoneNumber = thread.get(1).getPhoneNumber();
            else
                phoneNumber = thread.get(0).getPhoneNumber();
            JSONArray smsList = new JSONArray();
            for(SMSText text: thread) {
                JSONObject textJSON = new JSONObject();
                try {
                    textJSON.put("body", text.getBody());
                    textJSON.put("date", text.getDate());
                    if(text.getType() == SMSText.SMSType.INBOX)
                        textJSON.put("user_speaking", false);
                    else if(text.getType() == SMSText.SMSType.SENT)
                        textJSON.put("user_speaking", true);
                    smsList.put(textJSON);
                } catch (JSONException e) {

                }
            }

            try {
                req.put("thread_id", threadId.intValue());
                req.put("person", phoneNumber);
                req.put("sms_list", smsList);
            } catch (JSONException e) {
                System.out.println(e);
            }
            POST(SERVER_HOST + "/upload-sms", req);
        }
    }

    public void POST(String server, JSONObject jsonParam) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        System.out.println(inputLine);

                    in.close();
                    conn.disconnect();
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        // The Task returned from this call is always completed, no need to attach
        // a listener.
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        handleSignInResult(task, requestCode);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask, int requestCode) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (requestCode == RC_GOOGLE_SIGN_UP) {
                JSONObject json = new JSONObject();
                try {
                    json.put("token", account.getIdToken());
                } catch (JSONException e) {
                    System.out.println(e);
                }
                POST(SERVER_HOST + "/create-account", json);
            } else if(requestCode == RC_GOOGLE_UPLOAD_SMS) {
                uploadSMS(account.getIdToken());
            }
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            System.out.println("signInResult:failed code=" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }

    public void googleSignin(int action) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, action);
    }

    public Map<Integer, List<SMSText>> getAllSMSThreads() {
        SMSUtils.initREAD_SMS(this);

        List<SMSText> inboxTexts = SMSUtils.readSMS(this, SMSUtils.INBOX);
        List<SMSText> sentTexts = SMSUtils.readSMS(this, SMSUtils.SENT);

        List<SMSText> allMsgs = new ArrayList<SMSText>();

        allMsgs.addAll(inboxTexts);
        allMsgs.addAll(sentTexts);

        //group text by their thread ids
        Map<Integer, List<SMSText>> threads = allMsgs.stream().collect(Collectors.groupingBy(SMSText::getThreadId));
        return threads;
    }

}
