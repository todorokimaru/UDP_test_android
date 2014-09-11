package com.example.udptest1;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

  private ToneGenerator toneGenerator = new ToneGenerator(
  AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
  private StringBuffer cbuf = new StringBuffer(128);
  int n;
  private DatagramSocket ds;
  private DatagramPacket dp;
  private String IP_remote;
  private int port_remote;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LinearLayout linearLayout = new LinearLayout(this);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      final Button button1 = new Button(this);
      Button button2 = new Button(this);
      final TextView tv = new TextView(this);
      final TextView tv2 = new TextView(this);
      tv.setText("カウンタ");
      button1.setText("send to PC");
      button2.setText("－");

      //レイアウト
      linearLayout.addView(button1, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
      linearLayout.addView(button2, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
      linearLayout.addView(tv, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
      linearLayout.addView(tv2, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
      setContentView(linearLayout);

      IP_remote = "10.0.0.2"; //IPアドレス
      port_remote = 5100;       //ポート番号

      // Button1 がクリックされた時に呼び出されるコールバックを登録
      button1.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          n++;
          // 文字列を作る
          cbuf.append(n);
          tv.setText(cbuf.toString());  // tv には final が必要
          cbuf.delete(0, 99);
          toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);

          // UDP　送信
          new Thread(new Runnable() {
            public void run() {
              try{
                InetAddress host = InetAddress.getByName(IP_remote);
                String message = "send by Android " + n + " \n";  // 送信メッセージ
                        ds = new DatagramSocket();  //DatagramSocket 作成
                        byte[] data = message.getBytes("UTF8");
                        dp = new DatagramPacket(data, data.length, host, port_remote);  //DatagramPacket 作成
                        ds.send(dp);
                        tv2.setText("送信完了しました");
                     }
              catch(Exception e){
                        System.err.println("Exception : " + e);
                        tv2.setText("送信失敗しました");
              }
            }
          }).start();
        }
      });

      // カウンタの減少
      button2.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          n--;
          cbuf.append(n);
          tv.setText(cbuf.toString());
          cbuf.delete(0, 99);
          toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        }
      });

    n=0;        // カウント値の初期値
    try{
      InetAddress host = InetAddress.getByName(IP_remote);      // IPアドレス
      String message = "send by Android";  // 送信メッセージ
      ds = new DatagramSocket();  //DatagramSocket 作成
      byte[] data = message.getBytes();
      dp = new DatagramPacket(data, data.length, host, port_remote);  //DatagramPacket 作成
      tv2.setText("初期化が完了しました");
    }catch(Exception e){
      System.err.println("Exception : " + e);
      tv2.setText("初期化に失敗しました");
    }
  // end OnCreat()
  }
}
