package com.easy.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.easy.aidl.IBaseAidlInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    IBaseAidlInterface iBaseAidlInterface;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBaseAidlInterface = IBaseAidlInterface.Stub.asInterface(service);
            if (iBaseAidlInterface != null) {
                try {
                    Log.d("RemoteValue", "int =" + iBaseAidlInterface.getInt());
                    Log.d("RemoteValue", "long =" + iBaseAidlInterface.getLong());
                    Log.d("RemoteValue", "boolean =" + iBaseAidlInterface.getBoolean());
                    Log.d("RemoteValue", "float =" + iBaseAidlInterface.getFloat());
                    Log.d("RemoteValue", "double =" + iBaseAidlInterface.getDouble());
                    Log.d("RemoteValue", "string =" + iBaseAidlInterface.getString());
                } catch (Exception e) {
                    Log.d("RemoteValue", e.getMessage());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBind(View view) {
        Intent intent = new Intent();
        intent.setAction("RemoteAIDL");//要与服务端service下声明的action名称一致
        intent.setPackage("com.easy"); //此处设置应用A的包名
        Intent newIntent = buildExplicitIntent(MainActivity.this, intent);
        boolean isbind = getApplicationContext().bindService(newIntent, connection, Context.BIND_AUTO_CREATE);
        Log.d("RemoteValue", "isbind= " + isbind);
    }

    public void onUnBind(View view) {
        unbindService(connection);
    }

    public Intent buildExplicitIntent(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);
        if (resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(intent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
