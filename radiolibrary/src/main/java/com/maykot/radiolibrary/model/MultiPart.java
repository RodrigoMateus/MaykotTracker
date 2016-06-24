package com.maykot.radiolibrary.model;

import java.io.Serializable;
import java.util.HashMap;

public class MultiPart implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Part> parts;
	
	/**
	 * Build resource Multipart
	 * @return
	 */
	public static MultiPart builder() {
		MultiPart muitlPart = new MultiPart();
		muitlPart.parts = new HashMap<>();
		return muitlPart;
	}
	
	/**
	 * Add an part body
	 * @param field
	 * @param part
	 * @return
	 */
	public MultiPart addBody(String field, Part part){
		parts.put(field, part);
		return this;
	}
	
	/**
	 * Add an part body
	 * @param field
	 * @param body
	 * @param contentType
	 * @return
	 */
	public MultiPart addBody(String field,String contentType, String body){
		parts.put(field, new Part(contentType, "", body.getBytes()));
		return this;
	}

	public HashMap<String, Part> getParts() {
		return parts;
	}

	public void setParts(HashMap<String, Part> parts) {
		this.parts = parts;
	}
	
}
