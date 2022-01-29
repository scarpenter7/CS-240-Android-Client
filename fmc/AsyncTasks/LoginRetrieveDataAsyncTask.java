package cs240.fmc.AsyncTasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;
import cs240.fmc.network.ServerProxy;
import cs240.fmc.request.EventRequest;
import cs240.fmc.request.PersonRequest;
import cs240.fmc.result.EventsResult;
import cs240.fmc.result.LoginResult;
import cs240.fmc.result.PersonsResult;

public class LoginRetrieveDataAsyncTask extends AsyncTask<LoginResult, Void, LoginResult> {
    public interface Listener  {
        void onError(Error e);
        void doneRetrieve(LoginResult result);
    }

    private LoginRetrieveDataAsyncTask.Listener listener;
    public LoginRetrieveDataAsyncTask(LoginRetrieveDataAsyncTask.Listener l) { listener = l; }

    @Override
    protected LoginResult doInBackground(LoginResult... loginResults) {
        LoginResult result = loginResults[0];
        if (result.isSuccess()) {
            ServerProxy server = new ServerProxy();
            server.retrieveFamilyData(result);
        }
        return result;
    }

    protected void onPostExecute(LoginResult result) {
        listener.doneRetrieve(result);
    }


}
