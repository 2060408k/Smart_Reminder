package highway62.reminderapp.communications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;

import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.reminders.AudioLogReminder;

/**
 * Created by Highway62 on 15/09/2016.
 */
public class ComsHandler {

    private GoogleApiClient googleClient;

    public ComsHandler(){}

    public void sendReminderLogsToPhone(Context context, final ArrayList<AudioLogReminder> reminderLogs){
        Log.e("MSGCOM", "sendLogRequestToPhone in Coms handler connecting to googleClient");
        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new Thread(new SendLogsToPhone(reminderLogs)).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("MSGCOM", "ConnectionCallback onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("MSGGCOM", "ConnectionCallback onConnectionFailed: failed to conect to googleClient in sendReminderLogsToPhone");
                    }
                })
                .build();

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if( ConnectionResult.SUCCESS == result ){
            Log.e("MSGCOM", "GooglePlayServices available on wear");
        }
        else {
            Log.e("MSGCOM", "GooglePlayServices not available on wear");
        }

        googleClient.connect();
    }

    private class SendLogsToPhone implements Runnable{

        String path;
        ArrayList<AudioLogReminder> reminderLogs;

        SendLogsToPhone(ArrayList<AudioLogReminder> reminderLogs){
            path = Consts.REMINDER_LOG_PATH;
            this.reminderLogs = reminderLogs;
        }

        @Override
        public void run() {
            Log.e("MSGCOM", "running sendLogRequest in Coms Handler wear");

            // Get connected wearable nodes
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();

            // Convert reminder logs to list of datamap items
            ArrayList<DataMap> listAsDataMaps = convertListToDataMapList(reminderLogs);

            for(Node node : nodes.getNodes()) {
                Log.e("MSGCOM", "Node found, sending log request to node");

                PutDataMapRequest request = PutDataMapRequest.create(path);
                request.getDataMap().putLong("time", new Date().getTime());
                request.getDataMap().putDataMapArrayList(Consts.REMINDER_LOG_LIST_DATAMAP, listAsDataMaps);

                // Send the message to the data layer for syncing
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleClient, request.asPutDataRequest().setUrgent());

                // Set callback for debugging
                pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.d("MSG", "Logs Not Sent in ComsHandler");
                        } else {
                            Log.d("MSG", "Logs Sent");
                        }
                    }
                });

                /*
                Wearable.MessageApi.sendMessage(googleClient, node.getId(), path,)
                        .await();
                */
            }

            googleClient.disconnect();
        }
    }

    private ArrayList<DataMap> convertListToDataMapList(ArrayList<AudioLogReminder> reminderLogs){

        ArrayList<DataMap> listAsDataMaps = new ArrayList<>();
        for(AudioLogReminder alr : reminderLogs){
            listAsDataMaps.add(alr.putToDataMap(new DataMap()));
        }

        return listAsDataMaps;
    }

    /*
    private byte[] convertToByteArray(ArrayList<AudioLogReminder> reminderLogList) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(reminderLogList);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */
}
