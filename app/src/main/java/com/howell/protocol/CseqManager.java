package com.howell.protocol;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class CseqManager {
	private int cseq;

	public void setCseq(int cseq) {
		this.cseq = cseq;
	}
	
	public int getCseq(){
		return cseq++;
	}
}
