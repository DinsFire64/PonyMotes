package com.dinsfire.ponymotes;

public class Emote {

    private String emoteName;
    private String tags;
    private int dateModified;
    private String source;
    private boolean isNSFW;

    Emote(String emoteName, String tags, int isNSFW, int dateModified, String source) {
	this.emoteName = emoteName;
	this.tags = tags;
	this.dateModified = dateModified;
	this.source = source;

	if (isNSFW == 1)
	    this.isNSFW = true;
	else
	    this.isNSFW = false;
    }

    Emote(String emoteName) {
	this.emoteName = emoteName;
    }

    public String getEmoteName() {
	return emoteName;
    }

    public String getTags() {
	return tags;
    }

    public int getDateModified() {
	return dateModified;
    }

    public String getSource() {
	return source;
    }

    public int isIntNSFW() {
	if (isNSFW == true)
	    return 1;
	else
	    return 0;
    }

    public boolean isNSFW() {
	return isNSFW;
    }

    @Override
    public String toString() {
	return emoteName;
    }

}
