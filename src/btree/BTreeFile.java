package btree;

import global.*;
import bufmgr.*;
import diskmgr.*;

public class BTreeFile extends IndexFile implements GlobalConst {
	/**
	 * Open an existing index file.
	 * 
	 * @param name
	 *            : name of the index file.
	 */
	public BTreeFile(String name) {

	}

	/**
	 * If index file exists, open it; else create it.
	 * 
	 * @param filename
	 *            : name of the index file.
	 * @param keytype
	 *            : type of the key(String, Integer)
	 * @param keysize
	 *            : maximum size of the key in bytes.
	 * @param delete_fashion
	 *            (always '0' for this assignment) : full delete or naive
	 *            delete. Input parameter. It is either
	 *            DeleteFashion.NAIVE_DELETE or DeleteFashion.FULL_DELETE.
	 */
	public BTreeFile(String filename, int keytype, int keysize,
			int delete_fashion) {

	}

	@Override
	public void insert(KeyClass data, RID rid) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean Delete(KeyClass data, RID rid) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Close the B+ tree file. Unpin header page.
	 */
	public void close() {

	}

	/**
	 * Destroy entire B+ tree file.
	 */
	public void destroyFile() {

	}

	/**
	 * create a scan with given keys Cases: (1) lo_key = null, hi_key = null
	 * scan the whole index (2) lo_key = null, hi_key!= null range scan from min
	 * to the hi_key (3) lo_key!= null, hi_key = null range scan from the lo_key
	 * to max (4) lo_key!= null, hi_key!= null, lo_key = hi_key exact match (
	 * might not unique) (5) lo_key!= null, hi_key!= null, lo_key < hi_key range
	 * scan from lo_key to hi_key
	 * 
	 * @param lo_key: lo_key - the key where we begin scanning. Input parameter.
	 * @param hi_key: the key where we stop scanning. Input parameter.
	 * @return
	 */
	public BTFileScan new_scan(KeyClass lo_key, KeyClass hi_key) {
		
		return null;
	}
}
