package btree;

import java.io.IOException;

import global.*;
import heap.HFPage;
import heap.InvalidSlotNumberException;
import heap.Tuple;
import bufmgr.*;
import diskmgr.*;

public class BTreeFile extends IndexFile implements GlobalConst {

	BufMgr bufferManager = SystemDefs.JavabaseBM;
	DB diskManager = SystemDefs.JavabaseDB;

	private PageId headerPageId, rootPageId;
	private BTreeHeaderPage headerPage;
	private boolean writeInfoo = false;

	/**
	 * Open an existing index file.
	 * 
	 * @param name
	 *            : name of the index file.
	 * @throws IOException
	 * @throws DiskMgrException
	 * @throws InvalidPageNumberException
	 * @throws FileIOException
	 * @throws BufMgrException
	 * @throws PagePinnedException
	 * @throws BufferPoolExceededException
	 * @throws PageNotReadException
	 * @throws InvalidFrameNumberException
	 * @throws PageUnpinnedException
	 * @throws HashOperationException
	 * @throws ReplacerException
	 * @throws ConvertException
	 * @throws NodeNotMatchException
	 * @throws KeyNotMatchException
	 * @throws InvalidSlotNumberException
	 */
	public BTreeFile(String name) throws FileIOException,
			InvalidPageNumberException, DiskMgrException, IOException,
			ReplacerException, HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {

		headerPageId = diskManager.get_file_entry(name);
		HFPage header = new HFPage();
		bufferManager.pinPage(headerPageId, header, true);

		readInfo(header, name);
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
	 * @throws IOException
	 * @throws DiskMgrException
	 * @throws FileIOException
	 * @throws OutOfSpaceException
	 * @throws DuplicateEntryException
	 * @throws InvalidRunSizeException
	 * @throws InvalidPageNumberException
	 * @throws FileNameTooLongException
	 * @throws BufMgrException
	 * @throws PagePinnedException
	 * @throws BufferPoolExceededException
	 * @throws PageNotReadException
	 * @throws InvalidFrameNumberException
	 * @throws PageUnpinnedException
	 * @throws HashOperationException
	 * @throws ReplacerException
	 * @throws ConvertException
	 * @throws NodeNotMatchException
	 * @throws KeyNotMatchException
	 * @throws InvalidSlotNumberException
	 * @throws HashEntryNotFoundException
	 */
	public BTreeFile(String filename, int keytype, int keysize,
			int delete_fashion) throws FileNameTooLongException,
			InvalidPageNumberException, InvalidRunSizeException,
			DuplicateEntryException, OutOfSpaceException, FileIOException,
			DiskMgrException, IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			InvalidSlotNumberException, HashEntryNotFoundException {

		try {
			headerPageId = diskManager.get_file_entry(filename);
			HFPage header = new HFPage();

			bufferManager.pinPage(headerPageId, header, true);
			readInfo(header, filename);
			return;
		} catch (FileIOException | InvalidPageNumberException
				| DiskMgrException | IOException e) {
			// file doesn't exist; create it

			HFPage header = new HFPage();
			headerPageId = bufferManager.newPage(header, 1);
			headerPage = new BTreeHeaderPage(keytype, keysize, headerPageId,
					delete_fashion, filename, header, new PageId());

			diskManager.add_file_entry(filename, headerPageId);
			writeInfoo = true;
		}
	}

	private void readInfo(HFPage header, String name)
			throws InvalidSlotNumberException, IOException,
			KeyNotMatchException, ConvertException {
		RID keytypeRid, keysizeRid, deleteFashionRid, rootRid;
		try {
			keytypeRid = header.firstRecord();
			keysizeRid = header.nextRecord(keytypeRid);
			deleteFashionRid = header.nextRecord(keysizeRid);
			rootRid = header.nextRecord(deleteFashionRid);
		} catch (Exception e) {
			System.out.println("Can't Load header page information");
			return;
		}

		Tuple keytypeTuple = header.getRecord(keytypeRid);
		Tuple keysizeTuple = header.getRecord(keysizeRid);
		Tuple deleteFashionTuple = header.getRecord(deleteFashionRid);
		Tuple rootTuple = header.getRecord(rootRid);

		KeyDataEntry keytypeEntry = null, keysizeEntry = null, deleteFashionEntry = null, rootEntry = null;

		try {
			keytypeEntry = BT.getEntryFromBytes(
					keytypeTuple.getTupleByteArray(), keytypeTuple.getOffset(),
					keytypeTuple.getLength(), AttrType.attrInteger,
					NodeType.INDEX);

			keysizeEntry = BT.getEntryFromBytes(
					keysizeTuple.getTupleByteArray(), keysizeTuple.getOffset(),
					keysizeTuple.getLength(), AttrType.attrInteger,
					NodeType.INDEX);

			deleteFashionEntry = BT.getEntryFromBytes(
					deleteFashionTuple.getTupleByteArray(),
					deleteFashionTuple.getOffset(),
					deleteFashionTuple.getLength(), AttrType.attrInteger,
					NodeType.INDEX);

			rootEntry = BT.getEntryFromBytes(rootTuple.getTupleByteArray(),
					rootTuple.getOffset(), rootTuple.getLength(),
					AttrType.attrInteger, NodeType.INDEX);

		} catch (NodeNotMatchException e) {
			// the node type is not important here.[do nothing]
		}

		headerPage = new BTreeHeaderPage(
				((IntegerKey) keytypeEntry.key).getKey(),
				((IntegerKey) keysizeEntry.key).getKey(), headerPageId,
				((IntegerKey) deleteFashionEntry.key).getKey(), name, header,
				new PageId(((IntegerKey) rootEntry.key).getKey()));
	}

	private void writeInfo() throws KeyNotMatchException,
			NodeNotMatchException, ConvertException, IOException {

		HFPage header = new HFPage();

		KeyDataEntry keytypeEntry = new KeyDataEntry(headerPage.getKeytype(),
				headerPageId);
		KeyDataEntry keysizeEntry = new KeyDataEntry(headerPage.getKeysize(),
				headerPageId);
		KeyDataEntry deleteFashionEntry = new KeyDataEntry(
				headerPage.getDeleteFashion(), headerPageId);
		KeyDataEntry rootEntry = new KeyDataEntry(
				headerPage.getRootPageId().pid, headerPageId);

		header.insertRecord(BT.getBytesFromEntry(keytypeEntry));
		header.insertRecord(BT.getBytesFromEntry(keysizeEntry));
		header.insertRecord(BT.getBytesFromEntry(deleteFashionEntry));
		header.insertRecord(BT.getBytesFromEntry(rootEntry));
	}

	public BTreeHeaderPage getHeaderPage() {
		return headerPage;
	}

	@Override
	public void insert(KeyClass data, RID rid) {
		try {
			insert(rootPageId, data, rid);
		} catch (KeyNotMatchException | NodeNotMatchException
				| BufferPoolExceededException | HashOperationException
				| ReplacerException | HashEntryNotFoundException
				| InvalidFrameNumberException | PagePinnedException
				| PageUnpinnedException | PageNotReadException
				| BufMgrException | DiskMgrException | ConstructPageException
				| InvalidSlotNumberException | InsertRecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private KeyDataEntry newChiledEntry;

	private void insert(PageId curNodePageId, KeyClass data, RID rid)
			throws KeyNotMatchException, NodeNotMatchException, IOException,
			BufferPoolExceededException, HashOperationException,
			ReplacerException, HashEntryNotFoundException,
			InvalidFrameNumberException, PagePinnedException,
			PageUnpinnedException, PageNotReadException, BufMgrException,
			DiskMgrException, ConstructPageException,
			InvalidSlotNumberException, InsertRecException {

		HFPage curNode = new HFPage();
		// How to know if the page is IndexPage or LeafPage??????
		bufferManager.pinPage(curNodePageId, curNode, true);
		boolean dirtyBit = false;

		boolean type = false; // default is string
		if (data instanceof IntegerKey) {
			type = true;
		}

		if (curNode.getType() == NodeType.LEAF) {

			BTLeafPage thisLeafPage = (BTLeafPage) curNode;

			if (curNode.available_space() < BT.getKeyDataLength(data,
					NodeType.LEAF)) {
				// no space left; split it

				int entriesCount = curNode.getSlotCnt() / 2;

				BTLeafPage newPage = new BTLeafPage(type ? AttrType.attrInteger
						: AttrType.attrString);

				KeyDataEntry curDataEntry = null;
				RID curRid = new RID();

				curDataEntry = thisLeafPage.getFirst(curRid);
				while (--entriesCount > 0) {
					curDataEntry = thisLeafPage.getNext(curRid);
				}

				while (curRid != null && curDataEntry != null) {
					curDataEntry = thisLeafPage.getNext(curRid);
					newPage.insertRecord(curDataEntry.key, curRid);
					thisLeafPage.delEntry(curDataEntry);
				}

				newPage.insertRecord(data, rid);

				newPage.setNextPage(thisLeafPage.getNextPage());
				newPage.setPrevPage(thisLeafPage.getCurPage());
				thisLeafPage.setNextPage(newPage.getCurPage());

				RID copyUpRid = new RID();
				KeyDataEntry firstEntry = newPage.getFirst(copyUpRid);

				newChiledEntry = new KeyDataEntry(firstEntry.key,
						((IndexData) firstEntry.data).getData());

				bufferManager.unpinPage(newPage.getCurPage(), true);
				dirtyBit = true;
			} else {
				thisLeafPage.insertRecord(data, rid);
				newChiledEntry = null;
			}

		} else {

			BTIndexPage thisIndexPage = (BTIndexPage) curNode;

			RID curRid = new RID();
			KeyDataEntry curIndexEntry = thisIndexPage.getFirst(curRid);
			PageId rightPointerId = null;

			if (BT.keyCompare(data, curIndexEntry.key) < 0) {
				rightPointerId = thisIndexPage.getLeftLink();

			} else {
				KeyDataEntry prevIndexEntry = curIndexEntry;
				curIndexEntry = thisIndexPage.getNext(curRid);
				while (curIndexEntry != null) {
					if (BT.keyCompare(data, curIndexEntry.key) < 0) {
						rightPointerId = ((IndexData) prevIndexEntry.data)
								.getData();
						break;
					}

					prevIndexEntry = curIndexEntry;
					curIndexEntry = thisIndexPage.getNext(curRid);
				}

			}

			if (rightPointerId != null) {
				insert(rightPointerId, data, rid);
			}

			if (rightPointerId != null && newChiledEntry != null) {
				if (thisIndexPage.available_space() < BT.getKeyDataLength(
						newChiledEntry.key, NodeType.INDEX)) {
					// no space left; split
					BTIndexPage newIndexPage = new BTIndexPage(NodeType.INDEX);

					int entriesCount = thisIndexPage.getSlotCnt() / 2;

					curIndexEntry = thisIndexPage.getFirst(curRid);
					while (--entriesCount > 0) {
						curIndexEntry = thisIndexPage.getNext(curRid);
					}

					while (curRid != null) {
						curIndexEntry = thisIndexPage.getNext(curRid);
						newIndexPage.insertRecord(curIndexEntry);
						thisIndexPage.deleteRecord(curRid);
					}
					newIndexPage.insertRecord(newChiledEntry);

					newChiledEntry = newIndexPage.getFirst(curRid = new RID());// push
																				// up
					newIndexPage.deleteRecord(curRid);

					bufferManager.unpinPage(newIndexPage.getCurPage(), true);
					
					newIndexPage.setLeftLink(((IndexData) newChiledEntry.data)
							.getData());
					
					
					// adjust right pointer if the pushed up entry
					((IndexData)newChiledEntry.data).setData(newIndexPage.getCurPage());

					if (thisIndexPage.getCurPage().pid == rootPageId.pid) {
						HFPage root = new HFPage();
						rootPageId = bufferManager.newPage(root, 1);
						BTIndexPage newRoot = new BTIndexPage(rootPageId,
								NodeType.INDEX);
						newRoot.insertRecord(newChiledEntry);
						bufferManager.unpinPage(rootPageId, true);
						headerPage.setRootPageId(rootPageId);
						newRoot.setLeftLink(thisIndexPage.getCurPage());
						newChiledEntry = null;
					}
				} else {
					thisIndexPage.insertRecord(newChiledEntry);
					newChiledEntry = null;
				}

			}

		}

		bufferManager.unpinPage(curNodePageId, dirtyBit);
	}

	@Override
	public boolean Delete(KeyClass data, RID rid) {
		try {
			delete(rootPageId, data, rid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void delete(PageId curNodeId, KeyClass data, RID rid)
			throws ReplacerException, HashOperationException,
			PageUnpinnedException, InvalidFrameNumberException,
			PageNotReadException, BufferPoolExceededException,
			PagePinnedException, BufMgrException, IOException,
			HashEntryNotFoundException, KeyNotMatchException {

		HFPage curNode = new HFPage();
		bufferManager.pinPage(curNodeId, curNode, false);
		boolean dirtyBit = false;

		if (curNode.getType() == NodeType.LEAF) {

			BTLeafPage thisLeafPage = (BTLeafPage) curNode;
			dirtyBit = true;

			RID curRid = new RID();
			KeyDataEntry curDataEntry = thisLeafPage.getFirst(curRid);
			if (BT.keyCompare(curDataEntry.key, data) == 0) {
				thisLeafPage.delEntry(curDataEntry);
			} else
				while (curRid != null) {
					curDataEntry = thisLeafPage.getNext(curRid);
					if (BT.keyCompare(curDataEntry.key, data) == 0) {
						thisLeafPage.delEntry(curDataEntry);
					}
				}
		} else {

			BTIndexPage thisIndexPage = (BTIndexPage) curNode;
			dirtyBit = false;
			RID curRid = new RID();
			KeyDataEntry curDataEntry = thisIndexPage.getFirst(curRid);

			if (BT.keyCompare(data, curDataEntry.key) < 0) {
				delete(thisIndexPage.getLeftLink(), data, rid);
			} else {

				PageId prevD = curRid.pageNo;
				while (curRid != null) {
					curDataEntry = thisIndexPage.getNext(curRid);
					if (BT.keyCompare(data, curDataEntry.key) < 0) {
						delete(prevD, data, curRid);
						break;
					}
				}

			}
		}

		bufferManager.unpinPage(curNodeId, dirtyBit);
	}

	/**
	 * Close the B+ tree file. Unpin header page.
	 * 
	 * @throws InvalidFrameNumberException
	 * @throws HashEntryNotFoundException
	 * @throws PageUnpinnedException
	 * @throws ReplacerException
	 * @throws IOException
	 * @throws ConvertException
	 * @throws NodeNotMatchException
	 * @throws KeyNotMatchException
	 */
	public void close() throws ReplacerException, PageUnpinnedException,
			HashEntryNotFoundException, InvalidFrameNumberException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			IOException {

		writeInfo();
		bufferManager.unpinPage(headerPageId, true);
	}

	/**
	 * Destroy entire B+ tree file.
	 */
	public void destroyFile() {
		try {
			destroy(rootPageId);
		} catch (ReplacerException | HashOperationException
				| PageUnpinnedException | InvalidFrameNumberException
				| PageNotReadException | BufferPoolExceededException
				| PagePinnedException | BufMgrException
				| HashEntryNotFoundException | InvalidBufferException
				| DiskMgrException | IOException e) {
			e.printStackTrace();
		}
	}

	private void destroy(PageId curNodeId) throws ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			IOException, HashEntryNotFoundException, InvalidBufferException,
			DiskMgrException {

		HFPage curNode = null;
		bufferManager.pinPage(curNodeId, curNode, false);
		bufferManager.unpinPage(curNodeId, false);

		if (curNode.getType() == NodeType.INDEX) {

			BTIndexPage thisIndexPage = (BTIndexPage) curNode;

			destroy(thisIndexPage.getLeftLink());

			RID rid = new RID();
			KeyDataEntry curDataEntry = thisIndexPage.getFirst(rid);
			destroy(((IndexData) curDataEntry.data).getData());
			while (rid != null) {
				curDataEntry = thisIndexPage.getNext(rid);
				destroy(((IndexData) curDataEntry.data).getData());
			}

			bufferManager.freePage(curNodeId);

		}

	}

	/**
	 * create a scan with given keys Cases: (1) lo_key = null, hi_key = null
	 * scan the whole index (2) lo_key = null, hi_key!= null range scan from min
	 * to the hi_key (3) lo_key!= null, hi_key = null range scan from the lo_key
	 * to max (4) lo_key!= null, hi_key!= null, lo_key = hi_key exact match (
	 * might not unique) (5) lo_key!= null, hi_key!= null, lo_key < hi_key range
	 * scan from lo_key to hi_key
	 * 
	 * @param lo_key
	 *            : lo_key - the key where we begin scanning. Input parameter.
	 * @param hi_key
	 *            : the key where we stop scanning. Input parameter.
	 * @return
	 */
	public BTFileScan new_scan(KeyClass lo_key, KeyClass hi_key) {

		return new BTFileScan(this, lo_key, hi_key);
	}

	public void traceFilename(String string) {

	}
}
