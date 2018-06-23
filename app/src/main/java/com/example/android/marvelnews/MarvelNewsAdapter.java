package com.example.android.marvelnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link MarvelNewsAdapter} knows how to create a list item layout for each marvelNews
 * in the data source (a list of {@link MarvelNews} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class MarvelNewsAdapter extends ArrayAdapter<MarvelNews> {

    /**
     * Constructs a new {@link MarvelNewsAdapter}.
     *
     * @param context     of the app
     * @param marvelNewss is the list of marvelNews, which is the data source of the adapter
     */
    public MarvelNewsAdapter(Context context, List<MarvelNews> marvelNewss) {
        super(context, 0, marvelNewss);
    }

    /**
     * Returns a list item view that displays information about the marvelNews at the given position
     * in the list of marvelNewss.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the marvelNews at the given position in the list of marvelNewss
        MarvelNews currentMarvelNews = getItem(position);

        // Find the TextView with view ID news title
        TextView titleView = (TextView) listItemView.findViewById(R.id.news_title);
        String title = currentMarvelNews.getTitle();
        // Display the title of the current marvelNews in that TextView
        titleView.setText(title);

        // Find the TextView with view ID author_name
        TextView authorView = listItemView.findViewById(R.id.author_name);
        // Display the author name of the current news in that TextView
        if (currentMarvelNews.getAuthor() != "") {
            authorView.setText(currentMarvelNews.getAuthor());

            //Set author name view as visible
            authorView.setVisibility(View.VISIBLE);
        } else {
            //Set author name view as gone
            authorView.setVisibility(View.GONE);
        }

        // Find the TextView with view ID section name
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_name);
        String sectionName = currentMarvelNews.getSectionName();
        // Display the section name of the current marvelNews in that TextView
        sectionView.setText(sectionName);

        TextView dateView = null;
        TextView timeView = null;
        if (currentMarvelNews.getTime() != null) {
            // Find the TextView with view ID date
            dateView = listItemView.findViewById(R.id.date);
            // Format the date string (i.e. "May 3, 1987")
            String formattedDate = formatDate(currentMarvelNews.getTime()).concat(",");
            // Display the date of the current MarvelNews in that TextView
            dateView.setText(formattedDate);

            // Find the TextView with view ID time
            timeView = listItemView.findViewById(R.id.time);
            // Format the time string (i.e. "6:45 PM")
            String formattedTime = formatTime(currentMarvelNews.getTime());
            // Display the time of the current MarvelNews in that TextView
            timeView.setText(formattedTime);

            //Set date & time views as visible
            dateView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
        } else {
            //Set date & time views as gone
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "May 3, 1987") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "6:45 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}