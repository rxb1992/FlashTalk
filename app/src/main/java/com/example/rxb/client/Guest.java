package com.example.rxb.client;

import java.io.Serializable;

public class Guest implements Serializable {
	private String guestId;
	private String guestName;
	private String guestPic;
	private String guestToken;

	public Guest(){}

	public Guest(String guestId,String guestName,String guestToken) {
		this.guestId = guestId;
		this.guestName = guestName;
		this.guestToken = guestToken;
	}

	public Guest(String guestId,String guestName,String guestToken,String pic) {
		this.guestId = guestId;
		this.guestName = guestName;
		this.guestToken = guestToken;
		this.guestPic = pic;
	}

	public String getGuestId() {
		return guestId;
	}
	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}
	public String getGuestName() {
		return guestName;
	}
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}
	public void setGuestToken(String guestToken) {
		this.guestToken = guestToken;
	}
	public String getGuestToken() {
		return guestToken;
	}
	public void setGuestPic(String guestPic) {
		this.guestPic = guestPic;
	}
	public String getGuestPic() {
		return guestPic;
	}

	public boolean equals(Object obj) {
		
		if(obj instanceof Guest) {
			Guest g = (Guest)obj;
			if(this.getGuestId() == g.getGuestId() && this.getGuestName().equals(g.getGuestName())) {
				return true;
			}
		}
		return false;
	}

}
