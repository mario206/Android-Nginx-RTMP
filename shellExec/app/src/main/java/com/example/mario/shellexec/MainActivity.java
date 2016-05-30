package com.example.mario.shellexec;

import android.content.res.AssetManager;
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
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String cmd = "ps";
        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
        String strResult = result.toString();

        Log.d("onCreate:", strResult);

        /// kill
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

        /// run
        Button btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cmd = "/data/data/com.example.mario.shellexec/nginx/sbin/nginx -p /data/data/com.example.mario.shellexec/nginx -c /data/data/com.example.mario.shellexec/nginx/conf/nginx.conf";
                        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
                    }
                });
        /// textEdit
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

        /// install
        Button btn3 = (Button)findViewById(R.id.button3);
        btn3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //String prefix = "/data/data/" + this.getPackageName() + "nginx"; /// /data/data/com.example.mario.shellexec/nginx
                        //CopyAssets();
                        copyFileOrDir("nginx");
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/html", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/client_body_temp", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/fastcgi_temzp", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/proxy_temp", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/scgi_temp", true, true);
                        ShellUtils.execCommand("chmod -R 755 /data/data/com.example.mario.shellexec/nginx/uwsgi_temp", true, true);
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

        this.writeFile("/data/data/com.example.mario.shellexec/nginx/conf/nginx.conf", str);

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

    public void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }
        for (int i = 0; i < files.length; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {

                in = assetManager.open(files[i]);
                out = new FileOutputStream("/data/data/com.example.mario.shellexec/"
                        + files[i]);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                //Log.e(TAG, "Assets error", e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = "/data/data/" + this.getPackageName() + "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdir();
                for (int i = 0; i < assets.length; ++i) {
                    copyFileOrDir(path + "/" + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = "/data/data/" + this.getPackageName() + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

}
