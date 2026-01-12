package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.CategoryAdapter;
import uk.redcode.flarex.adapter.TopicAdapter;
import uk.redcode.flarex.network.CFCommunity;
import uk.redcode.flarex.object.Category;
import uk.redcode.flarex.object.Topic;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentCommunity extends FragmentCC implements TopicAdapter.TopicListener {

    private RecyclerView recycler;
    private BottomSheetBehavior<View> behavior;
    private TextInputEditText searchInput;
    private TabLayout tabs;
    private int lastView = LATEST;
    private int actualView = LATEST;

    private static final int LATEST = 0;
    private static final int CATEGORIES = 1;
    private static final int TOP = 2;
    private static final int SEARCH = 3;

    // data
    int pageLatest = 0;
    int pageTop = 0;
    private ArrayList<Topic> latest = null;
    private TopicAdapter adapterLatest = null;
    private ArrayList<Category> categories = null;
    private ArrayList<Topic> tops = null;
    private TopicAdapter adapterTop = null;
    private ArrayList<Topic> searchResult = null;

    private boolean wafCleared = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community, container, false);
        recycler = root.findViewById(R.id.recycler);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (actualView == SEARCH) {
                        //pageSearch++;
                        //refreshSearch();
                        return;
                    }
                    if (tabs.getSelectedTabPosition() == 0) {
                        pageLatest++;
                        refreshLatest();
                    }
                    if (tabs.getSelectedTabPosition() == 2) {
                        pageTop++;
                        refreshTop();
                    }
                }
            }
        });

        tabs = root.findViewById(R.id.tabs_community);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                actualView = tabs.getSelectedTabPosition();
                updateView();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        searchInput = root.findViewById(R.id.search_input);
        View bottomSheet = root.findViewById(R.id.search_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) search();
            }

            @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!wafCleared) {
            setLoading(true);
            Toast.makeText(requireContext(), R.string.community_first_run, Toast.LENGTH_LONG).show();
            CFCommunity.prepareConnection(requireContext(), new CFCommunity.ResultListener() {
                @Override
                public void onResult() {
                    wafCleared = true;
                    updateView();
                }

                @Override
                public void onError(Exception e) {
                    setLoading(false);
                    Toast.makeText(requireContext(), "Failed to clear browser check", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wafCleared) {
            TabLayout.Tab tab = tabs.getTabAt(actualView);
            if (tab != null) tab.select();
            updateView();
        }
    }

    @Override
    public void onTopicSelected(Topic topic) {
        setView(ViewManager.VIEW_COMMUNITY_TOPIC, topic);
    }

    private void updateView() {
        if (!wafCleared) return;

        if (actualView == SEARCH) {
            tabs.setVisibility(View.GONE);
            showSearchResult();
            return;
        } else {
            tabs.setVisibility(View.VISIBLE);
        }

        if (categories == null) {
            refreshCategories();
            return;
        }

        if (actualView == LATEST) {
            if (latest == null) {
                refreshLatest();
                return;
            }
            showLatest(null);
        } else if (actualView == CATEGORIES) {
            showCategories();
        } else if (actualView == TOP) {
            if (tops == null) {
                refreshTop();
                return;
            }
            showTop(null);
        }
    }

    /*
        Latest
     */

    private void refreshLatest() {
        setLoading(true);
        CFCommunity.getLatest(getContext(), pageLatest, new CFCommunity.TopicListener() {
            @Override
            public void onResult(ArrayList<Topic> topics) {
                showLatest(topics);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void showLatest(ArrayList<Topic> topics) {
        if (!isVisible()) return;

        if (topics == null) {
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(adapterLatest);
            return;
        }

        if (pageLatest == 0) {
            latest = topics;
            adapterLatest = new TopicAdapter(requireActivity(), latest);
            adapterLatest.setListener(this);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(adapterLatest);
        } else {
            int begin = latest.size();
            latest.addAll(topics);
            int end = latest.size();
            adapterLatest.notifyItemRangeInserted(begin, end);
        }
        setLoading(false);
    }

    /*
        Categories
     */

    private void refreshCategories() {
        setLoading(true);
        CFCommunity.getCategories(getContext(), new CFCommunity.CategoryListener() {
            @Override
            public void onResult(ArrayList<Category> categories) {
                FragmentCommunity.this.categories = categories;
                updateView();
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void showCategories() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        recycler.setAdapter(adapter);
        setLoading(false);
    }

    /*
        Top
     */

    private void refreshTop() {
        setLoading(true);
        CFCommunity.getTop(getContext(), pageTop, new CFCommunity.TopicListener() {
            @Override
            public void onResult(ArrayList<Topic> topics) {
                showTop(topics);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void showTop(ArrayList<Topic> topics) {
        if (!isVisible()) return;

        if (topics == null) {
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(adapterTop);
            return;
        }

        if (pageTop == 0) {
            tops = topics;
            adapterTop = new TopicAdapter(requireActivity(), tops);
            adapterTop.setListener(this);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(adapterTop);
        } else {
            int begin = tops.size();
            tops.addAll(topics);
            int end = tops.size();
            adapterTop.notifyItemRangeInserted(begin, end);
        }
        setLoading(false);
    }

    /*
        Search
     */

    public void openSearch() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void search() {
        if (searchInput.getText().toString().isEmpty()) {
            actualView = lastView;
            updateView();
            return;
        }

        Toast.makeText(requireContext(), "Search ...", Toast.LENGTH_SHORT).show();
        setLoading(true);
        if (actualView != SEARCH) lastView = actualView;
        actualView = SEARCH;

        CFCommunity.search(requireContext(), searchInput.getText().toString(), new CFCommunity.TopicListener() {
            @Override
            public void onResult(ArrayList<Topic> topics) {
                searchResult = topics;
                updateView();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void showSearchResult() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        TopicAdapter adapterSearch = new TopicAdapter(requireActivity(), searchResult);
        adapterSearch.setListener(this);
        recycler.setAdapter(adapterSearch);
        setLoading(false);
    }
}