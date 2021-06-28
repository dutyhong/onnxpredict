package biz.dto;

/**
 * @author tizi
 */
public class TokenNerTag {
    public String token = null;
    public String tag = null;

    public TokenNerTag(String token, String tag) {
        this.token = token;
        this.tag = tag;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
