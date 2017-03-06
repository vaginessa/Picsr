package picsr.photo.search.flickr;

/**
 * Created by ANDROID on 3/2/2016.
 */
public class Constants {

    //traditional way of holding constants

    public static final String FLICKR_API_KEY = "d4cfac4a5c1f882569451abc41e92a06";

    public static final String URL = "https://api.flickr.com/services/rest?method=flickr.photos.search&api_key=" +FLICKR_API_KEY +"&text=keyword&format=json&nojsoncallback=1";

    public static final String URL_RECENT_UPLOADS = "https://api.flickr.com/services/rest?method=flickr.photos.getRecent&api_key=" +FLICKR_API_KEY +"&format=json&nojsoncallback=1";


}
