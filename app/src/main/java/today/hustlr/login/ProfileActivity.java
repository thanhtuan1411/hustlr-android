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
import utils.Constants;
import utils.HustlrAPI;
import utils.ValidateUserInfo;
import today.hustlr.api.entity.User;

public class ProfileActivity extends Activity implements View.OnClickListener {
    EditText edit_first_name, edit_last_name, edit_email, edit_phone, edit_address, edit_city, edit_region, edit_postalcode;

    Button btn_update;
    private UpdateUserTask mCreateTask = null;

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

        if (Constants.loggedInUser != null) {
            String tt = Constants.loggedInUser.street;
            edit_first_name.setText((Constants.loggedInUser.first_name == "null") ? "" : Constants.loggedInUser.first_name);
            edit_last_name.setText(Constants.loggedInUser.last_name == "null" ? "" : Constants.loggedInUser.last_name);
            edit_email.setText(Constants.loggedInUser.email == "null" ? "" : Constants.loggedInUser.email);
            edit_phone.setText(Constants.loggedInUser.cell_phone == "null" ? "" : Constants.loggedInUser.cell_phone);
            edit_address.setText(Constants.loggedInUser.street == "null" ? "" : Constants.loggedInUser.street);
            edit_city.setText(Constants.loggedInUser.city == "null" ? "" : Constants.loggedInUser.city);
            edit_region.setText(Constants.loggedInUser.region == "null" ? "" : Constants.loggedInUser.region);
            edit_postalcode.setText(Constants.loggedInUser.postal_code == "null" ? "" : Constants.loggedInUser.postal_code);
        }

        btn_update = (Button) findViewById(R.id.btn_update);
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
        User user = Constants.loggedInUser;
        user.first_name = edit_first_name.getText().toString();
        user.last_name = edit_last_name.getText().toString();
        user.email = edit_email.getText().toString();
        user.cell_phone = edit_phone.getText().toString();
        user.street = edit_address.getText().toString();
        user.city = edit_city.getText().toString();
        user.region = edit_region.getText().toString();
        user.postal_code = edit_postalcode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(user.email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        }

        if (TextUtils.isEmpty(user.cell_phone)) {
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



            mCreateTask = new UpdateUserTask(user);
            mCreateTask.execute((Void) null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
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
    public class UpdateUserTask extends AsyncTask<Void, Void, Boolean> {
        private final User user;

        UpdateUserTask(User user) {
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
                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
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


