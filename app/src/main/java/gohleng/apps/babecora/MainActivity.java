package gohleng.apps.babecora;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gohleng.apps.babecora.constant.Constants;
import gohleng.apps.babecora.dialog.BluetoothDialog;

public class MainActivity extends AppCompatActivity {

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @BindView(R.id.imgLED1)
    View imgLED1;
    @BindView(R.id.imgLED2)
    View imgLED2;
    @BindView(R.id.imgLED3)
    View imgLED3;
    @BindView(R.id.imgLED4)
    View imgLED4;
    @BindView(R.id.imgLED5)
    View imgLED5;

    @BindView(R.id.imgVibrate1)
    View imgVibrate1;
    @BindView(R.id.imgVibrate2)
    View imgVibrate2;
    @BindView(R.id.imgVibrate3)
    View imgVibrate3;
    @BindView(R.id.imgVibrate4)
    View imgVibrate4;
    @BindView(R.id.imgVibrate5)
    View imgVibrate5;

    @BindView(R.id.imgSpeaker1)
    View imgSpeaker1;
    @BindView(R.id.imgSpeaker2)
    View imgSpeaker2;
    @BindView(R.id.imgSpeaker3)
    View imgSpeaker3;
    @BindView(R.id.imgSpeaker4)
    View imgSpeaker4;
    @BindView(R.id.imgSpeaker5)
    View imgSpeaker5;

    @BindView(R.id.txtPlay1)
    View txtPlay1;
    @BindView(R.id.txtPlay2)
    View txtPlay2;
    @BindView(R.id.txtPlay3)
    View txtPlay3;
    @BindView(R.id.txtPlay4)
    View txtPlay4;
    @BindView(R.id.txtPlay5)
    View txtPlay5;

    private ProgressDialog progress;
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter myBluetooth = null;

    private boolean isBtConnected;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        isBtConnected = false;
        sharedPref = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);

        BluetoothDialog dialogFragment = BluetoothDialog.newInstance("Connect to Bluetooth",
                new BluetoothDialog.Listener() {
                    @Override
                    public void onConnect() {
                        new ConnectBT().execute();
                    }
                });
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.txtPlay1)
    void txtPlay1Clicked() {
        imgLED1.setActivated(false);
        imgVibrate1.setActivated(false);
        imgSpeaker1.setActivated(false);
        txtPlay1.setVisibility(View.GONE);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("1".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    @OnClick(R.id.txtPlay2)
    void txtPlay2Clicked() {
        imgLED2.setActivated(false);
        imgVibrate2.setActivated(false);
        imgSpeaker2.setActivated(false);
        txtPlay2.setVisibility(View.GONE);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("2".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    @OnClick(R.id.txtPlay3)
    void txtPlay3Clicked() {
        imgLED3.setActivated(false);
        imgVibrate3.setActivated(false);
        imgSpeaker3.setActivated(false);
        txtPlay3.setVisibility(View.GONE);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("3".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    @OnClick(R.id.txtPlay4)
    void txtPlay4Clicked() {
        imgLED4.setActivated(false);
        imgVibrate4.setActivated(false);
        imgSpeaker4.setActivated(false);
        txtPlay4.setVisibility(View.GONE);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("4".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    @OnClick(R.id.txtPlay5)
    void txtPlay5Clicked() {
        imgLED5.setActivated(false);
        imgVibrate5.setActivated(false);
        imgSpeaker5.setActivated(false);
        txtPlay5.setVisibility(View.GONE);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("5".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(sharedPref.getString(Constants.PREF_MAC_ADDRESS, ""));
            BluetoothSocket fallbackSocket;
            try {
                if (btSocket == null || !isBtConnected) {
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                Class<?> clazz = btSocket.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                Method m;
                try {
                    m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{1};

                    fallbackSocket = (BluetoothSocket) m.invoke(btSocket.getRemoteDevice(), params);
                    fallbackSocket.connect();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            } else {
                msg("Connected.");
                isBtConnected = true;
            }

            final Handler mHandler = new Handler();
            if (isBtConnected) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream;
                        try {
                            inputStream = btSocket.getInputStream();
                            int byteCount = inputStream.available();
                            if (byteCount > 0) {
                                byte[] rawBytes = new byte[byteCount];
                                inputStream.read(rawBytes);
                                final String string = new String(rawBytes, "UTF-8");
                                msg(string);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(this, 200);
                    }
                };
                mHandler.postDelayed(runnable, 200);
            }
            progress.dismiss();
        }
    }

    private void trigger(String s) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(s.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }


    private void msg(String raw) {
        Toast.makeText(getApplicationContext(), raw, Toast.LENGTH_LONG).show();

        String[] list = raw.split(",");

        for (String s : list) {
            if (!(s.length() > 6)) {
                return;
            }
            boolean enabled = false;
            if (s.substring(0, 6).equalsIgnoreCase("TABLE1")) {
                if (s.substring(6).equalsIgnoreCase("LEVEL1")) {
                    imgLED1.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL2")) {
                    imgLED1.setActivated(true);
                    imgVibrate1.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL3")) {
                    imgLED1.setActivated(true);
                    imgVibrate1.setActivated(true);
                    imgSpeaker1.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL4")) {
                    imgLED1.setActivated(true);
                    imgVibrate1.setActivated(true);
                    imgSpeaker1.setActivated(true);
                    txtPlay1.setVisibility(View.VISIBLE);
                }
                enabled = true;
            }
            if (s.substring(0, 6).equalsIgnoreCase("TABLE2")) {
                if (s.substring(6).equalsIgnoreCase("LEVEL1")) {
                    imgLED2.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL2")) {
                    imgLED2.setActivated(true);
                    imgVibrate2.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL3")) {
                    imgLED2.setActivated(true);
                    imgVibrate2.setActivated(true);
                    imgSpeaker2.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL4")) {
                    imgLED2.setActivated(true);
                    imgVibrate2.setActivated(true);
                    imgSpeaker2.setActivated(true);
                    txtPlay2.setVisibility(View.VISIBLE);
                }
                enabled = true;
            }
            if (s.substring(0, 6).equalsIgnoreCase("TABLE3")) {
                if (s.substring(6).equalsIgnoreCase("LEVEL1")) {
                    imgLED3.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL2")) {
                    imgLED3.setActivated(true);
                    imgVibrate3.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL3")) {
                    imgLED3.setActivated(true);
                    imgVibrate3.setActivated(true);
                    imgSpeaker3.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL4")) {
                    imgLED3.setActivated(true);
                    imgVibrate3.setActivated(true);
                    imgSpeaker3.setActivated(true);
                    txtPlay3.setVisibility(View.VISIBLE);
                }
                enabled = true;
            }
            if (s.substring(0, 6).equalsIgnoreCase("TABLE4")) {
                if (s.substring(6).equalsIgnoreCase("LEVEL1")) {
                    imgLED4.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL2")) {
                    imgLED4.setActivated(true);
                    imgVibrate4.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL3")) {
                    imgLED4.setActivated(true);
                    imgVibrate4.setActivated(true);
                    imgSpeaker4.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL4")) {
                    imgLED4.setActivated(true);
                    imgVibrate4.setActivated(true);
                    imgSpeaker4.setActivated(true);
                    txtPlay4.setVisibility(View.VISIBLE);
                }
                enabled = true;
            }
            if (s.substring(0, 6).equalsIgnoreCase("TABLE5")) {
                if (s.substring(6).equalsIgnoreCase("LEVEL1")) {
                    imgLED5.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL2")) {
                    imgLED5.setActivated(true);
                    imgVibrate5.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL3")) {
                    imgLED5.setActivated(true);
                    imgVibrate5.setActivated(true);
                    imgSpeaker5.setActivated(true);
                }
                if (s.substring(6).equalsIgnoreCase("LEVEL4")) {
                    imgLED5.setActivated(true);
                    imgVibrate5.setActivated(true);
                    imgSpeaker5.setActivated(true);
                    txtPlay5.setVisibility(View.VISIBLE);
                }
                enabled = true;
            }
            if (!enabled && s.contains("LEVEL")) {
                int index = s.indexOf("LEVEL");
                if (s.substring(index - 1, index).equalsIgnoreCase("1")) {
                    if (s.substring(index).equalsIgnoreCase("LEVEL1")) {
                        imgLED1.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL2")) {
                        imgLED1.setActivated(true);
                        imgVibrate1.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL3")) {
                        imgLED1.setActivated(true);
                        imgVibrate1.setActivated(true);
                        imgSpeaker1.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL4")) {
                        imgLED1.setActivated(true);
                        imgVibrate1.setActivated(true);
                        imgSpeaker1.setActivated(true);
                        txtPlay1.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (s.substring(index - 1, index).equalsIgnoreCase("2")) {
                    if (s.substring(index).equalsIgnoreCase("LEVEL1")) {
                        imgLED2.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL2")) {
                        imgLED2.setActivated(true);
                        imgVibrate2.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL3")) {
                        imgLED2.setActivated(true);
                        imgVibrate2.setActivated(true);
                        imgSpeaker2.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL4")) {
                        imgLED2.setActivated(true);
                        imgVibrate2.setActivated(true);
                        imgSpeaker2.setActivated(true);
                        txtPlay2.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (s.substring(index - 1, index).equalsIgnoreCase("3")) {
                    if (s.substring(index).equalsIgnoreCase("LEVEL1")) {
                        imgLED3.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL2")) {
                        imgLED3.setActivated(true);
                        imgVibrate3.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL3")) {
                        imgLED3.setActivated(true);
                        imgVibrate3.setActivated(true);
                        imgSpeaker3.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL4")) {
                        imgLED3.setActivated(true);
                        imgVibrate3.setActivated(true);
                        imgSpeaker3.setActivated(true);
                        txtPlay3.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (s.substring(index - 1, index).equalsIgnoreCase("4")) {
                    if (s.substring(index).equalsIgnoreCase("LEVEL1")) {
                        imgLED4.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL2")) {
                        imgLED4.setActivated(true);
                        imgVibrate4.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL3")) {
                        imgLED4.setActivated(true);
                        imgVibrate4.setActivated(true);
                        imgSpeaker4.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL4")) {
                        imgLED4.setActivated(true);
                        imgVibrate4.setActivated(true);
                        imgSpeaker4.setActivated(true);
                        txtPlay4.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (s.substring(index - 1, index).equalsIgnoreCase("5")) {
                    if (s.substring(index).equalsIgnoreCase("LEVEL1")) {
                        imgLED5.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL2")) {
                        imgLED5.setActivated(true);
                        imgVibrate5.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL3")) {
                        imgLED5.setActivated(true);
                        imgVibrate5.setActivated(true);
                        imgSpeaker5.setActivated(true);
                    }
                    if (s.substring(index).equalsIgnoreCase("LEVEL4")) {
                        imgLED5.setActivated(true);
                        imgVibrate5.setActivated(true);
                        imgSpeaker5.setActivated(true);
                        txtPlay5.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
