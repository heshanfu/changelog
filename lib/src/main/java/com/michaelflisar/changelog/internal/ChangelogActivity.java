package com.michaelflisar.changelog.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.michaelflisar.changelog.ChangelogBuilder;
import com.michaelflisar.changelog.ChangelogUtil;
import com.michaelflisar.changelog.R;

/**
 * Created by flisar on 06.03.2018.
 */

public class ChangelogActivity extends AppCompatActivity {

    public static Intent createIntent(Context context, ChangelogBuilder changelogBuilder, Integer theme) {
        Intent intent = new Intent(context, ChangelogActivity.class);
        intent.putExtra("builder", changelogBuilder);
        intent.putExtra("theme", theme == null ? -1 : theme);
        return intent;
    }

    private ChangelogBuilder mBuilder;
    private ChangelogParserAsyncTask mAsyncTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int theme = getIntent().getIntExtra("theme", -1);
        if (theme > 0) {
            setTheme(theme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changelog_activity);

        mBuilder = getIntent().getParcelableExtra("builder");

        getSupportActionBar().setTitle(getString(R.string.changelog_dialog_title, ChangelogUtil.getAppVersionName(this)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProgressBar pb = findViewById(R.id.pbLoading);
        RecyclerView rv = findViewById(R.id.rvChangelog);
        ChangelogRecyclerViewAdapter adapter = mBuilder.setupEmptyRecyclerView(rv);

        mAsyncTask = new ChangelogParserAsyncTask(this, pb, adapter, mBuilder);
        mAsyncTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }
}
