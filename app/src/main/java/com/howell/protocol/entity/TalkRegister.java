package com.howell.protocol.entity;

public class TalkRegister {
	public static class Res{
		int result;
		int interval;
		String id;
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Res(int result, int interval, String id) {
			super();
			this.result = result;
			this.interval = interval;
			this.id = id;
		}
	}
	
	
	
	
}
