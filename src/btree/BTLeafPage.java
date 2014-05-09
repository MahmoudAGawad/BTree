package btree;

import global.*;
import diskmgr.*;

public class BTLeafPage extends BTSortedPage {

	/**
	 * 
	 * @param type
	 *            : NodeType.LEAF
	 * @throws ConstructPageException
	 */
	public BTLeafPage(int type) throws ConstructPageException {
		super(type);
	}

	/**
	 * associate the BTIndexPage instance with the Page instance, also it sets
	 * the type of node to be NodeType.LEAF.
	 * 
	 * @param pg
	 * @param type
	 *            : either AttrType.attrInteger or AttrType.attrString. Input
	 *            parameter.
	 * @throws ConstructPageException
	 */
	public BTLeafPage(Page pg, int type) throws ConstructPageException {
		super(type);
	}

	/**
	 * pin the page with pgid, and get the corresponding BTIndexPage, also it
	 * sets the type of node to be NodeType.LEAF.
	 * 
	 * @param pgid
	 * @param type
	 *            : either AttrType.attrInteger or AttrType.attrString. Input
	 *            parameter.
	 * @throws ConstructPageException
	 */
	public BTLeafPage(PageId pgid, int type) throws ConstructPageException {
		super(type);
	}
	
	
	
	public RID insertRecord(KeyClass key,
            RID dataRid){
		
		return null;
	}

	public KeyDataEntry getFirst(RID rid) {

		return null;
	}

	public KeyDataEntry getNext(RID rid) {

		return null;
	}

	public KeyDataEntry getCurrent(RID rid){
		
		return null;
	}
	
	public boolean delEntry(KeyDataEntry dEntry){
		
		return false;
	}
}
