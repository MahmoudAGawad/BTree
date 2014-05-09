package btree;

import diskmgr.Page;
import global.PageId;
import heap.HFPage;

public class BTreeHeaderPage {
	private int keytype, keysize, deleteFashion;
	private PageId headerPageId, rootPageId;
	private String treeName;
	private HFPage header;

	public BTreeHeaderPage(int keytype, int keysize, PageId hpgid,
			int deletefashion, String name, HFPage pg, PageId root) {
		this.headerPageId = hpgid;
		this.keysize = keysize;
		this.keytype = keytype;
		deleteFashion = deletefashion;
		treeName = name;
		header = pg;
		rootPageId=root;
	}

	public void setHeaderPageId(PageId headerPageId) {
		this.headerPageId = headerPageId;
	}

	public void setKeysize(int keysize) {
		this.keysize = keysize;
	}

	public void setKeytype(int keytype) {
		this.keytype = keytype;
	}

	public void setDeleteFashion(int deleteFashion) {
		this.deleteFashion = deleteFashion;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public void setHeader(HFPage header) {
		this.header = header;
	}
	
	public void setRootPageId(PageId rootPageId) {
		this.rootPageId = rootPageId;
	}
	
	
	
	
	
	
	
	
	public PageId getHeaderPageId() {
		return headerPageId;
	}

	public int getKeysize() {
		return keysize;
	}

	public int getKeytype() {
		return keytype;
	}

	public int getDeleteFashion() {
		return deleteFashion;
	}

	public String getTreeName() {
		return treeName;
	}

	public Page getHeader() {
		return header;
	}
	public PageId getRootPageId() {
		return rootPageId;
	}
}
