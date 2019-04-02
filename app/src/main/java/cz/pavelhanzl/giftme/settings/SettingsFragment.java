package cz.pavelhanzl.giftme.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import cz.pavelhanzl.giftme.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //spustí aktivitu pro změnu hesla
        super.onCreate(savedInstanceState);
        Preference changePasswordButton = findPreference("settings_preferences_change_password_button");
        changePasswordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), Activity_ChangePassword.class));
                return true;
            }
        });

        //vytvoří intent pro odeslání emailu na účet administrátora, s předefinovaný začátkem zprávy.
        Preference sendFeedbackButton = findPreference("settings_preferences_send_feedback");
        sendFeedbackButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent Email = new Intent(Intent.ACTION_SENDTO);
                Email.setData(Uri.parse("mailto:"));
                Email.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.admin_email) });
                Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) +" "+ getString(R.string.settings_feedback));
                Email.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_feedback_email_message) + "");
                startActivity(Intent.createChooser(Email, getString(R.string.settings_send_feedback_title)));
                return true;
            }
        });
    }
}