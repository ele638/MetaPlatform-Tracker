package metaplatform.ele638.tracker.feature;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment taskList;
    private Fragment timeList;
    private FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            if(item.getItemId() == R.id.navigation_task){
                //Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();
                fragmentTransaction.replace(R.id.mainFrame, taskList);
            }
            if(item.getItemId() == R.id.navigation_time){
                //Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();
                fragmentTransaction.replace(R.id.mainFrame, timeList);
            }
            fragmentTransaction.commit();
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new TaskList();
        timeList = new TimeList();
        fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainFrame, taskList);
        fragmentTransaction.commit();

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
