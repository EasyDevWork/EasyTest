package com.easy.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.easy.aidl.Book;
import com.easy.aidl.BookController;
import com.easy.aidl.IAdilListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookController bookController;

    private boolean connected;
    int i;
    private List<Book> bookList;
    TextView tvScreen;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookController = BookController.Stub.asInterface(service);
            connected = true;
            try {
                //添加回调监听
                bookController.registerListener(iAdilListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d("TestAidlClient", "client_Connected name=" + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
            bookController = null;
            Log.d("TestAidlClient", "client_Disconnected name=" + name);
        }
    };
    private IAdilListener iAdilListener = new IAdilListener.Stub() {

        @Override
        public void onOperationCompleted(final Book result) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvScreen.setText("two add book=" + result.toString());
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvScreen = findViewById(R.id.tvScreen);
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            unbindService(serviceConnection);
            try {
                bookController.unregisterListener(iAdilListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.easy");//服务端包名
        intent.setAction("com.easy.aidl.action");//服务端声明的action
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void getBook(View view) {
        if (connected) {
            try {
                bookList = bookController.getBookList();
                Log.d("TestAidlClient", bookList.toString());
                tvScreen.setText(bookList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("TestAidlClient", "连接失败");
        }
    }

    public void addBookIn(View view) {
        if (connected) {
            i++;
            Book book = new Book("ClientBook_in_" + i);
            try {
                bookController.addBookIn(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void addBookOut(View view) {
        if (connected) {
            i++;
            Book book = new Book("ClientBook_out_" + i);
            try {
                bookController.addBookOut(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTwoBook(View view) {
        if (connected) {
            i++;
            Book book2 = new Book("Client_add_" + i);
            Book book1 = new Book("Client_add_" + i);
            try {
                bookController.addTwoBook(book1, book2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void addBook(View view) {
        if (connected) {
            i++;
            Book book = new Book("ClientBook_" + i);
            try {
                bookController.addBookInOut(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("TestAidlClient", "连接失败");
        }
    }
}
