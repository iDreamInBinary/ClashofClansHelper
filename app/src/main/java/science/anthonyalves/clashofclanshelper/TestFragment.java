package science.anthonyalves.clashofclanshelper;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TestFragment extends Fragment {

    public String mName;

    public void setName (String name) {
        mName = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test_fragment_layout, container, false);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(mName);
        return v;
    }
}
