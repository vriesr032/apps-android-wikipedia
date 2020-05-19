package org.wikipedia.settings;

import android.os.Bundle;
import android.widget.TextView;

import org.wikipedia.R;
import org.wikipedia.activity.BaseActivity;
import org.wikipedia.util.ResourceUtil;
import org.wikipedia.util.StringUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.wikipedia.util.FileUtil.readFile;

/**
 * Displays license text of the libraries we use.
 */
public class LicenseActivity extends BaseActivity {
    private Logger logger = Logger.getAnonymousLogger();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        setNavigationBarColor(ResourceUtil.getThemedColor(this, android.R.attr.windowBackground));

        final int libraryNameStart = 24;
        if (getIntent().getData() == null
                || getIntent().getData().getPath() == null
                || getIntent().getData().getPath().length() <= libraryNameStart) {
            return;
        }
        final String path = getIntent().getData().getPath();
        // Example string: "/android_asset/licenses/Otto"
        setTitle(getString(R.string.license_title, path.substring(libraryNameStart)));

        try {
            TextView textView = findViewById(R.id.license_text);
            final int assetPathStart = 15;
            final String text = readFile(getAssets().open(path.substring(assetPathStart)));
            textView.setText(StringUtil.fromHtml(text.replace("\n\n", "<br/><br/>")));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }
}
