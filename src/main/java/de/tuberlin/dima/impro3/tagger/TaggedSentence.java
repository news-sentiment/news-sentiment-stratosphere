package de.tuberlin.dima.impro3.tagger;

import java.util.Arrays;

/**
 * This class represents a tagged sentence.
 */
public class TaggedSentence {
	
	/* tokens array of a sentence */ 
	private String[] tokens;
	
	/* tags array of a sentence */
	private String[] tags;
	
	/* named entities of a sentence */
	private String[] ne;
	
	public TaggedSentence(String[] tokens, String[] tags, String[] ne) {
		this.tokens = tokens;
		this.tags = tags;
		this.ne = ne;
	}
	
	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String[] getNe() {
		return ne;
	}

	public void setNe(String[] ne) {
		this.ne = ne;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ne);
		result = prime * result + Arrays.hashCode(tags);
		result = prime * result + Arrays.hashCode(tokens);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaggedSentence other = (TaggedSentence) obj;
		if (!Arrays.equals(ne, other.ne))
			return false;
		if (!Arrays.equals(tags, other.tags))
			return false;
		if (!Arrays.equals(tokens, other.tokens))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaggedSentence [tokens=" + Arrays.toString(tokens) + ", tags="
				+ Arrays.toString(tags) + ", ne=" + Arrays.toString(ne) + "]";
	}

}
