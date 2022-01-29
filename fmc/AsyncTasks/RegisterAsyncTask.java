package cs240.fmc.AsyncTasks;

import android.os.AsyncTask;

import cs240.fmc.network.ServerProxy;
import cs240.fmc.request.RegisterRequest;
import cs240.fmc.result.RegisterResult;

public class RegisterAsyncTask extends AsyncTask<RegisterRequest, Void, RegisterResult>
                                implements RegisterRetrieveDataAsyncTask.Listener {

    public interface Listener  {
        void onRegisterSuccess(RegisterResult result);
        void onRegisterFail();
        void onDoneRetrieve(RegisterResult result);
        void onError(Error e);
    }

    public RegisterAsyncTask(RegisterAsyncTask.Listener l) { listener = l; }
    private RegisterAsyncTask.Listener listener;

    @Override
    protected RegisterResult doInBackground(RegisterRequest... requests) {
        RegisterRequest request = requests[0];
        ServerProxy serverProxy = new ServerProxy();
        RegisterResult result = serverProxy.register(request);
        return result;
    }

    protected void onPostExecute (RegisterResult result) {
        if (result.isSuccess()) {
            RegisterRetrieveDataAsyncTask task = new RegisterRetrieveDataAsyncTask(this);
            task.execute(result);
            onRegisterSuccess(result);
        }
        else {
            onRegisterFail();
        }
    }

    public void onRegisterFail() {
        listener.onRegisterFail();
    }

    public void onRegisterSuccess(RegisterResult result) {
        listener.onRegisterSuccess(result);
    }

    @Override
    public void onError(Error e) {}

    @Override
    public void doneRetrieve(RegisterResult result) {
        listener.onDoneRetrieve(result);
    }
}
