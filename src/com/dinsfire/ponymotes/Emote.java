package com.dinsfire.ponymotes;

public class Emote {
	
	private String emoteName;
	private String tags;
	private int dateModified;
	private String source;
	private int isNSFW;
	
	Emote(String emoteName, String tags, int isNSFW, int dateModified, String source) {
		this.emoteName = emoteName;
		this.tags = tags;
		this.dateModified = dateModified;
		this.source = source;
		this.isNSFW = isNSFW;
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
	
	public boolean isNSFW() {
		if(isNSFW == 1)
			return true;
		else
			return false;
	}
	
	public int isIntNSFW() {
		return isNSFW;
	}
	
	@Override 
	public String toString() {
	    return emoteName;
	  }

}
