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
import android.widget.Toast;
import utils.CheckNetwork;
import utils.HustlrAPI;
import utils.ValidateUserInfo;
import today.hustlr.api.entity.User;

public class ProfileActivity extends Activity implements View.OnClickListener {
    EditText edit_first_name, edit_last_name, edit_email, edit_phone, edit_address, edit_city, edit_region, edit_postalcode;

    Button btn_update;
    private CreateUserTask mCreateTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String verification_code;

        edit_first_name = (EditText) findViewById(R.id.edit_first_name);
        edit_last_name = (EditText) findViewById(R.id.edit_last_name);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_address = (EditText) findViewById(R.id.edit_address);
        edit_city = (EditText) findViewById(R.id.edit_city);
        edit_region = (EditText) findViewById(R.id.edit_region);
        edit_postalcode = (EditText) findViewById(R.id.edit_postalcode);


        btn_update = (Button) findViewById(R.id.btn_verify);
        btn_update.setOnClickListener(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptUpdate() {
        // Store values at the time of the login attempt.
        String first_name = edit_first_name.getText().toString();
        String last_name = edit_last_name.getText().toString();
        String email = edit_email.getText().toString();
        String phone = edit_phone.getText().toString();
        String address = edit_address.getText().toString();
        String city = edit_city.getText().toString();
        String region = edit_region.getText().toString();
        String postal_code = edit_postalcode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            edit_phone.setError(getString(R.string.error_field_required));
            focusView = edit_phone;
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

            mCreateTask = new CreateUserTask(user);
            mCreateTask.execute((Void) null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                attemptUpdate();
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
        private final User user;


        CreateUserTask(User user) {
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return HustlrAPI.saveUser(user);
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


