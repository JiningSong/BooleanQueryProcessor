package com.vruiz.invertedindex.document;

import java.util.HashMap;

/**
 * Represents a single document contained in the index
 */
public class Document {

	/**
	 * unique identifier for the document in the index
	 * String representation of doc_id+sentence_id+token_id
	 */
	private String tokenIdentifier;

	/**
	 * A document is composed of Fields. Every field is identified by its name (String)
	 */
	private final HashMap<String, Field> fields = new HashMap<>();


	public Document(String doc_id, String sentence_id, String token_id, String token_text) {
		this.setTokenIdentifier(String.format("%s|%s|%s", doc_id, sentence_id, token_id));
	}

	public Document() {
		this.setTokenIdentifier("");
	}

	public HashMap<String, Field> fields() {
		return this.fields;
	}

	public void addField(final Field field) {
		this.fields.put(field.name(), field);
	}

	public String getTokenIdentifier() {
		return tokenIdentifier;
	}

	public void setTokenIdentifier(String tokenIdentifier) {
		this.tokenIdentifier = tokenIdentifier;
	}
}
