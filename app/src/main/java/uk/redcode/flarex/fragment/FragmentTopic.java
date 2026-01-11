package uk.redcode.flarex.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.PostAdapter;
import uk.redcode.flarex.network.CFCommunity;
import uk.redcode.flarex.object.Topic;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentTopic extends FragmentCC {

    public Topic topic = null;
    private RecyclerView recycler;
    private PostAdapter adapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
        this.lastView = ViewManager.VIEW_COMMUNITY;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_topic, container, false);
        this.recycler = root.findViewById(R.id.recycler);
        this.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (topic.streams.size() <= topic.posts.size()) return;
                    loadNextTopic();
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (topic == null) return;

        if (topic.posts.size() == 0) {
            updateContent();
            return;
        }

        drawContent();
    }

    private void updateContent() {
        setLoading(true);
        CFCommunity.getContent(requireContext(), topic, new CFCommunity.ResultListener() {
            @Override
            public void onResult() {
                drawContent();
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void drawContent() {
        adapter = new PostAdapter(requireActivity(), topic);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

    }

    public void showOnWeb() {
        if (topic == null) return;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(topic.url));
        startActivity(browserIntent);
    }

    private void loadNextTopic() {
        int start = topic.posts.size();
        setLoading(true);
        CFCommunity.getNextContent(requireContext(), topic, new CFCommunity.ResultListener() {
            @Override
            public void onResult() {
                if (adapter == null) return;
                adapter.notifyItemRangeInserted(start, topic.posts.size());
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

}
