package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes;

/**
 * Wildcard node
 * 
 * @author Felix Burk
 *
 */
public class LongWNode extends AGFNode {

	/**
	 * constructor
	 * @param content of the node
	 */
	public LongWNode(String content) {
		super(content);
	}
	
	/**
	 * returns the node type
	 * 
	 * @return the type
	 */
	@Override
	public AGFNodeType getType() {
		return AGFNodeType.LONGWC;
	}
	
	/**
	 * special print self method, because this node may never have children
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode#printSelf(java.lang.String, int)
	 */
	@Override
	public String printSelf(String name, int indent) {
		StringBuilder b = new StringBuilder();
		b.append("+" + name);
		b.append("\n");
		
		return b.toString();
	}

}
