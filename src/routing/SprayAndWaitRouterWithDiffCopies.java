/*
	修改自SprayAndWaitRouter.java
	修改了读取的设置文件的命名空间名称
 	删除了在这里增加副本数的代码（85行）
 */
package routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;

/**
 * Implementation of Spray and wait router as depicted in
 * <I>Spray and Wait: An Efficient Routing Scheme for Intermittently
 * Connected Mobile Networks</I> by Thrasyvoulos Spyropoulus et al.
 *
 */
public class SprayAndWaitRouterWithDiffCopies extends ActiveRouter {
	/* Settings */
	public static final String SPRAYANDWAITWITHDIFFCOPIES_NS = "SprayAndWaitRouterWithDiffCopies";
	public static final String BUFFER = "buffer";
	public static final String BINARY_MODE = "binaryMode";

	public static final String SETTINGEVENTS_NAME = "Events";
	public static final String SETTINGEVENTS_NROF = "nrof";
	public static final String SETTINGEVENTS_PREFIX = "prefix";
	public static final String SETTINGEVENTS_DELPRO = "DelPro";
	public static final String NROF_COPIES = "initCopies";

	/** Message property key */
	public static final String MSG_COUNT_PROPERTY_LOCAL = SPRAYANDWAITWITHDIFFCOPIES_NS + "." + "copies";

	
	protected boolean isBinary;
	protected boolean isMyBuffer;

	protected Map<String, Integer> Pro;
	protected Map<String, Integer> initCopies;

	public SprayAndWaitRouterWithDiffCopies(Settings s) {
		super(s);
		Settings snwSettings = new Settings(SPRAYANDWAITWITHDIFFCOPIES_NS);
		this.isBinary = snwSettings.getBoolean(BINARY_MODE);
		this.isMyBuffer = snwSettings.getBoolean(BUFFER);

		/* 读入事件数据 */
		Settings settings = new Settings(SETTINGEVENTS_NAME);
		int Number = settings.getInt(SETTINGEVENTS_NROF);
		this.Pro = new HashMap<String, Integer>();
		this.initCopies = new HashMap<String,Integer>();

		for (int i = 1; i <= Number; i++) {
			Settings settings2 = new Settings(SETTINGEVENTS_NAME + i);
			String name = settings2.getSetting(SETTINGEVENTS_PREFIX);
			int DelPro = settings2.getInt(SETTINGEVENTS_DELPRO);
			int Copies = settings2.getInt(NROF_COPIES);

			this.Pro.put(name, DelPro);
			this.initCopies.put(name,Copies);
		}
	}

	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected SprayAndWaitRouterWithDiffCopies(SprayAndWaitRouterWithDiffCopies r) {
		super(r);
		this.isBinary = r.isBinary;
		this.isMyBuffer = r.isMyBuffer;
		this.Pro = r.Pro;
		this.initCopies = r.initCopies;
	}

	@Override
	public int receiveMessage(Message m, DTNHost from) {
		return super.receiveMessage(m, from);
	}

	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message msg = super.messageTransferred(id, from);
		Integer nrofCopies = (Integer) msg.getProperty(MSG_COUNT_PROPERTY_LOCAL);

		assert nrofCopies != null : "Not a SnW message: " + msg;

		if (isBinary) {
			/* in binary S'n'W the receiving node gets floor(n/2) copies */
			nrofCopies = (int) Math.floor(nrofCopies / 2.0);
		} else {
			/* in standard S'n'W the receiving node gets only single copy */
			nrofCopies = 1;
		}

		msg.updateProperty(MSG_COUNT_PROPERTY_LOCAL, nrofCopies);

		//System.out.println(msg.getId() + ": " + msg.getProperty(MSG_COUNT_PROPERTY_LOCAL));
		return msg;
	}

	@Override
	public boolean createNewMessage(Message msg) {
		makeRoomForNewMessage(msg.getSize());

		msg.setTtl(this.msgTtl);

		msg.addProperty(MSG_COUNT_PROPERTY_LOCAL, new Integer(initCopies.get(noNumber(msg.getId()))));
		addToMessages(msg, true);

		return true;
	}

	@Override
	public void update() {
		super.update();
		if (!canStartTransfer() || isTransferring()) {
			return; // nothing to transfer or is currently transferring
		}

		/* try messages that could be delivered to final recipient */
		if (exchangeDeliverableMessages() != null) {
			return;
		}

		/* create a list of SAWMessages that have copies left to distribute */
		@SuppressWarnings(value = "unchecked")
		List<Message> copiesLeft = sortByQueueMode(getMessagesWithCopiesLeft());

		if (copiesLeft.size() > 0) {
			/* try to send those messages */
			this.tryMessagesToConnections(copiesLeft, getConnections());
		}

	}

	/**
	 * Creates and returns a list of messages this router is currently
	 * carrying and still has copies left to distribute (nrof copies > 1).
	 * @return A list of messages that have copies left
	 */
	protected List<Message> getMessagesWithCopiesLeft() {
		List<Message> list = new ArrayList<Message>();

		for (Message m : getMessageCollection()) {
			Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY_LOCAL);
			assert nrofCopies != null : "SnW message " + m + " didn't have " + "nrof copies property!";
			if (nrofCopies > 1) {
				list.add(m);
			}
		}

		return list;
	}

	/**
	 * Called just before a transfer is finalized (by
	 * {@link ActiveRouter#update()}).
	 * Reduces the number of copies we have left for a message.
	 * In binary Spray and Wait, sending host is left with floor(n/2) copies,
	 * but in standard mode, nrof copies left is reduced by one.
	 */
	@Override
	protected void transferDone(Connection con) {
		Integer nrofCopies;
		String msgId = con.getMessage().getId();
		/* get this router's copy of the message */
		Message msg = getMessage(msgId);

		if (msg == null) { // message has been dropped from the buffer after..
			return; // ..start of transfer -> no need to reduce amount of copies
		}

		/* reduce the amount of copies left */
		nrofCopies = (Integer) msg.getProperty(MSG_COUNT_PROPERTY_LOCAL);
		if (isBinary) {
			/* in binary S'n'W the sending node keeps ceil(n/2) copies */
			nrofCopies = (int) Math.ceil(nrofCopies / 2.0);
		} else {
			nrofCopies--;
		}
		msg.updateProperty(MSG_COUNT_PROPERTY_LOCAL, nrofCopies);

		//System.out.println(msg.getId() + ": " + msg.getProperty(MSG_COUNT_PROPERTY_LOCAL));
	}

	@Override
	public SprayAndWaitRouterWithDiffCopies replicate() {
		return new SprayAndWaitRouterWithDiffCopies(this);
	}

	@Override
	protected Message getNextMessageToRemove(boolean excludeMsgBeingSent) {
		Collection<Message> messages = this.getMessageCollection();
		if (this.isMyBuffer) {
			List<Message> list = new ArrayList<Message>();
			for (Message m : messages) {
				if (excludeMsgBeingSent && isSending(m.getId())) {
					continue; // skip the message(s) that router is sending
				}
				list.add(m);
			}
			return getDeleteWhich(list);
		} else {
			Message oldest = null;
			for (Message m : messages) {
				if (excludeMsgBeingSent && isSending(m.getId())) {
					continue; // skip the message(s) that router is sending
				}
				if (oldest == null) {
					oldest = m;
				} else if (oldest.getReceiveTime() > m.getReceiveTime()) {
					oldest = m;
				}
			}
			return oldest;
		}
	}

	String noNumber(String str) {
		return str.replaceAll("\\d+", "");
	}

	protected Message getDeleteWhich(List<Message> list) {
		List<Message> list2 = new ArrayList<Message>();
		for (Message msg : list) {
			for (int i = 0; i < Pro.get(noNumber(msg.getId())); i++) {
				list2.add(msg);
			}
		}
		if (list2.isEmpty())
			return null;
		else
			return list2.get(rand(list2.size()));
	}

	protected int rand(int Max) {
		int bit = 1;
		int t = Max;
		while (t > 0) {
			t /= 10;
			bit *= 10;
		}
		int ans = (int) (Math.random() * bit) % bit;
		while (ans >= Max) {
			ans = (int) (Math.random() * bit) % bit;
		}
		return ans;
	}
}
