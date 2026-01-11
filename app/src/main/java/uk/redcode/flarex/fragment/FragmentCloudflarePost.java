package uk.redcode.flarex.fragment;

import android.content.Intent;
import android.net.Uri;
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

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.CFPostContentAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.CFPost;

public class FragmentCloudflarePost extends FragmentCC {

    private CFPost post = null;
    private String postToLoad = null;

    private RecyclerView recycler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cloudflare_post, container, false);

        recycler = root.findViewById(R.id.recycler);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updatePost();
    }

    public void setPost(@Nullable CFPost post) {
        this.post = post;
    }
    public void setPostToLoad(String url) {
        this.postToLoad = url;
    }

    public void showPost() {
        if (!isAdded()) return;
        CFPostContentAdapter adapter = new CFPostContentAdapter(requireActivity(), post);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) activity.setTitle(post.title);
    }

    public void updatePost() {
        setLoading(true);
        final String url = post != null ? post.url : postToLoad;
        Log.d("HERE", "updatePost: load: "+url);

        CFApi.getBlogPost(requireContext(), url, new CFApi.HTMLListener() {
            @Override
            public void onResult(String html) {
                parse(html, url);
                showPost();
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showOnWeb() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.url));
        startActivity(browserIntent);
    }

    /*
        Parsing Part
     */

    private void parse(String html, String url) {
        try {
            post = CFPost.parseFromPage(url, html);
            Document doc = Jsoup.parse(html);
            Element main = doc.select("main#post article section.post-full-content div.post-content").first();

            //post.content = new ArrayList<>();
            for (Element elem : main.children()) {
                //Log.d("HERE", "parse: "+elem.tagName());
                parseElement(elem);
            }

            setLoading(false);
        } catch (Exception e) {
            setLoading(false);
            e.printStackTrace();
            Toast.makeText(getContext(), "Parsing Error", Toast.LENGTH_LONG).show();
        }
    }

    private void parseElement(Element elem) {
        //Log.d("HERE", "parseElement: "+elem.childrenSize());
        if (elem.tagName().equals("figure")) {
            CFPost.Content img = new CFPost.Content(CFPost.Content.TYPE_IMG);
            Element imgElem = elem.select("img").first();
            img.setImg(imgElem.attr("src").isEmpty() ? imgElem.attr("data-cfsrc") : imgElem.attr("src"));
            post.contents.add(img);
        } else if (elem.tagName().equals("p") && elem.childrenSize() == 2 && elem.children().first().tagName().equals("img")) {
            CFPost.Content img = new CFPost.Content(CFPost.Content.TYPE_IMG);
            Element imgElem = elem.select("img").first();
            img.setImg(imgElem.attr("src").isEmpty() ? imgElem.attr("data-cfsrc") : imgElem.attr("src"));
            post.contents.add(img);
        } else if (elem.tagName().equals("p") && elem.classNames().size() == 0) {
            CFPost.Content text = new CFPost.Content(CFPost.Content.TYPE_TEXT);
            text.setText(elem.html());
            post.contents.add(text);
        } else if (elem.tagName().equals("center") && elem.childrenSize() == 0) {
            CFPost.Content text = new CFPost.Content(CFPost.Content.TYPE_TEXT);
            text.setText(elem.html());
            text.setAlignment(View.TEXT_ALIGNMENT_CENTER);
            post.contents.add(text);
        } else if (elem.tagName().equals("center") && elem.childrenSize() == 1 && elem.children().first().tagName().equals("a")) {
            CFPost.Content btn = new CFPost.Content(CFPost.Content.TYPE_BTN);
            btn.setButton(elem.children().first().attr("title"));
            btn.setButtonLink(elem.children().first().attr("href"));
            post.contents.add(btn);
        } else if (elem.tagName().equals("h2") || elem.tagName().equals("h3")) {
            CFPost.Content title = new CFPost.Content(CFPost.Content.TYPE_TITLE);
            title.setTitle(elem.html());
            post.contents.add(title);
        } else if (elem.tagName().equals("blockquote")) {
            CFPost.Content blockquote = new CFPost.Content(CFPost.Content.TYPE_QUOTE);
            blockquote.setQuote(elem.children().first().html());
            post.contents.add(blockquote);
        } else if (elem.tagName().equals("pre")) {
            CFPost.Content code = new CFPost.Content(CFPost.Content.TYPE_CODE);
            code.setCode(elem.children().first().text());
            post.contents.add(code);
        } else if (elem.tagName().equals("ul") || elem.tagName().equals("ol")) {
            CFPost.Content ul = new CFPost.Content(CFPost.Content.TYPE_LIST);
            ul.setList(elem.html());
            post.contents.add(ul);
        } else if (elem.tagName().equals("div") && elem.children().first().tagName().equals("iframe")) {
            CFPost.Content video = new CFPost.Content(CFPost.Content.TYPE_VIDEO);
            video.setVideo(elem.html());
            post.contents.add(video);
        } else if (elem.tagName().equals("table")) {
            CFPost.Content table = new CFPost.Content(CFPost.Content.TYPE_TABLE);
            table.setTable(elem.select("tbody").html());
            post.contents.add(table);
        } else {
            Log.d("CFPost", "parseElement: Not Handle: "+ elem.tagName());
        }

    }
}
