package btree;

import global.PageId;
import global.RID;
import diskmgr.Page;

public class BTIndexPage extends BTSortedPage {

	/**
	 * 
	 * @param type
	 * @throws ConstructPageException
	 */
	public BTIndexPage(int type) throws ConstructPageException {
		super(type);
		// TODO Auto-generated constructor stub
	}

	/**
	 * pin the page with pageno, and get the corresponding BTIndexPage, also it
	 * sets the type of node to be NodeType.INDEX.
	 * 
	 * @param pg
	 * @param type: either AttrType.attrString or AttrType.attrInteger
	 * @throws ConstructPageException
	 */
	public BTIndexPage(Page pg, int type) throws ConstructPageException {
		super(type);
	}

	/**
	 * pin the page with pageno, and get the corresponding BTIndexPage, also it
	 * sets the type of node to be NodeType.INDEX.
	 * 
	 * @param pgid
	 * @param type
	 *            : either AttrType.attrString or AttrType.attrInteger
	 * @throws ConstructPageException
	 */
	public BTIndexPage(PageId pgid, int type) throws ConstructPageException {
		super(type);
	}
	
	public RID insertKey(KeyClass key, PageId pageNo){
		
		return null;
	}
	
	public PageId getPageNoByKey(KeyClass key){
		
		return null;
	}
	
	public KeyDataEntry getFirst(RID rid){
		
		return null;
	}
	
	public KeyDataEntry getNext(RID rid){
		
		return null;
	}
	
	public PageId getLeftLink(){
		
		return null;
	}
	
	public void setLeftLink(PageId left){
		
	}
}
