package edu.tarleton.welborn.webchess;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GameUpdateData {
    public String screenName;

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}