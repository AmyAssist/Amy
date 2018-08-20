package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes;

/**
 * Short Wildcard node with max wordlength
 * 
 * @author Felix Burk
 *
 */
public class ShortWNode extends AGFNode {
	
	/**
	 * fixed max word length
	 */
	final int maxWordLength = 5;

	/**
	 * constructor 
	 * @param content empty here
	 */
	public ShortWNode(String content) {
		super(content);
	}
	
	/**
	 * returns the node type
	 * 
	 * @return the type
	 */
	@Override
	public AGFNodeType getType() {
		return AGFNodeType.SHORTWC;
	}
	

	/**
	 * @return the maxWordLength
	 */
	public int getMaxWordLength() {
		return this.maxWordLength;
	}


}
