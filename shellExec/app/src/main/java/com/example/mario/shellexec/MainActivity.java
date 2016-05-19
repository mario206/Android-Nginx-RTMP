package com.example.mario.shellexec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String cmd = "ps";
        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
        String strResult = result.toString();

        Log.d("onCreate:", strResult);
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d("onCreate:", "xxx");
//                        String cmd = "ps";
//                        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
//                        String successMsg = result.successMsg;
//
//                        Log.d("successMsg", result.successMsg);
//                        Log.d("errorMsg", result.errorMsg);
                        ShellUtils.killProcessByName("nginx");
                    }
                });

        Button btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cmd = "/data/misc/nginx/sbin/nginx -p  /data/misc/nginx  -c  /data/misc/nginx/conf/nginx.conf";
                        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
                    }
                });

        EditText text01 = (EditText) findViewById(R.id.editText);
        text01.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                writeConf(str);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
    public void writeConf(String input)
    {
        String str1 = "user  root;\n" +
                "worker_processes  1;\n" +
                "\n" +
                "events {\n" +
                "    worker_connections  1024;\n" +
                "}\n" +
                "http {\n" +
                "    include       mime.types;\n" +
                "    default_type  application/octet-stream;\n" +
                "    sendfile        on;\n" +
                "    keepalive_timeout  65;\n" +
                "    server {\n" +
                "        listen       80;\n" +
                "        server_name  localhost;\n" +
                "\n" +
                "        location / {\n" +
                "            root   html;\n" +
                "            index  index.html;\n" +
                "        }\n" +
                "        error_page   500 502 503 504  /50x.html;\n" +
                "        location = /50x.html {\n" +
                "            root   html;\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "rtmp {\n" +
                "    server {\n" +
                "        listen 1935;\n" +
                "        application live1 {\n" +
                "            live on;\n";

        String str2 = "        }\n" +
                "    }\n" +
                "}\n";

        String str = str1 + input + "\n" + str2;

        this.writeFile("/data/misc/nginx/conf/nginx.conf", str);

    }

    public void writeFile(String fileName,String writestr) {
        try{

            File file  = new File(fileName);

            FileOutputStream fout = new FileOutputStream(file);

            byte [] bytes = writestr.getBytes();

            fout.write(bytes);

            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }
}
