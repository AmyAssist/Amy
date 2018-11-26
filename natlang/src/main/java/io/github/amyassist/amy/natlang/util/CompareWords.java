/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package io.github.amyassist.amy.natlang.util;

/**
 * Helper class to compare words 
 * different approaches are possible here. 
 * I choose the Damerau-Levenshtein distance as it is pretty straight forward
 * and a distance of one will match about 80% of human spelling mistakes 
 * according to the original paper of damerau
 * 
 * @author Felix Burk
 */
public class CompareWords {
	
	private CompareWords() {
		//hide constructor
	}
	
	/**
	 * calculates word distance
	 * 
	 * @param source charsequence to compare
	 * @param target second charsequence to compare
	 * @return the word distance
	 */
	public static int wordDistance(CharSequence source, CharSequence target) {
		if(source == null || target == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		return damerauLevenshtein(source, target);
		
	}

	/**
	 * helper method to prevent expensive comparisons
	 * 
	 * @param source charsequence to compare
	 * @param target second charsequence to compare
	 * @param comparison distance to compare it to
	 * @return is the word distance bigger than one?
	 */
	public static boolean isDistanceBigger(String source, String target, int comparison) {
		if (Math.abs(source.length() - target.length()) > comparison)
			return true;
		return wordDistance(source, target) > comparison;
	}
	
	
	/**
	 * calculates distance of two words based on 
	 * Damerau-Levenshtein Distance.
	 * 
	 * note that right now this only calculates the Optimal string alignment distance
	 * {@link "https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance#Optimal_string_alignment_distance"}}
	 * 
	 * a better approach would be calculating the distance with adjacent transpositions 
	 * i might add that in the future
	 * 
	 * lots of optimization can still be done see
	 * {@link "https://github.com/apache/commons-text/blob/f945dd26de832ee6bf925be13bda929932e301f1/src/main/java/org/apache/commons/text/similarity/LevenshteinDistance.java"}}
	 * 
	 * but the current implementation should be fine for now
	 * 
	 * @param source charsequence to compare
	 * @param target second charsequence to compare
	 * @return the word distance
	 */
	private static int damerauLevenshtein(CharSequence source, CharSequence target) {
		int lengthSource = source.length();
		int lengthTarget = target.length();
		
		if(lengthSource == 0) {
			return lengthTarget;
		}else if(lengthTarget == 0) {
			return lengthSource;
		}

		int[][] matrix = new int[lengthSource+1][lengthTarget+1];
		
		int i;
		int j;
		int cost;
		int transposition;
		int substitution;
		int insertion;
		int deletion;
		
		for(i = 0; i <= lengthSource; i++) {
			matrix[i][0] = i;
		}
		for(j = 0; j <= lengthTarget; j++) {
			matrix[0][j] = j;
		}
		
		for(i = 1; i <= lengthSource; i++) {
			for(j = 1; j <= lengthTarget; j++) {
				cost = source.charAt(i-1) == target.charAt(j-1) ? 0 : 1;
				
				substitution = matrix[i-1][j-1] + cost;
				insertion = matrix[i][j-1] + 1;
				deletion = matrix[i-1][j] + 1;
				
				matrix[i][j] = Math.min(Math.min(substitution, insertion), deletion);
				
				if(i > 1 && j > 1 && source.charAt(i-1) == target.charAt(j-2) && source.charAt(i-2) == target.charAt(j-1)) {
					transposition = matrix[i-2][j-2] + cost;
					matrix[i][j] = Math.min(matrix[i][j], transposition);
				}
				
			}
		}
		
		return matrix[lengthSource][lengthTarget];
		
	}

}
