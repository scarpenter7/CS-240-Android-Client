package cs240.fmc.AsyncTasks;

import android.os.AsyncTask;

import cs240.fmc.network.ServerProxy;
import cs240.fmc.request.LoginRequest;
import cs240.fmc.result.LoginResult;

public class LoginAsyncTask extends AsyncTask<LoginRequest, Void, LoginResult>
                            implements LoginRetrieveDataAsyncTask.Listener {

    public interface Listener  {
        void onError(Error e);
        void onLoginSuccess(LoginResult result);
        void onLoginFail();
        void onDoneRetrieve(LoginResult result);
    }

    public LoginAsyncTask(Listener l) { listener = l; }
    private Listener listener;

    @Override
    protected LoginResult doInBackground(LoginRequest... requests) {
        LoginRequest request = requests[0];
        ServerProxy serverProxy = new ServerProxy();
        LoginResult result = serverProxy.login(request);
        return result;
    }

    protected void onPostExecute (LoginResult result) {
        if (result.isSuccess()) {
            LoginRetrieveDataAsyncTask task = new LoginRetrieveDataAsyncTask(this);
            task.execute(result);
            onLoginSuccess(result);
        }
        else {
            onLoginFail();
        }
    }

    protected void onLoginSuccess(LoginResult result) {
        listener.onLoginSuccess(result);
    }

    protected void onLoginFail() {
        listener.onLoginFail();
    }


    @Override
    public void onError(Error e) {}

    @Override
    public void doneRetrieve(LoginResult result) {
        listener.onDoneRetrieve(result);
    }
}
