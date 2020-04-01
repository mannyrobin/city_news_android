package ru.mycity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import ru.mycity.MainActivity;
import ru.mycity.R;
import ru.mycity.adapter.ButtonsAdapter;
import ru.mycity.data.Button;
import ru.mycity.data.PushData;
import ru.mycity.data.RootData;

public class MainFragmentOld extends BaseFragment implements AdapterView.OnItemClickListener
{
    public static final String NAME = "MainFragment";
    private RootData rootData = null;

    public void setData(RootData rootData)
    {
        this.rootData = rootData;
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.main_fragment_old, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.tiles);
        List<Button> buttons = (null != rootData) ? rootData.buttons : null;
        if (null != buttons && !buttons.isEmpty())
        {
            /*
            ArrayList<Button> t = new ArrayList<>(34);
            for (int i = 0 ; i < 5; i++)
            {
                t.addAll(buttons);
            }
            buttons = t;
            */
            gridView.setAdapter(new ButtonsAdapter(inflater, buttons));
            gridView.setOnItemClickListener(this);
        }
        else
        {
            gridView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    protected CharSequence getTitle()
    {
        return getText(R.string.menu_item_categories);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        PushData pushData = (null != rootData) ? rootData.pushData : null;
        if (null != pushData)
        {
            MainActivity activity = (MainActivity) getActivity();
            if (null != activity)
            {
                activity.openItem(pushData);
            }
            rootData.pushData = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Button button = (Button) parent.getAdapter().getItem(position);
        MainActivity activity = (MainActivity) getActivity();
        if (null != activity)
        {
            activity.openItem(button.table, button.row_id, button.link, button.phone, false);
        }
    }

}
