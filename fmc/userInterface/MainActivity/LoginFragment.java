package cs240.fmc.userInterface.MainActivity;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import cs240.fmc.AsyncTasks.LoginAsyncTask;
import cs240.fmc.AsyncTasks.RegisterAsyncTask;
import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Person;
import cs240.fmc.network.ServerProxy;
import cs240.fmc.request.LoginRequest;
import cs240.fmc.request.RegisterRequest;
import cs240.fmc.result.LoginResult;
import cs240.fmc.result.RegisterResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements LoginAsyncTask.Listener, RegisterAsyncTask.Listener {

    private Button signInButton;
    private Button registerButton;

    private EditText serverHostEdit;
    private EditText serverPortEdit;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText emailEdit;

    private RadioButton maleButton;
    private RadioButton femaleButton;
    private RadioGroup genderOptions;

    public LoginFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostEdit = (EditText) v.findViewById(R.id.serverHostEditText);
        serverPortEdit = (EditText) v.findViewById(R.id.serverPortEditText);
        userNameEdit = (EditText) v.findViewById(R.id.userNameEditText);
        passwordEdit = (EditText) v.findViewById(R.id.passwordEditText);
        firstNameEdit = (EditText) v.findViewById(R.id.firstNameEditText);
        lastNameEdit = (EditText) v.findViewById(R.id.lastNameEditText);
        emailEdit = (EditText) v.findViewById(R.id.emailEditText);
        maleButton = (RadioButton) v.findViewById(R.id.radio_male);
        femaleButton = (RadioButton) v.findViewById(R.id.radio_female);
        genderOptions = (RadioGroup) v.findViewById(R.id.genderOptions);
        signInButton = (Button) v.findViewById(R.id.sign_in_button);
        registerButton = (Button) v.findViewById(R.id.register_button);

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSignInState();
                setRegisterState();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
        RadioButton.OnCheckedChangeListener listener = new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setRegisterState();
            }
        };

        serverPortEdit.addTextChangedListener(textWatcher);
        serverHostEdit.addTextChangedListener(textWatcher);
        userNameEdit.addTextChangedListener(textWatcher);
        passwordEdit.addTextChangedListener(textWatcher);
        firstNameEdit.addTextChangedListener(textWatcher);
        lastNameEdit.addTextChangedListener(textWatcher);
        emailEdit.addTextChangedListener(textWatcher);
        maleButton.setOnCheckedChangeListener(listener);
        femaleButton.setOnCheckedChangeListener(listener);

        setDefaultLogin();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClicked();
            }
        });
        return v;
    }


    //TESTING PURPOSES DEFAULT LOGIN
    public void setDefaultLogin() {
        serverHostEdit.setText(R.string.defaultServerHost);
        serverPortEdit.setText(R.string.defaultPortNumber);
        userNameEdit.setText("sheila");
        passwordEdit.setText("parker");
    }

    public void onRegisterButtonClicked() {
        if (verifyValidRegisterInput()) {
            String serverHost = serverHostEdit.getText().toString();
            int portNumber = Integer.parseInt(serverPortEdit.getText().toString());
            ServerProxy.setServerHostName(serverHost);
            ServerProxy.setServerPortNumber(portNumber);

            RegisterRequest request = getRegisterRequest();
            RegisterAsyncTask task = new RegisterAsyncTask(this);
            task.execute(request);
        }
        else {
            Toast.makeText(getActivity(), R.string.invalidRegister, Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginButtonClicked() {
        if (verifyValidLoginInput()) {
            String serverHost = serverHostEdit.getText().toString();
            int portNumber = Integer.parseInt(serverPortEdit.getText().toString());
            ServerProxy.setServerHostName(serverHost);
            ServerProxy.setServerPortNumber(portNumber);

            LoginRequest request = getLoginRequest();
            LoginAsyncTask task = new LoginAsyncTask(this);
            task.execute(request);        }
        else {
            Toast.makeText(getActivity(), R.string.invalidLogin, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verifyValidRegisterInput() {
        if (userNameEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals("") ||
            firstNameEdit.getText().toString().equals("") || lastNameEdit.getText().toString().equals("") ||
            emailEdit.getText().toString().equals("") || !genderSelected()) {
            return false;
        }
        return true;
    }

    private boolean verifyValidLoginInput() {
        if (userNameEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    private boolean genderSelected() {
        if (maleButton.isChecked() || femaleButton.isChecked()) {
            return true;
        }
        return false;
    }

    public LoginRequest getLoginRequest() {
        String userName = userNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        return new LoginRequest(userName, password);
    }

    public RegisterRequest getRegisterRequest() {
        String userName = userNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String gender;
        if (maleButton.isChecked()) {
            gender = "m";
        }
        else {
            gender = "f";
        }
        return new RegisterRequest(userName, password, email, firstName, lastName, gender);
    }

    @Override
    public void onError(Error e) {}

    @Override
    public void onRegisterSuccess(RegisterResult result) {
        MainActivity activity = (MainActivity) getActivity();
        activity.onRegisterSuccess(result);
    }

    @Override
    public void onLoginSuccess(LoginResult result) {
        MainActivity activity = (MainActivity) getActivity();
        activity.onLoginSuccess(result);
    }

    @Override
    public void onLoginFail() {
        Toast.makeText( getActivity(), "Login attempt failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterFail() {
        Toast.makeText( getActivity(), "Register attempt failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoneRetrieve(LoginResult result) {
        DataCache dataCache = DataCache.getInstance();
        String userPersonID = result.getPersonID();
        Person userPerson = dataCache.getPeople().get(userPersonID);
        assert userPerson != null;
        loginToast(userPerson);
    }

    @Override
    public void onDoneRetrieve(RegisterResult result) {
        DataCache dataCache = DataCache.getInstance();
        String userPersonID = result.getPersonID();
        Person userPerson = dataCache.getPeople().get(userPersonID);
        assert userPerson != null;
        loginToast(userPerson);
    }

    private void loginToast(Person userPerson) {
        String firstName = userPerson.getFirstName();
        String lastName = userPerson.getLastName();
        Toast.makeText( getActivity(), "Welcome " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
    }

    private void setSignInState() {
        boolean enabled = true;
        enabled = enabled && !serverHostEdit.getText().toString().isEmpty();
        enabled = enabled && !serverPortEdit.getText().toString().isEmpty();
        enabled = enabled && !userNameEdit.getText().toString().isEmpty();
        enabled = enabled && !passwordEdit.getText().toString().isEmpty();
        signInButton.setEnabled(enabled);
    }

    private void setRegisterState() {
        boolean enabled = true;
        enabled = enabled && !serverHostEdit.getText().toString().isEmpty();
        enabled = enabled && !serverPortEdit.getText().toString().isEmpty();
        enabled = enabled && !userNameEdit.getText().toString().isEmpty();
        enabled = enabled && !passwordEdit.getText().toString().isEmpty();
        enabled = enabled && !firstNameEdit.getText().toString().isEmpty();
        enabled = enabled && !lastNameEdit.getText().toString().isEmpty();
        enabled = enabled && !emailEdit.getText().toString().isEmpty();
        enabled = enabled && (maleButton.isChecked() || femaleButton.isChecked());
        registerButton.setEnabled(enabled);
    }
}