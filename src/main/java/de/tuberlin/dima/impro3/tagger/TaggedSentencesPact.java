package de.tuberlin.dima.impro3.tagger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import eu.stratosphere.pact.common.type.Value;

	/**
	 *  This class represents a tagged sentence.
	 */
	public class TaggedSentencesPact implements Value {
		
		/* tokens array of a sentence */ 
		private String[] tokens;
		
		/* tags array of a sentence */
		private String[] tags;
		
		/* named entities of a sentence */
		private String[] ne;
		
		private TaggedSentence taggedSentence;
		
		public TaggedSentencesPact() {
			super();
		}
		
		public TaggedSentencesPact(TaggedSentence taggedSentence){
			this.taggedSentence = taggedSentence;
		}

		public TaggedSentencesPact(String [] tokens, String [] tags, String [] ne){
			this.tokens = tokens;
			this.tags = tags;
			this.ne = ne;
		}
		
		@Override
		public void read(DataInput in) throws IOException {
			
			int tokenLength = in.readInt();
			int tagsLength = in.readInt();
			int neLength = in.readInt();
			
			tokens = new String[tokenLength];
			tags = new String[tagsLength];
			ne = new String [neLength];
			
			for (int i = 0; i < tokenLength; i++) {
				tokens[i] = in.readUTF();
			}
			for (int j = 0; j < tagsLength; j++) {
				tags[j] = in.readUTF();
			}
			for (int k = 0; k < neLength; k++) {
				ne[k] = in.readUTF();
			}
			
			taggedSentence.setTokens(tokens);
			taggedSentence.setTags(tags);
			taggedSentence.setNe(ne);
		}

		@Override
		public void write(DataOutput out) throws IOException {
			
			int tokenLength = taggedSentence.getTokens().length;
			int tagsLength = taggedSentence.getTags().length;
			int neLength = taggedSentence.getNe().length;
			
			out.write(tokenLength);
			out.write(tagsLength);
			out.write(neLength);
			
			for (int i = 0; i < tokenLength; i++) {
				out.writeUTF(taggedSentence.getTokens()[i]);
			
			}
			for (int j = 0; j < tagsLength; j++) {
				out.writeUTF(taggedSentence.getTags()[j]);
			}
			for (int k = 0; k < neLength; k++) {
				out.writeUTF(taggedSentence.getNe()[k]);
			}
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
		
		public TaggedSentence getTaggedSentence() {
			return taggedSentence;
		}

		public void setTaggedSentence(TaggedSentence taggedSentence) {
			this.taggedSentence = taggedSentence;
		}

		@Override
		public String toString() {
			return super.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(ne);
			result = prime
					* result
					+ ((taggedSentence == null) ? 0 : taggedSentence.hashCode());
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
			TaggedSentencesPact other = (TaggedSentencesPact) obj;
			if (!Arrays.equals(ne, other.ne))
				return false;
			if (taggedSentence == null) {
				if (other.taggedSentence != null)
					return false;
			} else if (!taggedSentence.equals(other.taggedSentence))
				return false;
			if (!Arrays.equals(tags, other.tags))
				return false;
			if (!Arrays.equals(tokens, other.tokens))
				return false;
			return true;
		}
		
	}