package com.example.android.marvelnews;

import java.util.Date;

/**
 * An {@link MarvelNews} object contains information related to a single MarvelNews.
 */
public class MarvelNews {

    /**
     * Author of the MarvelNews
     */
    private String mAuthor;

    /**
     * Title of the MarvelNews
     */
    private String mTitle;

    /**
     * SectionName of the MarvelNews
     */
    private String mSectionName;

    /**
     * Publication Date of the MarvelNews
     */
    private Date mTime;

    /**
     * Website URL of the MarvelNews
     */
    private String mUrl;

    /**
     * Constructs a new {@link MarvelNews} object.
     *
     * @param author      is the author of the MarvelNews
     * @param title       is the title of the MarvelNews
     * @param sectionName is the section of the MarvelNews
     * @param time        is the date the MarvelNews was published
     * @param url         is the website URL to find more details about the MarvelNews
     */
    public MarvelNews(String author, String title, String sectionName, Date time, String url) {
        mAuthor = author;
        mTitle = title;
        mSectionName = sectionName;
        mTime = time;
        mUrl = url;
    }

    /**
     * Returns the author of the MarvelNews.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the title of the MarvelNews.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the section of the MarvelNews.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the publication date of the MarvelNews.
     */
    public Date getTime() {
        return mTime;
    }

    /**
     * Returns the website URL to find more information about the MarvelNews.
     */
    public String getUrl() {
        return mUrl;
    }
}