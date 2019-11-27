package gohleng.apps.babecora.dialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import gohleng.apps.babecora.R;
import gohleng.apps.babecora.constant.Constants;

import static android.content.Context.MODE_PRIVATE;

public class BluetoothDialog extends DialogFragment {

    public static BluetoothDialog newInstance(String title, Listener listener) {
        BluetoothDialog frag = new BluetoothDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.listener = listener;
        return frag;
    }

    @BindView(R.id.txtEmpty)
    View txtEmpty;
    @BindView(R.id.listBtDevice)
    ListView listBtDevice;

    private BluetoothAdapter myBluetooth = null;
    private Listener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bluetooth, container, false);
        ButterKnife.bind(this, v);
        initializeBluetooth();

        return v;
    }

    private void initializeBluetooth() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            Toast.makeText(getContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else {
            if (myBluetooth.isEnabled()) {
                Toast.makeText(getContext(), "Bluetooth Device Available", Toast.LENGTH_LONG).show();
            } else {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }

        pairedDevicesList(getContext());
    }

    private void pairedDevicesList(final Context context) {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        List<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        txtEmpty.setVisibility(pairedDevices.size() == 0 ? View.VISIBLE : View.GONE);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        listBtDevice.setAdapter(adapter);
        listBtDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_LONG).show();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(Constants.PREF_MAC_ADDRESS, address);
                editor.apply();

                listener.onConnect();

                dismissAllowingStateLoss();
            }
        });
    }

    public interface Listener {
        void onConnect();
    }
}
