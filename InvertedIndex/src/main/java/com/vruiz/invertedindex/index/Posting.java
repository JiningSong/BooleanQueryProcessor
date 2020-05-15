package com.vruiz.invertedindex.index;


/**
 * A Posting represents an occurrence of a Term inside a document:
 */
public class Posting {

	/**
	 * token where the term occurs
	 */
	private String tokenIdentifier;

	/**
	 * number of times that the term occurs in the document
	 */
	private short termFrequency;

	/**
	 * @param documentId documentId
	 * @param termFrequency term frequency
	 */
	public Posting(final String tokenIdentifier, short termFrequency) {
		this.tokenIdentifier = tokenIdentifier;
		this.termFrequency = termFrequency;
	}

	public Posting(final String tokenIdentifier) {
		this(tokenIdentifier, (short)0);
	}

	public String getIdentifier() {
		return tokenIdentifier;
	}

	public int getTermFrequency() {
		return termFrequency;
	}

	public void addOccurrence() {
		this.termFrequency++;
	}

	public String toString() {
		return String.format("%s,%d", this.tokenIdentifier, this.termFrequency);
	}
}
