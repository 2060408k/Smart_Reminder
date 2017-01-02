package highway62.reminderapp.communication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

import highway62.reminderapp.adminSettings.LogPrinter;
import highway62.reminderapp.constants.Consts;
import highway62.reminderapp.marshalling.ParcelableUtil;
import highway62.reminderapp.reminders.AudioLogReminder;
import highway62.reminderapp.reminders.BaseReminder;

/**
 * Created by Highway62 on 15/09/2016.
 */
public class CommunicationHandler implements DataApi.DataListener{

    private GoogleApiClient googleClient;
    private Context context;

    public CommunicationHandler(Context context){
        this.context = context;
    }

    public void sendReminderToWatch(final BaseReminder reminder){
        Log.e("MSGCOM", "sendReminderToWatch in Coms handler connecting to googleClient");
        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new Thread(new SendReminderMessage(reminder)).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("MSGCOM", "ConnectionCallback onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("MSGCOM", "ConnectionCallback onConnectionFailed");
                    }
                })
                .build();
        // TODO re-do this as an activity or service that connects to google client
        // in onResume, and disconnects in onPause
        // Make it implement the callbacks and dataapi.datalistener

        googleClient.connect();
    }

    private class SendReminderMessage implements Runnable {
        String path;
        BaseReminder reminder;

        SendReminderMessage(BaseReminder reminder){
            path = Consts.REMINDER_PATH;
            this.reminder = reminder;
        }

        @Override
        public void run(){
            Log.e("MSGCOM", "running sendMessage in SendReminderMessage");
            // Get connected wearable nodes
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();

            for(Node node : nodes.getNodes()) {
                Log.e("MSGCOM", "Node found, sending reminder to node");
                Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, ParcelableUtil.marshall(reminder))
                        .await();
            }

            googleClient.disconnect();
        }
    }

    public void sendLogRequestToWatch(){
        Log.e("MSGCOM", "sendLogRequestToWatch in Coms handler connecting to googleClient");
        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new Thread(new SendLogRequest()).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("MSGCOM", "ConnectionCallback onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("MSGCOM", "ConnectionCallback onConnectionFailed");
                    }
                })
                .build();

        googleClient.connect();
    }

    private class SendLogRequest implements Runnable{

        String path;

        SendLogRequest(){
            path = Consts.REMINDER_LOG_PATH;
        }

        @Override
        public void run() {
            Log.e("MSGCOM", "running sendLogRequest in Coms Handler");

            // Get connected wearable nodes
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();

            for(Node node : nodes.getNodes()) {
                Log.e("MSGCOM", "Node found, sending log request to node");
                Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        if(sendMessageResult.getStatus().isSuccess()){
                            Toast toast = Toast.makeText(context, "Log Request Sent to Watch", Toast.LENGTH_SHORT);
                            toast.show();
                        }else{
                            Toast toast = Toast.makeText(context, "Error: Log Request Not Sent", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }

            googleClient.disconnect();
        }
    }

    // Handles receiving the list of audio reminder logs from the watch
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMapReceived;

        for (DataEvent event : dataEvents) {

            if(event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals(Consts.REMINDER_LOG_PATH)){
                dataMapReceived = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                ArrayList<AudioLogReminder> reminderLogs = convertDataMapListToAudioLogList(dataMapReceived
                        .getDataMapArrayList(Consts.REMINDER_LOG_LIST_DATAMAP));

                if(reminderLogs.size() > 0){
                    Toast toast = Toast.makeText(context, "Printing Audio Logs Received from Watch", Toast.LENGTH_SHORT);
                    toast.show();

                    //TODO add datapi stuff to manifest

                    // Print the audio logs
                    LogPrinter logPrinter = new LogPrinter(context);
                    logPrinter.printWatchLogs(reminderLogs);

                }else{
                    Toast toast = Toast.makeText(context, "No Audio Logs from Watch to Print", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }
    }

    private ArrayList<AudioLogReminder> convertDataMapListToAudioLogList(ArrayList<DataMap> dataMapList){
        ArrayList<AudioLogReminder> reminderLogs = new ArrayList<>();
        for(DataMap dm : dataMapList){
            reminderLogs.add(new AudioLogReminder(dm));
        }

        return reminderLogs;
    }
}
