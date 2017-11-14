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
import utils.HustlrAPI;
import utils.ValidateUserInfo;


public class ProfileActivity extends Activity implements View.OnClickListener{
    EditText edit_code;
    TextView txt_alreadyHave;
    Button btn_verify;
    private CreateUserTask mCreateTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String verification_code;

        edit_code = (EditText) findViewById(R.id.edit_code);
        txt_alreadyHave = (TextView) findViewById(R.id.txt_already_have);
        txt_alreadyHave.setOnClickListener(this);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptVerify() {
        // Store values at the time of the login attempt.
        String verify_code = edit_code.getText().toString();
        String email = "";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (!bundle.isEmpty()) {
            email = bundle.getString("email");
        }

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(verify_code)) {
            edit_code.setError(getString(R.string.error_field_required));
            focusView = edit_code;
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

            mCreateTask = new CreateUserTask(verify_code, email);
            mCreateTask.execute((Void) null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                attemptVerify();
                break;
            case R.id.txt_already_have:
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
        private final String verify_code;
        private final String email;

        CreateUserTask(String verify_code, String email) {
            this.email = email;
            this.verify_code = verify_code;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return HustlrAPI.verifyCode(verify_code, email);

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            CheckNetwork checkNetwork = new CheckNetwork();
            if (checkNetwork.isConnected(ProfileActivity.this) && success) {
                Toast.makeText(ProfileActivity.this, "Account created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
            if (success) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }
}


