package cs240.fmc.AsyncTasks;

import android.os.AsyncTask;

import cs240.fmc.network.ServerProxy;
import cs240.fmc.result.RegisterResult;

public class RegisterRetrieveDataAsyncTask extends AsyncTask<RegisterResult, Void, RegisterResult> {

    public interface Listener  {
        void onError(Error e);
        void doneRetrieve(RegisterResult result);
    }

    private RegisterRetrieveDataAsyncTask.Listener listener;
    public RegisterRetrieveDataAsyncTask(Listener l) { listener = l; }

    @Override
    protected RegisterResult doInBackground(RegisterResult... registerResults) {
        RegisterResult result = registerResults[0];
        if (result.isSuccess()) {
            ServerProxy server = new ServerProxy();
            server.retrieveFamilyData(result);
        }
        return result;
    }

    protected void onPostExecute(RegisterResult result) {
        listener.doneRetrieve(result);
    }
}
