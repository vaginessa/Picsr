package picsr.photo.search.flickr;

/**
 * Created by ANDROID on 3/2/2016.
 */
public class DataModel {

    //POJO class (encapsulation) that holds data and processes data to construct pictures url

    private String id;
    private String secret;
    private String server;
    private String farm;
    private String title;
    private String constructedURL;

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    private boolean flipped;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String constructURL() {

        if( constructedURL == null ) {

            constructedURL =  "https://farm" +
                    farm +
                    ".staticflickr.com/" +
                    server +
                    "/" +
                    id +
                    "_" +
                    secret +
                    ".jpg";
        }

        return constructedURL;
    }

}
