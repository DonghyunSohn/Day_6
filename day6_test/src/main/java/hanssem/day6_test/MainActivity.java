package hanssem.day6_test;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    GridView monthView;
    CalendarMonthAdapter monthViewAdapter;

    TextView monthText;

    int curYear;
    int curMonth;
    int curPosition;

    EditText scheduleInput;
    Button saveButton;

    ListView scheduleList;
    ScheduleListAdapter scheduleAdapter;
    ArrayList<ScheduleListItem> outScheduleList;

    public static final int REQUEST_CODE_SCHEDULE_INPUT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new CalendarMonthAdapter(this);
        monthView.setAdapter(monthViewAdapter);

        monthView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MonthItem curItem = (MonthItem) monthViewAdapter.getItem(position);
                int day = curItem.getDay();

                monthViewAdapter.setSelectedPosition(position);
                monthViewAdapter.notifyDataSetChanged();

                curPosition = position;

                outScheduleList = monthViewAdapter.getSchedule(position);
                if (outScheduleList == null) {
                    outScheduleList = new ArrayList<ScheduleListItem>();
                }
                scheduleAdapter.scheduleList = outScheduleList;

                scheduleAdapter.notifyDataSetChanged();
            }
        });

        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        Button monthPrevious = (Button) findViewById(R.id.monthPrevious);
        monthPrevious.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setPreviousMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });

        Button monthNext = (Button) findViewById(R.id.monthNext);
        monthNext.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setNextMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });


        curPosition = -1;

        scheduleList = (ListView) findViewById(R.id.scheduleList);
        scheduleAdapter = new ScheduleListAdapter(this);
        scheduleList.setAdapter(scheduleAdapter);


    }


    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    private void addOptionMenuItems(Menu menu) {
        int id = Menu.FIRST;
        menu.clear();

        menu.add(id, id, Menu.NONE, "일정 추가");
    }

    private void showScheduleInput() {
        Intent intent = new Intent(this, ScheduleInputActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCHEDULE_INPUT);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null) {
            if (requestCode == REQUEST_CODE_SCHEDULE_INPUT) {
                String time = intent.getStringExtra("time");
                String message = intent.getStringExtra("message");

                if (message != null) {
                    Toast toast = Toast.makeText(getBaseContext(), "일시 : " + time + "\n" + "일정 : " + message, Toast.LENGTH_SHORT);
                    toast.show();

                    ScheduleListItem aItem = new ScheduleListItem(time, message);


                    if (outScheduleList == null) {
                        outScheduleList = new ArrayList<ScheduleListItem>();
                    }

                    outScheduleList.add(aItem);

                    monthViewAdapter.putSchedule(curPosition, outScheduleList);

                    scheduleAdapter.scheduleList = outScheduleList;
                    scheduleAdapter.notifyDataSetChanged();
                }
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        addOptionMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                showScheduleInput();

                return true;
            default:
                break;
        }

        return false;
    }
}
