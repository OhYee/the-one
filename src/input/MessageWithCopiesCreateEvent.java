/*
	修改自MessageCreateEvent.java
	从设置文件读取不同事件的初始副本数,然后传给MessageWithCopiesEventGenerator.java
	使用SprayAndWaitRouterWithDiffCopies.java进行更新
 */
package input;

import core.DTNHost;
import core.Message;
import core.World;

/**
 * External event for creating a message.
 */
public class MessageWithCopiesCreateEvent extends MessageEvent {
    private int size;
	private int responseSize;
    protected int initCopies;

    //add
	public static final String SPRAYANDWAITWITHDIFFCOPIES_NS = "SprayAndWaitRouterWithDiffCopies";
	public static final String MSG_COUNT_PROPERTY_LOCAL = SPRAYANDWAITWITHDIFFCOPIES_NS + "." +
		"copies";

    
    /**
	 * Creates a message creation event with a optional response request
	 * @param from The creator of the message
	 * @param to Where the message is destined to
	 * @param id ID of the message
	 * @param size Size of the message
	 * @param responseSize Size of the requested response message or 0 if
	 * no response is requested
	 * @param time Time, when the message is created
	 */
	public MessageWithCopiesCreateEvent(int from, int to, String id, int size,
			int responseSize, double time,int initCopies) {
		super(from,to, id,time);
        
        this.size = size;
		this.responseSize = responseSize;

        this.initCopies = initCopies;   //add
	}

	@Override
	public void processEvent(World world) {
		DTNHost to = world.getNodeByAddress(this.toAddr);
		DTNHost from = world.getNodeByAddress(this.fromAddr);

		Message m = new Message(from, to, this.id, this.size);
		m.setResponseSize(this.responseSize);
        
        m.addProperty(MSG_COUNT_PROPERTY_LOCAL, new Integer(initCopies)); //add

		from.createNewMessage(m);
	}


	@Override
	public String toString() {
		return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
		"size:" + size + " CREATE";
	}
}