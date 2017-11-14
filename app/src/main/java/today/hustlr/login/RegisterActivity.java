package today.hustlr.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import utils.CheckNetwork;
import utils.Constants;
import utils.HustlrAPI;
import utils.ValidateUserInfo;
import today.hustlr.api.entity.User;

public class RegisterActivity extends Activity implements View.OnClickListener{
    EditText edit_first_name, edit_last_name, edit_email, edit_password, edit_phone;
    TextView txt_alreadyHave;
    Button btn_registrar;
    private CreateUserTask mCreateTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        String email;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            email = extras == null ? "" : extras.getString(Constants.TAG_EMAIL);
        } else {
            email = savedInstanceState.getString(Constants.TAG_EMAIL);
        }

        edit_first_name = (EditText) findViewById(R.id.edit_first_name);
        edit_last_name = (EditText) findViewById(R.id.edit_last_name);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_email.setText(email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        txt_alreadyHave = (TextView) findViewById(R.id.txt_already_have);
        txt_alreadyHave.setOnClickListener(this);

        btn_registrar = (Button) findViewById(R.id.btn_verify);
        btn_registrar.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptCreate() {
        // Store values at the time of the login attempt.
        String first_name = edit_first_name.getText().toString();
        String last_name = edit_last_name.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        String cell_phone = edit_phone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(first_name)) {
            edit_first_name.setError(getString(R.string.error_field_required));
            focusView = edit_first_name;
            cancel = true;
        } else if (TextUtils.isEmpty(last_name)) {
            edit_last_name.setError(getString(R.string.error_field_required));
            focusView = edit_last_name;
            cancel = true;
        } else if (TextUtils.isEmpty(cell_phone)) {
            edit_phone.setError(getString(R.string.error_field_required));
            focusView = edit_phone;
            cancel = true;
        }
        else if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        } else if (!validate.isEmailValid(email)) {
            edit_email.setError(getString(R.string.error_invalid_email));
            focusView = edit_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edit_password.setError(getString(R.string.error_field_required));
            focusView = edit_password;
            cancel = true;
        } else if (!validate.isPasswordValid(password)) {
            edit_password.setError(getString(R.string.error_invalid_password));
            focusView = edit_password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //TODO Create account logic
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            User user = new User();
            user.first_name = first_name;
            user.last_name = last_name;
            user.email = email;
            user.password = password;
            user.cell_phone = cell_phone;

            mCreateTask = new CreateUserTask(user);
            mCreateTask.execute((Void) null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                attemptCreate();
                break;
            case R.id.txt_already_have:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
        private final User user;

        CreateUserTask(User user) {
           this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: check if account already exists against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }


            // TODO: if there's no account registered, register the new account here.

//            TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            user.cell_phone = tMgr.getLine1Number();
            HustlrAPI.addUser(user);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            CheckNetwork checkNetwork = new CheckNetwork();
            if (checkNetwork.isConnected(RegisterActivity.this) && success) {
                Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("email",this.user.email);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
