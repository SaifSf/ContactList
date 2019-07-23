package com.example.contact;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sectionrecycler.SectionParameters;
import com.example.sectionrecycler.SectionedRecyclerViewAdapter;
import com.example.sectionrecycler.StatelessSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SectionFragment extends BaseFragment {

    private SectionedRecyclerViewAdapter sectionAdapter;
    private List<String> contacts = new ArrayList<>();

    public SectionFragment() {
    }

    public static SectionFragment newInstance() {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_section, container, false);

        sectionAdapter = new SectionedRecyclerViewAdapter();
        contacts = getContactNames();
        if (contacts.size() > 0) {
            sectionAdapter.addSection(new NewsSection(NewsSection.WORLD));
            sectionAdapter.addSection(new NewsSection(NewsSection.BUSINESS));

        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContext().getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contacts.add(name);
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();
        Log.d("contactlist", "getContactNames: " + contacts.size());

        return contacts;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private class NewsSection extends StatelessSection {

        final static int WORLD = 0;
        final static int BUSINESS = 1;
        final static int TECHNOLOGY = 2;
        final static int SPORTS = 3;

        String title;
        List<String> list;
        int imgPlaceholderResId;
        boolean expanded = true;


        NewsSection(int topic) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.section_ex4_item)
                    .headerResourceId(R.layout.section_ex4_header)
                    .build());

            switch (topic) {
                case WORLD:
                    this.title = getString(R.string.world_topic);
                    this.list = getNews(R.array.news_biz);

                    this.imgPlaceholderResId = R.drawable.ic_public_black_48dp;
                    break;
                case BUSINESS:
                    this.title = getString(R.string.biz_topic);
                    if (contacts.size() > 0) {
                        this.list = contacts;
                    } else {
                        this.list = getNews(R.array.news_biz);
                    }
                    this.imgPlaceholderResId = R.drawable.ic_business_black_48dp;
                    break;
//                case TECHNOLOGY:
//                    this.title = getString(R.string.tech_topic);
//                    this.list = getNews(R.array.news_tech);
//                    this.imgPlaceholderResId = R.drawable.ic_devices_other_black_48dp;
//                    break;
//                case SPORTS:
//                    this.title = getString(R.string.sports_topic);
//                    this.list = getNews(R.array.news_sports);
//                    this.imgPlaceholderResId = R.drawable.ic_directions_run_black_48dp;
//                    break;
            }
        }

        private List<String> getNews(int arrayResource) {
            return new ArrayList<>(Arrays.asList(getResources().getStringArray(arrayResource)));
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            String name = list.get(position);

            itemHolder.tvHeader.setText(name);
            itemHolder.imgItem.setImageResource(name.hashCode() % 2 == 0 ? R.drawable.ic_face_black_48dp : R.drawable.ic_tag_faces_black_48dp);

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),
                            String.format("Clicked on position #%s of Section %s",
                                    sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()),
                                    title),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvTitle.setText(title);



        }




    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);



        }
    }



    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView imgItem;
        private final TextView tvHeader;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            imgItem = view.findViewById(R.id.imgItem);
            tvHeader = view.findViewById(R.id.tvItem);
        }
    }


    @Override
    protected void setUp(View view) {

    }
}
