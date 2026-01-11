package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CountryStat;

public class WorldTableFragment extends FragmentCC {

    private CountryStat stat = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Map functionality removed. Returning a basic view for now.
        return inflater.inflate(R.layout.recycler, container, false);
    }

    public void setStat(CountryStat stat) {
        this.stat = stat;
    }

    public boolean hasStat() {
        return stat != null;
    }
}