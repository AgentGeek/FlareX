package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.CFPostsAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentCloudflareBlog extends FragmentCC implements CFPostsAdapter.PostListener {

    private ArrayList<CFPost> posts = new ArrayList<>();
    private CFPostsAdapter adapter;
    private boolean refreshing = false;
    private int page = 1;

    private RecyclerView recycler;

    private static final String BASE_URL = "https://blog.cloudflare.com";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cloudflare_blog, container, false);

        recycler = root.findViewById(R.id.recycler);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    page++;
                    updateList();
                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!refreshing) updateList();
    }

    public void updateList() {
        refreshing = true;
        setLoading(true);

        CFApi.getBlogPosts(requireContext(), page, new CFApi.HTMLListener() {
            @Override
            public void onResult(String html) {
                parse(html);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPostClicked(CFPost post) {
        Log.d("HERE", "onPostClicked: CLICKED: "+ post.title);
        ((MainActivity) requireActivity()).viewManager.setView(ViewManager.VIEW_CLOUDFLARE_POST, post);
    }

    /*
        Parsing Part
     */

    private void parse(String html) {
        try {
            if (page == 1) {
                posts = new ArrayList<>();
                adapter = new CFPostsAdapter(requireActivity(), posts);
                adapter.setListener(this);
                recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                recycler.setAdapter(adapter);
            }

            int begin = posts.size();
            parsePosts(html);
            int end = posts.size();

            if (page == 1) AppParameter.setLastPost(requireContext(), posts.get(0).url);

            adapter.notifyItemRangeInserted(begin, end);
            refreshing = false;
            setLoading(false);
        } catch (Exception e) {
            setLoading(false);
            e.printStackTrace();
            Toast.makeText(getContext(), "Parsing Error", Toast.LENGTH_LONG).show();
        }
    }

    private void parsePosts(String html) {
        Document doc = Jsoup.parse(html);
        Element main = doc.select("main#main-body").first();
        Elements articles = main.select("article");

        for (Element article : articles) {
            posts.add(CFPost.parse(article));
        }
    }
}
