package com.mymindsweeper.mymindsweeper.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mymindsweeper.mymindsweeper.R;
import com.mymindsweeper.mymindsweeper.sms.SMSText;
import com.mymindsweeper.mymindsweeper.sms.SMSUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity {

    public static final int RC_GOOGLE_SIGN_IN = 12;
    public static final String SERVER_GOOGLE_CLIENT_ID = "263280707472-o46anofpr2aoc3aslltgeaegjg3qpqlp.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignin();
            }
        });
    }

    public void POST(String server) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("run");
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("token", "what ever");

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
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            System.out.println(account.getDisplayName());
            System.out.println(account.getIdToken());
            System.out.println(account.getEmail());
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            System.out.println("signInResult:failed code=" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }

    public void googleSignin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null || account.getIdToken() == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.server_client_id))
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } else {
            System.out.println(account.getIdToken());
        }
    }

    public void getAllSMSThreads() {
        SMSUtils.initREAD_SMS(this);

        List<SMSText> inboxTexts = SMSUtils.readSMS(this, SMSUtils.INBOX);
        List<SMSText> sentTexts = SMSUtils.readSMS(this, SMSUtils.SENT);

        List<SMSText> allMsgs = new ArrayList<SMSText>();

        allMsgs.addAll(inboxTexts);
        allMsgs.addAll(sentTexts);

        //group text by their thread ids
        Map<Integer, List<SMSText>> threads = allMsgs.stream().collect(Collectors.groupingBy(SMSText::getThreadId));

        List<SMSText> exampleTextThread = threads.get(1).stream().sorted(Comparator.comparing(SMSText::getDate)).collect(Collectors.toList());
        for(SMSText text: exampleTextThread) {
            System.out.println(text.getDate() + ":" + text.getBody());
        }
    }

}
