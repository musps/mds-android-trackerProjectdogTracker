package info.serxan.trackerproject_dogtracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.serxan.trackerproject_dogtracker.R;
import info.serxan.trackerproject_dogtracker.services.TrackerService;
import info.serxan.trackerproject_dogtracker.tools.PermissionTool;

public class defaultActivity extends AppCompatActivity {

    public PermissionTool permTool;

    /**
     * Show a default image.
     * Instantiate the Tracker Service and start the process.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        // --> Hide action bar.
        getSupportActionBar().hide();

        // --> Init custom class.
        this.permTool = new PermissionTool(this, this);

        // --> Check permission or ask them.
        if (! this.permTool.check()) {
            this.permTool.ask();
        }

        // --> Create and run the service.
        Intent i = new Intent(getApplicationContext(),  TrackerService.class);
        startService(i);
    }
}
