package science.anthonyalves.clashofclanshelper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TroopCalculatorFragment extends Fragment {


    private Activity mActivity;
    LinearLayout calcs;

    int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.troop_calculator_layout, container, false);

        calcs = (LinearLayout) v.findViewById(R.id.calc_view);
        (v.findViewById(R.id.textVieww)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow();
            }
        });

        return v;
    }

    private void addRow() {
        CardView tt = (CardView) LayoutInflater.from(mActivity).inflate(R.layout.calculator_result_row, null);

        LinearLayout temp = (LinearLayout) tt.findViewById(R.id.frame);

        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
        lineParams.gravity = Gravity.CENTER_VERTICAL;

        View line = new View(mActivity);
        line.setBackgroundResource(R.drawable.divider);
        line.setLayoutParams(lineParams);

        ImageView image = new ImageView(mActivity);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(100, 100);
        ll.gravity = Gravity.CENTER_VERTICAL;
        image.setLayoutParams(ll);
        image.setImageResource(R.drawable.gold_mine_10);


        TextView tv = new TextView(mActivity);
        tv.setText("x100");
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(ll);

        temp.addView(image);
        temp.addView(tv);
        temp.addView(line);

        calcs.addView(tt);

    }
}
