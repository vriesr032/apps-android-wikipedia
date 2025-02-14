package org.wikipedia.page;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.language.AppLanguageLookUpTable;
import org.wikipedia.settings.SiteInfoClient;
import org.wikipedia.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;

import static org.wikipedia.util.UriUtil.decodeURL;

/**
 * Represents certain vital information about a page, including the title, namespace,
 * and fragment (section anchor target).  It can also contain a thumbnail URL for the
 * page, and a short description retrieved from Wikidata.
 *
 * WARNING: This class is not immutable! Specifically, the thumbnail URL and the Wikidata
 * description can be altered after construction. Therefore do NOT rely on all the fields
 * of a PageTitle to remain constant for the lifetime of the object.
 */
public class PageTitle implements Parcelable {

    public static final Parcelable.Creator<PageTitle> CREATOR
            = new Parcelable.Creator<PageTitle>() {
        @Override
        public PageTitle createFromParcel(Parcel in) {
            return new PageTitle(in);
        }

        @Override
        public PageTitle[] newArray(int size) {
            return new PageTitle[size];
        }
    };

    /**
     * The localised namespace of the page as a string, or null if the page is in mainspace.
     *
     * This field contains the prefix of the page's title, as opposed to the namespace ID used by
     * MediaWiki. Therefore, mainspace pages always have a null namespace, as they have no prefix,
     * and the namespace of a page will depend on the language of the wiki the user is currently
     * looking at.
     *
     * Examples:
     * * [[Manchester]] on enwiki will have a namespace of null
     * * [[Deutschland]] on dewiki will have a namespace of null
     * * [[User:Deskana]] on enwiki will have a namespace of "User"
     * * [[Utilisateur:Deskana]] on frwiki will have a namespace of "Utilisateur", even if you got
     *   to the page by going to [[User:Deskana]] and having MediaWiki automatically redirect you.
     */
    @Nullable private final String namespace;
    @NonNull private String text;
    @Nullable private final String fragment;
    @Nullable private String thumbUrl;
    @SerializedName("site") @NonNull private final WikiSite wiki;
    @Nullable private String description;
    @Nullable private final PageProperties properties;
    @Nullable private String displayText;

    /**
     * Creates a new PageTitle object.
     * Use this if you want to pass in a fragment portion separately from the title.
     *
     * @param prefixedText title of the page with optional namespace prefix
     * @param fragment optional fragment portion
     * @param wiki the wiki site the page belongs to
     * @return a new PageTitle object matching the given input parameters
     */
    public static PageTitle withSeparateFragment(@NonNull String prefixedText,
                                                 @Nullable String fragment, @NonNull WikiSite wiki) {
        if (TextUtils.isEmpty(fragment)) {
            return new PageTitle(prefixedText, wiki, null, (PageProperties) null);
        } else {
            return new PageTitle(prefixedText + "#" + fragment, wiki, null, (PageProperties) null);
        }
    }

    public PageTitle(@Nullable final String namespace, @NonNull String text, @Nullable String fragment, @Nullable String thumbUrl, @NonNull WikiSite wiki) {
        this.namespace = namespace;
        this.text = text;
        this.fragment = fragment;
        this.wiki = wiki;
        this.thumbUrl = thumbUrl;
        properties = null;
    }

    public PageTitle(@Nullable String text, @NonNull WikiSite wiki, @Nullable String thumbUrl, @Nullable String description, @Nullable PageProperties properties) throws NullPointerException {
        this(text, wiki, thumbUrl, properties);
        this.description = description;
    }

    public PageTitle(@Nullable String text, @NonNull WikiSite wiki, @Nullable String thumbUrl, @Nullable String description, @Nullable String displayText) {
        this(text, wiki, thumbUrl, description);
        this.displayText = displayText;
    }

    public PageTitle(@Nullable String text, @NonNull WikiSite wiki, @Nullable String thumbUrl, @Nullable String description) {
        this(text, wiki, thumbUrl);
        this.description = description;
    }

    public PageTitle(@Nullable String namespace, @NonNull String text, @NonNull WikiSite wiki) {
        this(namespace, text, null, null, wiki);
    }

    public PageTitle(@Nullable String text, @NonNull WikiSite wiki, @Nullable String thumbUrl) {
        this(text, wiki, thumbUrl, (PageProperties) null);
    }

    public PageTitle(@Nullable String text, @NonNull WikiSite wiki) {
        this(text, wiki, null);
    }

    private PageTitle(@Nullable String text, @NonNull WikiSite wiki, @Nullable String thumbUrl,
                      @Nullable PageProperties properties) throws NullPointerException {
        if (TextUtils.isEmpty(text)) {
            // If empty, this refers to the main page.
            text = SiteInfoClient.getMainPageForLang(wiki.languageCode());
        }

        // Remove any URL parameters (?...) from the title
        String[] parts;
        try {
            parts = text.split("\\?", -1);
        } catch (NullPointerException e){
            throw new NullPointerException(e.getLocalizedMessage());
        }
        if (parts.length > 1 && parts[1].contains("=")) {
            text = parts[0];
        }

        // Split off any fragment (#...) from the title
        parts = text.split("#", -1);
        text = parts[0];
        if (parts.length > 1) {
            this.fragment = decodeURL(parts[1]).replace(" ", "_");
        } else {
            this.fragment = null;
        }

        parts = text.split(":", -1);
        if (parts.length > 1) {
            String namespaceOrLanguage = parts[0];
            if (Arrays.asList(Locale.getISOLanguages()).contains(namespaceOrLanguage)) {
                this.namespace = null;
                this.wiki = new WikiSite(wiki.authority(), namespaceOrLanguage);
            } else {
                this.wiki = wiki;
                this.namespace = namespaceOrLanguage;
            }
            this.text = TextUtils.join(":", Arrays.copyOfRange(parts, 1, parts.length));
        } else {
            this.wiki = wiki;
            this.namespace = null;
            this.text = parts[0];
        }

        this.thumbUrl = thumbUrl;
        this.properties = properties;
    }

    @Nullable
    public String getNamespace() {
        return namespace;
    }

    @NonNull public Namespace namespace() {
        if (properties != null) {
            return properties.getNamespace();
        }

        // Properties has the accurate namespace but it doesn't exist. Guess based on title.
        return Namespace.fromLegacyString(wiki, namespace);
    }

    @NonNull public WikiSite getWikiSite() {
        return wiki;
    }

    @NonNull public String getText() {
        return text.replace(" ", "_");
    }

    @Nullable public String getFragment() {
        return fragment;
    }

    @Nullable public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(@Nullable String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @Nullable public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    // This update the text to the API text.
    public void setText(@NonNull String convertedFromText) {
        this.text = convertedFromText;
    }

    @NonNull public String getDisplayText() {
        return displayText == null ? getPrefixedText().replace("_", " ") : displayText;
    }

    public void setDisplayText(@Nullable String displayText) {
        this.displayText = displayText;
    }

    @Nullable public PageProperties getProperties() {
        return properties;
    }

    public boolean isMainPage() {
        if (properties != null) {
            return properties.isMainPage();
        }
        String mainPageTitle = SiteInfoClient.getMainPageForLang(getWikiSite().languageCode());
        return mainPageTitle.equals(getDisplayText());
    }

    public String getUri() {
        return getUriForDomain(getWikiSite().authority());
    }

    public String getUriForAction(String action) {
        try {
            return String.format(
                    "%1$s://%2$s/w/index.php?title=%3$s&action=%4$s",
                    getWikiSite().scheme(),
                    getWikiSite().authority(),
                    URLEncoder.encode(getPrefixedText(), "utf-8"),
                    action
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPrefixedText() {
        return namespace == null ? getText() : StringUtil.addUnderscores(namespace) + ":" + getText();
    }

    /**
     * Check if the Title represents a File:
     *
     * @return true if it is a File page, false if not
     */
    public boolean isFilePage() {
        return namespace().file();
    }

    /**
     * Check if the Title represents a special page
     *
     * @return true if it is a special page, false if not
     */
    public boolean isSpecial() {
        return namespace().special();
    }

    /**
     * Check if the Title represents a talk page
     *
     * @return true if it is a talk page, false if not
     */
    public boolean isTalkPage() {
        return namespace().talk();
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(namespace);
        parcel.writeString(text);
        parcel.writeString(fragment);
        parcel.writeParcelable(wiki, flags);
        parcel.writeParcelable(properties, flags);
        parcel.writeString(thumbUrl);
        parcel.writeString(description);
        parcel.writeString(displayText);
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PageTitle)) {
            return false;
        }

        PageTitle other = (PageTitle)o;
        // Not using namespace directly since that can be null
        return StringUtil.normalizedEquals(other.getPrefixedText(), getPrefixedText()) && other.wiki.equals(wiki);
    }

    @Override public int hashCode() {
        int result = getPrefixedText().hashCode();
        result = 31 * result + wiki.hashCode();
        return result;
    }

    @Override public String toString() {
        return getPrefixedText();
    }

    @Override public int describeContents() {
        return 0;
    }

    private String getUriForDomain(String domain) {
        try {
            return String.format(
                    "%1$s://%2$s/%3$s/%4$s%5$s",
                    getWikiSite().scheme(),
                    domain,
                    domain.startsWith(AppLanguageLookUpTable.CHINESE_LANGUAGE_CODE) ? getWikiSite().languageCode() : "wiki",
                    URLEncoder.encode(getPrefixedText(), "utf-8"),
                    (this.fragment != null && this.fragment.length() > 0) ? ("#" + this.fragment) : ""
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private PageTitle(Parcel in) {
        namespace = in.readString();
        text = in.readString();
        fragment = in.readString();
        wiki = in.readParcelable(WikiSite.class.getClassLoader());
        properties = in.readParcelable(PageProperties.class.getClassLoader());
        thumbUrl = in.readString();
        description = in.readString();
        displayText = in.readString();
    }
}
