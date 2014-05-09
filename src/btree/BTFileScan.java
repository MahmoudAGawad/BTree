package btree;

import java.io.IOException;

import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.PageId;
import global.RID;
import heap.HFPage;

public class BTFileScan extends IndexFileScan {

	private KeyClass upperKey, lowerKey;
	private BTreeFile bTreeFile;
	private BTLeafPage curPage;
	private KeyDataEntry curDataEntry;
	private KeyDataEntry prevDataEntry = null;

	public BTFileScan(BTreeFile bFile) {
		bTreeFile = bFile;
		lowerKey = upperKey = null;
		curPage = null;
		curDataEntry = prevDataEntry = null;
		
	}

	public BTFileScan(BTreeFile bFile, KeyClass lower, KeyClass upper) {
		upperKey = upper;
		lowerKey = lower;
		bTreeFile = bFile;
		curPage = getFirstLeafPage();
		curDataEntry = getFirstDataEntry();
	}

	private BTLeafPage getFirstLeafPage() {
		try {
			return search(bTreeFile.getHeaderPage().getRootPageId());
		} catch (ReplacerException | HashOperationException
				| PageUnpinnedException | InvalidFrameNumberException
				| PageNotReadException | BufferPoolExceededException
				| PagePinnedException | BufMgrException
				| HashEntryNotFoundException | KeyNotMatchException
				| IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BTLeafPage search(PageId curPageId) throws ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			IOException, HashEntryNotFoundException, KeyNotMatchException {

		HFPage curNode;
		bTreeFile.bufferManager.pinPage(curPageId, curNode = new HFPage(),
				false);
		bTreeFile.bufferManager.unpinPage(curPageId, false);

		if (curNode.getType() == NodeType.LEAF) {
			return (BTLeafPage) curNode;
		} else {

			BTIndexPage thisIndexPage = (BTIndexPage) curNode;
			RID rid = new RID();
			KeyDataEntry curDataEntry = thisIndexPage.getFirst(rid);

			if (lowerKey == null
					|| BT.keyCompare(lowerKey, curDataEntry.key) < 0) {
				return search(((BTIndexPage) curNode).getLeftLink());
			} else {

				PageId pgid = rid.pageNo;
				curDataEntry = thisIndexPage.getNext(rid);

				while (rid != null) {
					if (BT.keyCompare(lowerKey, curDataEntry.key) < 0) {
						return search(pgid);
					}
				}
			}
		}

		return null;
	}

	private KeyDataEntry getFirstDataEntry() {

		RID rid = new RID();
		KeyDataEntry cur = curPage.getFirst(rid);
		try {
			while (BT.keyCompare(lowerKey, cur.key) != 0) {
				cur = curPage.getNext(rid);
			}
		} catch (KeyNotMatchException e) {
			e.printStackTrace();
		}
		return cur;
	}

	@Override
	public KeyDataEntry get_next() {

		if (curDataEntry == null) {
			return null;
		}

		KeyDataEntry ret = prevDataEntry = curDataEntry;
		RID rid = new RID();
		curDataEntry = curPage.getNext(rid);
		if (curDataEntry == null) {
			try {
				if (curPage.getNextPage() != null) {
					try {
						bTreeFile.bufferManager.unpinPage(curPage.getCurPage(),
								false);
					} catch (ReplacerException | PageUnpinnedException
							| HashEntryNotFoundException
							| InvalidFrameNumberException e) {
						e.printStackTrace();
					}

					try {
						bTreeFile.bufferManager.pinPage(curPage.getNextPage(),
								new HFPage(), false);
					} catch (ReplacerException | HashOperationException
							| PageUnpinnedException
							| InvalidFrameNumberException
							| PageNotReadException
							| BufferPoolExceededException | PagePinnedException
							| BufMgrException e) {
						e.printStackTrace();
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (BT.keyCompare(upperKey, curDataEntry.key) > 0) {
				curDataEntry = null;
			}
		} catch (KeyNotMatchException e) {
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public void delete_current() {
		if(prevDataEntry==null){
			// do nothing, or throw exception? because the get_next never called yet!!
		}else{
			curPage.delEntry(prevDataEntry);
		}

	}

	@Override
	public int keysize() {
		return bTreeFile.getHeaderPage().getKeysize();
	}

	public void DestroyBTreeFileScan() throws ReplacerException,
			PageUnpinnedException, HashEntryNotFoundException,
			InvalidFrameNumberException, IOException {

		bTreeFile.bufferManager.unpinPage(curPage.getCurPage(), false);
		curDataEntry = null;
	}
}
