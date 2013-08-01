package org.wordpress.android.providers;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.wordpress.android.WordPress;
import org.wordpress.android.datasets.SQLTable;
import org.wordpress.android.datasets.StatsClicksTable;
import org.wordpress.android.datasets.StatsGeoviewsTable;
import org.wordpress.android.datasets.StatsMostCommentedTable;
import org.wordpress.android.datasets.StatsReferrersTable;
import org.wordpress.android.datasets.StatsSearchEngineTermsTable;
import org.wordpress.android.datasets.StatsTagsAndCategoriesTable;
import org.wordpress.android.datasets.StatsTopAuthorsTable;
import org.wordpress.android.datasets.StatsTopCommentersTable;
import org.wordpress.android.datasets.StatsTopPostsAndPagesTable;
import org.wordpress.android.datasets.StatsVideosTable;

public class StatsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "org.wordpress.android.providers.StatsContentProvider";

    public static final Uri STATS_CLICKS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.CLICKS);
    public static final Uri STATS_GEOVIEWS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.GEOVIEWS);
    public static final Uri STATS_MOST_COMMENTED_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.MOST_COMMENTED);
    public static final Uri STATS_REFERRERS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.REFERRERS);
    public static final Uri STATS_SEARCH_ENGINE_TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.SEARCH_ENGINE_TERMS);
    public static final Uri STATS_TAGS_AND_CATEGORIES_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.TAGS_AND_CATEGORIES);
    public static final Uri STATS_TOP_AUTHORS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.TOP_AUTHORS);
    public static final Uri STATS_TOP_COMMENTERS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.TOP_COMMENTERS);
    public static final Uri STATS_TOP_POSTS_AND_PAGES_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.TOP_POSTS_AND_PAGES);
    public static final Uri STATS_VIDEOS_URI = Uri.parse("content://" + AUTHORITY + "/" + Paths.VIDEOS);
    
    private static final class Paths {
        private static final String CLICKS = "clicks";
        private static final String GEOVIEWS = "geoviews";
        private static final String MOST_COMMENTED = "most_commented";
        private static final String REFERRERS = "referrers";
        private static final String SEARCH_ENGINE_TERMS = "search_engine_terms";
        private static final String TAGS_AND_CATEGORIES = "tags_and_categories";
        private static final String TOP_AUTHORS = "top_authors";
        private static final String TOP_COMMENTERS = "top_commenters";
        private static final String TOP_POSTS_AND_PAGES = "top_posts_and_pages";
        private static final String VIDEOS = "videos";
    }
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private int URI_MATCH = 0;
    private Map<Integer, SQLTable> mUriMatchToSQLTableMap;
    
    @Override
    public synchronized boolean onCreate() {
        mUriMatchToSQLTableMap = new HashMap<Integer, SQLTable>();
        
        registerTable(Paths.CLICKS, StatsClicksTable.getInstance());
        registerTable(Paths.GEOVIEWS, StatsGeoviewsTable.getInstance());
        registerTable(Paths.MOST_COMMENTED, StatsMostCommentedTable.getInstance());
        registerTable(Paths.REFERRERS, StatsReferrersTable.getInstance());
        registerTable(Paths.SEARCH_ENGINE_TERMS, StatsSearchEngineTermsTable.getInstance());
        registerTable(Paths.TAGS_AND_CATEGORIES, StatsTagsAndCategoriesTable.getInstance());
        registerTable(Paths.TOP_AUTHORS, StatsTopAuthorsTable.getInstance());
        registerTable(Paths.TOP_COMMENTERS, StatsTopCommentersTable.getInstance());
        registerTable(Paths.TOP_POSTS_AND_PAGES, StatsTopPostsAndPagesTable.getInstance());
        registerTable(Paths.VIDEOS, StatsVideosTable.getInstance());
        return false;
    }
    
    private void registerTable(String path, SQLTable table) {
        final int match = URI_MATCH ++;
        sUriMatcher.addURI(AUTHORITY, path, match);
        mUriMatchToSQLTableMap.put(match, table);
    }

    @Override
    public synchronized String getType(Uri uri) {
        return null;
    }
    
    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        
        SQLTable table = getSQLTable(uri);
        if (table != null) {
            return table.delete(getDB(), uri, selection, selectionArgs);
        }
        
        return 0;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        SQLTable table = getSQLTable(uri);
        if (table != null) {
            long rowId = table.insert(getDB(), uri, values);
            return Uri.parse(uri + "/" + rowId);
        }
        
        return null;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLTable table = getSQLTable(uri);
        if (table != null) {
            return table.query(getDB(), uri, projection, selection, selectionArgs, sortOrder);
        }
        
        return null;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLTable table = getSQLTable(uri);
        if (table != null) {
            return table.update(getDB(), uri, values, selection, selectionArgs);
        }
        
        return 0;
    }
    
    private synchronized SQLTable getSQLTable(Uri uri) {
        int uriMatch = sUriMatcher.match(uri);
        if (mUriMatchToSQLTableMap.containsKey(uriMatch))
                return mUriMatchToSQLTableMap.get(uriMatch);
        
        return null;
    }
    
    private synchronized SQLiteDatabase getDB() {
        return WordPress.wpStatsDB.getWritableDatabase();
    }

}